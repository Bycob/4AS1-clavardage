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
import org.ljsn.clavardage.network.PacketGoodbye;
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
	/** refused is set to true if user has picked a bad username */
	private boolean refused = false;
	/** List of active users */
	private UserList userList;
	private User currentUser;
	/** Hash table of conversations associated to users */
	private HashMap<User, Conversation> conversations;

	private SessionListener sessionListener;

	private UDPMessager udpMessager;
	private TCPReceiver tcpReceiver;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Future<Boolean> connectionTimeout;

	private class NetworkListener implements PacketListener {
		@Override
		public void onPacket(InetAddress address, Packet packet) { synchronized (Session.this) {
			
			if (refused) {
				return;
			}
			
			logger.log(Level.INFO, "Received packet " + packet.getClass().getSimpleName() + " from " + address.getHostAddress());
			boolean isLocalAddress = false;

			if (packet instanceof PacketHello) {
				PacketHello hellopkt = (PacketHello) packet;
				// generate new user instance using pseudonym
				User newUser = new User(hellopkt.getPseudo(), hellopkt.getTcpPort(), address.getHostAddress());

				// verifies if pseudonym is taken
				// TODO re-think how it should work when we accept loopback packets
				if (!userList.hasPseudo(newUser.getPseudo()) && !isLocalAddress) {
					// send hello back
					UserList sentUserList = new UserList(userList);
					sentUserList.addUser(currentUser);
					
					PacketHelloBack helloBack = new PacketHelloBack(sentUserList);
					sendPacket(newUser, helloBack);
					
					// add user after sending the packet
					userList.addUser(newUser);
					logger.log(Level.INFO, "Added new user " + newUser.getPseudo() + " to UserList");
					
					// update ui
					sessionListener.onUserListChange();
				} else {
					PacketHelloBack helloBack = PacketHelloBack.createPseudoAlreadyUsedPacket();
					sendPacket(newUser, helloBack);
					
					logger.log(Level.INFO, "Refused user " + newUser.getPseudo() + " because they have a bad pseudo.");
				}
			} else if (packet instanceof PacketHelloBack) {
				PacketHelloBack hellobackpkt = (PacketHelloBack) packet;
				connectionTimeout.cancel(true);
				
				if (hellobackpkt.pseudoUsed()) {
					logger.warning("Pseudo is already taken");
					refused = true;
					sessionListener.onConnectionFailed(new Exception("Pseudo already in use"));
				}
				else {
					userList.addUserList(hellobackpkt.getActiveUsers());
					logger.log(Level.INFO, "Updated user list");
					
					// replace sender address, or discard it if sender is already in the list
					User sender = userList.getByIpAddress("me");
					
					if (sender != null) {
						if (userList.getByIpAddress(address.getHostAddress()) != null) {
							userList.removeUser(sender);
						}
						else {
							sender.setIpAddr(address.getHostAddress());
						}
					}
					
					// stop timeout and update UI
					sessionListener.onConnectionSuccess();
				}
			} else if (packet instanceof PacketGoodbye) {
				// get user ip address and pseudo
				String exitingUserPseudo = ((PacketGoodbye) packet).getPseudo();
				String exitingUserIP = address.getHostAddress();
				
				// if the user's identity is valid, remove the user from userList
				User exitingUser = userList.pseudoMatchesIP(exitingUserPseudo, exitingUserIP);
				if ( exitingUser != null) {
					userList.removeUser(exitingUser);										
				}
				
				// update userlist on gui
				sessionListener.onUserListChange();
				
			} else if (packet instanceof PacketMessage) {
				PacketMessage messagePkt = (PacketMessage) packet;
				User u = userList.getByIpAddress(address.getHostAddress());
				
				if (u != null) {
					Conversation conv = getConversation(u);
					conv.addMessage(messagePkt.getMessage());
					
					// update UI
					sessionListener.onMessageReceived(u);
				}
				else {
					logger.warning("Message sender unkown");
				}
			} else {
				// not hello or helloback so it's a message for a conversation 
				
			}
		}}
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
			this.currentUser = new User(pseudo, tcpReceiver.getPort(), "me");
			this.udpMessager.multicast(new PacketHello(pseudo, this.tcpReceiver.getPort()));
			this.connectionTimeout = executor.submit(new Callable<Boolean>() {
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

	public synchronized void destroy() throws IOException {
		IOException caught = null;
		
		// send Goodbye packet to notify users of end of activity 
		this.udpMessager.multicast(new PacketGoodbye(this.pseudo));
		
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
	
	public synchronized UserList getUserList() {
		return new UserList(this.userList);
	}
	
	public synchronized User getCurrentUser() {
		return this.currentUser;
	}
	
	/** Returns conversation with a particular user. If the conversation does
	 * not exist then it's created. */
	// TODO synchronize
	public Conversation getConversation(User user) {
		Conversation conv = this.conversations.get(user);
		if (conv == null) {
			conv = new Conversation();
			this.conversations.put(user, conv);
		}
		return conv;
	}
	
	// ACTIONS
	
	public synchronized void changePseudo(String newPseudo) {
		// TODO changePseudo
	}

	public synchronized void sendMessage(User user, String content) {
		Message message = new Message(new Date(), content, this.currentUser);
		getConversation(user).addMessage(message);
		this.sessionListener.onMessageSent(user);
		
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
