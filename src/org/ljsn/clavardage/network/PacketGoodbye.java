package org.ljsn.clavardage.network;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class PacketGoodbye extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -870039571809879736L;
	
	private String pseudo;

	public PacketGoodbye(String pseudo) {
		super(Type.GOODBYE);
		this.pseudo = pseudo;
	}
	
	@Override
	public void writeContent(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.pseudo).append("\n");
		
		ByteBuffer pseudoBuf = StandardCharsets.UTF_8.encode(sb.toString());
		buffer.put(pseudoBuf);
	}

	@Override
	public void readContent(ByteBuffer from) {
		CharBuffer decoded = StandardCharsets.UTF_8.decode(from);
		String[] lines = decoded.toString().split("\n");
		
		// TODO manage errors
		this.pseudo = lines[0];
	}
	
	public String getPseudo() {
		return this.pseudo;
	}
}
