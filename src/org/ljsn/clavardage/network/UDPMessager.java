package org.ljsn.clavardage.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class for sending and receiving UDP messages
 * 
 * This class is used mainly for UDP broadcast and multicast.
 */
public class UDPMessager {
	
	private static Logger logger = Logger.getLogger(UDPMessager.class.getName());
	
	private boolean running;
	private ArrayList<PacketListener> packetListeners = new ArrayList<PacketListener>();
	
	private int port;
	private InetAddress group;
	private MulticastSocket socket;
	
	private Thread receiveThread;
	
	
	public UDPMessager(String multicastAddress, int port) throws IOException {
		this.port = port;
		
		try {
			this.socket = new MulticastSocket(port);
			
			this.socket.setLoopbackMode(false);
			this.socket.setReuseAddress(true);
			
			this.group = InetAddress.getByName(multicastAddress);
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
							logger.warning("Socket closed");
						}
						else {
							e.printStackTrace();
						}
						running = false;
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
	
	/** Sends multicast packet to the set multicast address. */
	public void multicast(Packet packet) throws IOException {
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
