package org.ljsn.clavardage.core;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.ljsn.clavardage.network.Packet;
import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.PacketHelloBack;
import org.ljsn.clavardage.network.PacketListener;
import org.ljsn.clavardage.network.PacketMessage;
import org.ljsn.clavardage.network.TCPReceiver;
import org.ljsn.clavardage.network.UDPMessager;

public class Session {
	public static final int PORT_UDP = 52684;
	public static final int PORT_TCP = 1026;

	private String pseudo;
	/** This session has been initialized correctly. Valid pseudo name */
	private boolean valid = false;
	/** List of active users */
	private UserList userList;
	/** Hash table of conversations associated to users */
	private HashMap<User, Conversation> conversations;

	private ArrayList<SessionListener> sessionListener = new ArrayList<SessionListener>();

	private UDPMessager udpMessager;
	private TCPReceiver tcpReceiver;

	private class NetworkListener implements PacketListener {

		@Override
		public void onPacket(InetAddress address, Packet packet) {
			boolean isLocalAddress = address.isAnyLocalAddress() || address.isLoopbackAddress();

			if (packet instanceof PacketHello) {
				PacketHello hellopkt = (PacketHello) packet;
				// generate new user instance using pseudonym
				User u = new User(hellopkt.getPseudo(), hellopkt.getTcpPort(), address.getHostAddress());

				// verifies if pseudonym is taken
				if (!userList.hasUser(u) && !isLocalAddress) {
					userList.addUser(u);
					System.out.println(address.getHostAddress() + " " + hellopkt.getPseudo());
					PacketHelloBack helloBack = new PacketHelloBack(userList);
					sendPacket(u, helloBack);
				} else {
					// exception
				}
			} else if (packet instanceof PacketHelloBack) {
				if (userList.isEmpty()) {
					userList = ((PacketHelloBack) packet).getActiveUsers();
				}
			} else if (packet instanceof PacketMessage) {
				PacketMessage messagePkt = (PacketMessage) packet;
				User u = userList.getByIpAddress(address.getHostAddress());
				Conversation conv = conversations.get(u);
				
				if (conv == null) {
					conv = new Conversation();
					conversations.put(u, conv);
				}
				
				conv.addMessage(messagePkt.getMessage());
			} else {
				// exception
			}
		}
	}

	/**
	 * Create a session.
	 * 
	 * @param pseudo
	 * @throws IllegalArgumentException If the pseudo is not valid (locally).
	 */
	public Session(String pseudo) {
		// check pseudo validity
		if (pseudo.isEmpty()) {
			throw new IllegalArgumentException("Pseudo should be at least one character long");
		}

		this.pseudo = pseudo;

		this.conversations = new HashMap<User, Conversation>();
		this.userList = new UserList();

		try {
			this.udpMessager = new UDPMessager(PORT_UDP);
			this.udpMessager.addPacketListener(new NetworkListener());

			this.udpMessager.broadcast(new PacketHello(pseudo, 1234));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			this.tcpReceiver = new TCPReceiver(PORT_TCP);
			this.tcpReceiver.addPacketListener(new NetworkListener());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addSessionListener(SessionListener l) {
		this.sessionListener.add(l);
	}

	public void changePseudo(String newPseudo) {
		// TODO changePseudo
	}

	public void sendMessage(User user, String content) {
		Message message = new Message(new Date(), content, user);
		PacketMessage messagePkt = new PacketMessage(message);
		sendPacket(user, messagePkt);
	}
	
	
	private void sendPacket(User user, Packet packet) {
		// TODO sendPacket
	}
}
