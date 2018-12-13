package org.ljsn.clavardage.core;

import java.util.HashMap;

public class Session {
	private String pseudo;
	/** This session has been initialized correctly. */
	private boolean valid = false;
	
	private UserList userList;
	private HashMap<User, Conversation> conversations;
	
	
	private class NetworkListener {
		
	}
	
	
	public Session(String pseudo) {
		this.conversations = new HashMap<User, Conversation>();
	}
	
	public void changePseudo(String newPseudo) {
		
	}
	
	public void sendMessage() {
		
	}
}
