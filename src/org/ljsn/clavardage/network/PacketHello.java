package org.ljsn.clavardage.network;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class PacketHello extends Packet {

	private String pseudo;
	private int tcpPort;
	
	public PacketHello() {
		this("", -1);
	}
	
	public PacketHello(String pseudo, int tcpPort) {
		super(Type.HELLO);
		this.pseudo = pseudo;
		this.tcpPort = tcpPort;
	}
	
	@Override
	public void writeContent(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.tcpPort).append("\n");
		sb.append(this.pseudo).append("\n");
		
		ByteBuffer pseudoBuf = StandardCharsets.UTF_8.encode(this.pseudo);
		buffer.put(pseudoBuf);
	}

	@Override
	public void readContent(ByteBuffer from) {
		CharBuffer decoded = StandardCharsets.UTF_8.decode(from);
		String[] lines = decoded.toString().split("\n");
		
		// TODO manage errors
		this.tcpPort = Integer.getInteger(lines[0]);
		this.pseudo = lines[1];
	}
}
