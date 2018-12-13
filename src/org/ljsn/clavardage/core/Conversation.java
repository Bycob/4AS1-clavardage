package org.ljsn.clavardage.core;

import java.util.ArrayList;

public class Conversation {
	private ArrayList<Message> messageList;
	private int version;
	
	public Conversation() {
		this.messageList = new ArrayList<Message>();
		// conversation version is initiated at 0
		this.version = 0;
	}
	
	void addMessage(Message m) {
		// conversation version is updated at each new message
		this.version++;
		this.messageList.add(m);
	}
	
	ArrayList<Message> getMessages() {
		return this.messageList;
	}
	
	int getVersion() {
		return this.version;
	}
}
