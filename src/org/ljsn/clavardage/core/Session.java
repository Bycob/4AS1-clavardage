package org.ljsn.clavardage.core;

import java.util.HashMap;

public class Session {
	private String pseudo;
	private boolean valid = false;
	
	private UserList userList;
	private HashMap<User, Conversation> conversations;
	
	
	private class NetworkListener {
		
	}
	
	
	public Session() {
		this.conversations = new HashMap<User, Conversation>();
	}
	
	public void changePseudo(String newPseudo) {
		
	}
	
	public void sendMessage() {
		
	}
}
