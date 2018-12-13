package org.ljsn.clavardage.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class UDPMessager {
	
	private boolean running;
	private ArrayList<PacketListener> packetListeners;
	
	private int port;
	private DatagramChannel datagramChannel;
	
	private Thread receiveThread;
	
	
	public UDPMessager(int port) throws IOException {
		this.port = port;
		
		try {
			this.datagramChannel = DatagramChannel.open();
			this.datagramChannel.socket().bind(new InetSocketAddress(port));
		} catch (IOException e) {
			throw e;
		}
		
		this.receiveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					DatagramPacket packet = new DatagramPacket(new byte[0xffff], 0xffff);
					try {
						datagramChannel.socket().receive(packet);
						ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());
						Packet.readPacket(buffer);
					}
					catch (IOException e) {
						running = false;
						// TODO manage IOException when receiving packet
						System.err.println("TODO manage exception");
						Thread.dumpStack();
					}
				}
			}
		}, "Receive thread");
		
		this.receiveThread.setDaemon(false);
		this.running = true;
		this.receiveThread.start();
	}
	
	public void addPacketListener(PacketListener l) {
		this.packetListeners.add(l);
	}
	
	public void clearPacketListeners() {
		this.packetListeners.clear();
	}
	
	private ByteBuffer getPacketBuffer(Packet packet) {
		ByteBuffer buffer = ByteBuffer.allocate(0xffff);
		packet.write(buffer);
		buffer.flip();
		return buffer;
	}
	
	public void broadcast(Packet packet) throws IOException {
		this.datagramChannel.send(getPacketBuffer(packet), new InetSocketAddress("255.255.255.255", this.port));
	}
	
	public void stop() {
		this.running = false;
	}
}
