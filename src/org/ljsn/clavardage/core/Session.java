package org.ljsn.clavardage.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ljsn.clavardage.network.Packet;
import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.PacketHelloBack;
import org.ljsn.clavardage.network.PacketListener;
import org.ljsn.clavardage.network.PacketMessage;
import org.ljsn.clavardage.network.TCPReceiver;
import org.ljsn.clavardage.network.TCPSender;
import org.ljsn.clavardage.network.UDPMessager;

public class Session {
	public static final String MULTICAST_ADDRESS = "225.4.5.6";
	public static final int PORT_UDP = 52684;
	
	
	private static Logger logger = Logger.getLogger(Session.class.getName());

	private String pseudo;
	/** This session has been initialized correctly. Valid pseudo name */
	private boolean valid = false;
	/** List of active users */
	private UserList userList;
	/** Hash table of conversations associated to users */
	private HashMap<User, Conversation> conversations;

	private SessionListener sessionListener;

	private UDPMessager udpMessager;
	private TCPReceiver tcpReceiver;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Future<Boolean> connectionTimeout;

	private class NetworkListener implements PacketListener {
		//TODO login automatically after timeout
		//TODO show message if not hello or helloback 
		@Override
		public void onPacket(InetAddress address, Packet packet) {
			boolean isLocalAddress = false;

			if (packet instanceof PacketHello) {
				PacketHello hellopkt = (PacketHello) packet;
				// generate new user instance using pseudonym
				User u = new User(hellopkt.getPseudo(), hellopkt.getTcpPort(), address.getHostAddress());
				logger.log(Level.INFO, "Received packet Hello", hellopkt);

				// verifies if pseudonym is taken
				if (!userList.hasUser(u) && !isLocalAddress) {
					// send hello back
					UserList ul = new UserList(userList);
					ul.addUser(new User(pseudo, tcpReceiver.getPort(), "me"));
					
					PacketHelloBack helloBack = new PacketHelloBack(ul);
					sendPacket(u, helloBack);
					
					// add user after sending the packet
					userList.addUser(u);
					logger.log(Level.INFO, "Added user " + u.getPseudo());
					
					// update ui
					sessionListener.onUserListChange();
				} else {
					// nothing
				}
			} else if (packet instanceof PacketHelloBack) {
				PacketHelloBack hellobackpkt = (PacketHelloBack) packet;
				logger.log(Level.INFO, "Received packet HelloBack", hellobackpkt);
				
				if (hellobackpkt.pseudoUsed()) {
					sessionListener.onConnectionFailed(new Exception("Pseudo already in use"));
				}
				if (userList.isEmpty()) {
					userList = hellobackpkt.getActiveUsers();
					
					// replace sender address
					User sender = userList.getByIpAddress("me");
					if (sender != null) {
						sender.setIpAddr(address.getHostAddress());
						logger.log(Level.INFO, "Changed remote user address");
					}
					
					connectionTimeout.cancel(true);
					sessionListener.onConnectionSuccess();
				}
				
				// TODO update UI
			} else if (packet instanceof PacketMessage) {
				PacketMessage messagePkt = (PacketMessage) packet;
				logger.log(Level.INFO, "Received packet MESSAGE", packet);
				
				User u = userList.getByIpAddress(address.getHostAddress());
				
				if (u != null) {
					Conversation conv = conversations.get(u);
					
					if (conv == null) {
						conv = new Conversation();
						conversations.put(u, conv);
					}
					
					conv.addMessage(messagePkt.getMessage());
					
					// update UI
					sessionListener.onMessageSent(u);
				}
				else {
					System.err.println("Session received message from nobody");
				}
			} else {
				// not hello or helloback so it's a message for a conversation 
				
			}
		}
	}

	/**
	 * Create a session.
	 * 
	 * @param pseudo
	 * @throws IllegalArgumentException If the pseudo is not valid (locally).
	 */
	public Session(String pseudo, SessionListener l) {
		// check pseudo validity
		if (pseudo.isEmpty()) {
			throw new IllegalArgumentException("Pseudo should be at least one character long");
		}
		
		if (l == null) {
			throw new NullPointerException("No session listener found");
		}

		this.pseudo = pseudo;
		this.sessionListener = l;

		this.conversations = new HashMap<User, Conversation>();
		this.userList = new UserList();

		try {
			this.udpMessager = new UDPMessager(MULTICAST_ADDRESS, PORT_UDP);
			this.udpMessager.addPacketListener(new NetworkListener());
		} catch (IOException e) {
			// TODO this breaks the session
			e.printStackTrace();
			
			if (this.udpMessager != null) {
				this.udpMessager.stop();
			}
		}

		try {
			this.tcpReceiver = new TCPReceiver();
			this.tcpReceiver.addPacketListener(new NetworkListener());

		} catch (IOException e) {
			// TODO this breaks the session
			e.printStackTrace();
			
			try {
				if (this.tcpReceiver != null)
					this.tcpReceiver.stop();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		try {
			this.udpMessager.multicast(new PacketHello(pseudo, this.tcpReceiver.getPort()));
			connectionTimeout = executor.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					Thread.sleep(1000);
					if (!connectionTimeout.isCancelled()) {
						sessionListener.onConnectionSuccess();
						return true;
					}
					else {
						return false;
					}
				}
				
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void destroy() throws IOException {
		IOException caught = null;
		
		this.executor.shutdown();
		this.udpMessager.stop();
		
		try {
			this.tcpReceiver.stop();
		}
		catch (IOException e) {
			caught = e;
		}
		
		// TODO close client sockets if some are still open.
		
		if (caught != null) {
			// Before throwing this exception we have made sure to attempt to close everything.
			throw caught;
		}
	}
	
	// GETTERS / SETTERS
	
	public UserList getUserList() {
		return this.userList;
	}
	
	/** Returns conversation with a particular user. If the conversation does
	 * not exist then it's created. */
	public Conversation getConversation(User user) {
		Conversation conv = this.conversations.get(user);
		if (conv == null) {
			conv = new Conversation();
			this.conversations.put(user, conv);
		}
		return conv;
	}
	
	// ACTIONS
	
	public void changePseudo(String newPseudo) {
		// TODO changePseudo
	}

	public void sendMessage(User user, String content) {
		Message message = new Message(new Date(), content, user);
		getConversation(user).addMessage(message);
		
		PacketMessage messagePkt = new PacketMessage(message);
		sendPacket(user, messagePkt);
	}
	
	
	private void sendPacket(User user, Packet packet) {
		try {
			TCPSender ts = new TCPSender(user.getIpAddr(), user.getTcpPort());
			ts.sendPacket(packet);
			ts.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
