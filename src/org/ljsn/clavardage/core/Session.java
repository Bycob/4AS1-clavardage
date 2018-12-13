package org.ljsn.clavardage.core;

import java.net.InetAddress;
import java.util.HashMap;

import org.ljsn.clavardage.network.Packet;
import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.PacketListener;

public class Session {
	public static final int PORT = 1025;
	private String pseudo;
	/** This session has been initialized correctly. Valid pseudo name */
	private boolean valid = false;
	/** List of active users */
	private UserList userList;
	/** Hash table of conversations associated to users */
	private HashMap<User, Conversation> conversations;
	
	private class NetworkListener implements PacketListener {

		@Override
		public void onPacket(InetAddress address, Packet packet) {
			if (packet instanceof PacketHello) {
				PacketHello hellopkt = (PacketHello) packet;
				// generate new user instance using pseudonym
				User u = new User(hellopkt.getPseudo());
				// verifies if pseudonym is taken
				if (userList.getUsers().contains(u)) {
					userList.addUser(u);
				} else {
					// exception
				}
			} else {
				//exception
			}
		}
		
	}
	
	
	public Session(String pseudo) {
		this.conversations = new HashMap<User, Conversation>();
	}
	
	public void changePseudo(String newPseudo) {}
	
	public void sendMessage() {}
	
}
