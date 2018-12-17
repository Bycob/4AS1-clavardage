package org.ljsn.clavardage.network;

import org.ljsn.clavardage.core.Message;

public class PacketMessage extends Packet {
	
	private static final long serialVersionUID = 4939615196698592103L;
	
	private Message messageAdded;

	public PacketMessage() {
		this(null);
	}
	
	public PacketMessage(Message messageAdded) {
		super(Type.MESSAGE);
		
		this.messageAdded = messageAdded;
	}

	public Message getMessage() {
		return this.messageAdded;
	}
}
