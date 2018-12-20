package org.ljsn.clavardage.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ljsn.clavardage.network.Packet;
import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.PacketHelloBack;
import org.ljsn.clavardage.network.PacketListener;
import org.ljsn.clavardage.network.PacketMessage;
import org.ljsn.clavardage.network.TCPReceiver;
import org.ljsn.clavardage.network.TCPSender;
import org.ljsn.clavardage.network.UDPMessager;

public class Session {
	public static final int PORT_UDP = 52684;

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

				// verifies if pseudonym is taken
				if (!userList.hasUser(u) && !isLocalAddress) {
					userList.addUser(u);
					System.out.println(address.getHostAddress() + " " + hellopkt.getPseudo() + " " + hellopkt.getTcpPort());
					
					// send hello back
					UserList ul = new UserList(userList);
					try {
						ul.addUser(new User(pseudo, tcpReceiver.getPort(), InetAddress.getLocalHost().getHostAddress()));
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PacketHelloBack helloBack = new PacketHelloBack(ul);
					sendPacket(u, helloBack);
					
					// update ui
					sessionListener.onUserListChange();
				} else {
					// nothing
				}
			} else if (packet instanceof PacketHelloBack) {
				if (((PacketHelloBack) packet).pseudoUsed()) {
					sessionListener.onConnectionFailed(new Exception("Pseudo already in use"));
				}
				if (userList.isEmpty()) {
					userList = ((PacketHelloBack) packet).getActiveUsers();
					sessionListener.onConnectionSuccess();
				}
				
				// TODO update UI
			} else if (packet instanceof PacketMessage) {
				PacketMessage messagePkt = (PacketMessage) packet;
				User u = userList.getByIpAddress(address.getHostAddress());
				Conversation conv = conversations.get(u);
				
				if (conv == null) {
					conv = new Conversation();
					conversations.put(u, conv);
				}
				
				conv.addMessage(messagePkt.getMessage());
				
				// update UI
				sessionListener.onMessageSent(u);
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
			this.udpMessager = new UDPMessager(PORT_UDP);
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
			this.udpMessager.broadcast(new PacketHello(pseudo, this.tcpReceiver.getPort()));
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
