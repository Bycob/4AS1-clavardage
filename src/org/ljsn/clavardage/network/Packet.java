package org.ljsn.clavardage.network;

import java.nio.ByteBuffer;

public abstract class Packet {
	
	public static Packet readPacket(ByteBuffer from) {
		// TODO check that type value is good, raise exception if there is any problem.
		Type type = Type.values()[from.getInt()];
		Packet p = null;
		try {
			p = type.packetClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new Error("Bad instantiation of packet " + type.packetClass.getName() + " : Missing constructor with no parameters");
		}
		// TODO check exception from this method
		p.readContent(from);
		return p;
	}
	
	public enum Type {
		HELLO(PacketHello.class);
		
		public final Class<? extends Packet> packetClass;
		
		Type(Class<? extends Packet> c) {
			packetClass = c;
		}
	}
	
	
	private final Type type;
	
	protected Packet(Type type) {
		this.type = type;
	}
	
	public void write(ByteBuffer to) {
		to.putInt(this.type.ordinal());
		writeContent(to);
	}
	
	protected abstract void writeContent(ByteBuffer to);
	protected abstract void readContent(ByteBuffer from);
}
