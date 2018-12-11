package org.ljsn.clavardage.network;

import java.util.ArrayList;

public class UDPMessager {
	
	private ArrayList<PacketListener> packetListeners;
	
	public UDPMessager() {
		
	}
	
	public void addPacketListener(PacketListener l) {
		this.packetListeners.add(l);
	}
	
	public void clearPacketListeners() {
		this.packetListeners.clear();
	}
	
	public void broadcast(Packet packet) {
		
	}
	
	
}
