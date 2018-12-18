package org.ljsn.clavardage.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class UDPMessager {
	
	public static final String MULTICAST_ADDRESS = "225.4.5.6";
	
	private boolean running;
	private ArrayList<PacketListener> packetListeners = new ArrayList<PacketListener>();
	
	private int port;
	private InetAddress group;
	private MulticastSocket socket;
	
	private Thread receiveThread;
	
	
	public UDPMessager(int port) throws IOException {
		this.port = port;
		
		try {
			this.socket = new MulticastSocket(port);
			
			// this.socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			
			this.group = InetAddress.getByName(MULTICAST_ADDRESS);
			this.socket.joinGroup(this.group);
		} catch (IOException e) {
			throw e;
		}
		
		
		this.receiveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					DatagramPacket datagram = new DatagramPacket(new byte[0xffff], 0xffff);
					try {
						socket.receive(datagram);
						ByteBuffer buffer = ByteBuffer.wrap(datagram.getData(), datagram.getOffset(), datagram.getLength());
						Packet packet = Packet.readPacket(buffer);
						
						for (PacketListener l : packetListeners) {
							l.onPacket(datagram.getAddress(), packet);
						}
					}
					catch (SocketException e) {
						if ("socket closed".equals(e.getMessage())) {
							System.err.println("Socket closed");
						}
						else {
							e.printStackTrace();
						}
					}
					catch (IOException e) {
						running = false;
						// TODO manage IOException when receiving packet
						System.err.println("TODO manage exception");
						e.printStackTrace();
					}
				}
			}
		}, "UDP Receive thread");
		
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
		ByteBuffer buffer = getPacketBuffer(packet);
		byte[] array = new byte[buffer.remaining()];
		buffer.get(array);
		DatagramPacket datagram = new DatagramPacket(array, array.length, this.group, this.port);
		this.socket.send(datagram);
	}
	
	public void stop() {
		this.running = false;
		socket.close();
	}
}
