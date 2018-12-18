package org.ljsn.clavardage.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class TCPReceiver {


	private class SocketReader {
		public SocketChannel channel;
		public ByteBuffer buffer;
		public LinkedList<Packet> packets = new LinkedList<Packet>();
		/** Length of message, including the length field at the beggining of
		 * the packet. */
		private int messageLength = -1;
		
		public SocketReader(SocketChannel channel) {
			this.channel = channel;
			this.buffer = ByteBuffer.allocate(0xffff);
		}
		
		public void processReadIteration() throws IOException {
			int byteRead = channel.read(buffer);
			
			if (byteRead == 0) 
				return;

			boolean hasReadableData = true;
			do {
				// Finish to read a message
				if (this.messageLength != -1 && buffer.position() >= this.messageLength) {
					int suppBytes = buffer.position() - this.messageLength;
					buffer.position(4);
					buffer.limit(this.messageLength);
					// TODO manage errors
					packets.addLast(Packet.readPacket(buffer));
					
					ByteBuffer newBuf = ByteBuffer.allocate(0xffff);
					buffer.position(this.messageLength);
					buffer.limit(this.messageLength + suppBytes);
					newBuf.put(buffer);
					buffer = newBuf;
					this.messageLength = -1;
				}
				
				// Start to read a new message
				else if (this.messageLength == -1 && buffer.position() >= 4) {
					int oldPos = buffer.position();
					buffer.position(0);
					this.messageLength = buffer.getInt();
					buffer.position(oldPos);
				}
				else {
					hasReadableData = false;
				}
			} while (hasReadableData);
		}
	}
	
	private int port;
	private ArrayList<PacketListener> packetListeners;

	private Thread receiveThread;
	private boolean running;
	private ServerSocketChannel server;
	private HashMap<SelectionKey, SocketReader> sockets = new HashMap<SelectionKey, SocketReader>();

	
	public TCPReceiver(int port) throws IOException {
		this.port = port;
		this.packetListeners = new ArrayList<PacketListener>();

		try {
			this.server = ServerSocketChannel.open();
			this.server.socket().bind(new InetSocketAddress("localhost", port));
			this.server.configureBlocking(false);
		} catch (IOException e) {
			throw e;
		}

		this.receiveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Selector selector = null;
				try {
					selector = Selector.open();
				} catch (IOException e) {
					throw new RuntimeException("ReceiveThread could not launch");
				}

				while (running) {
					try {
						// Server
						SocketChannel newSocket = server.accept();
						
						if (newSocket != null) {
							newSocket.configureBlocking(false);
							SelectionKey key = newSocket.register(selector, SelectionKey.OP_READ);
							sockets.put(key, new SocketReader(newSocket));
						}
						
						// TODO select is blocking, resulting in a problem with only one thread... Server should have 2 threads
						if (sockets.isEmpty())
							continue;
						
						// Socket
						int readyChannels = selector.select();

						if (readyChannels == 0)
							continue;

						Set<SelectionKey> selectedKeys = selector.selectedKeys();
						Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

						while (keyIterator.hasNext()) {

							SelectionKey key = keyIterator.next();
							SocketReader reader = sockets.get(key);

							if (key.isReadable()) {
								reader.processReadIteration();
								
								for (Packet packet : reader.packets) {
									for (PacketListener l : packetListeners) {
										InetAddress socketAddr = reader.channel.socket().getInetAddress();
										l.onPacket(socketAddr, packet);
									}
								}
							}

							keyIterator.remove();
						}
					} catch (IOException e) {
						// TODO handle errors
						System.err.println("Something went wrong. Error not handled yet.");
					}
				}
			}
		}, "TCP receiver thread");

		//this.receiveThread.setDaemon(true);
		this.running = true;
		this.receiveThread.start();
	}

	public int getPort() {
		return this.port;
	}

	public void addPacketListener(PacketListener l) {
		this.packetListeners.add(l);
	}

	public void clearPacketListeners() {
		this.packetListeners.clear();
	}
	
	/** Stops the server and close every opened socket */
	public void stop() throws IOException {
		this.running = false;
		// TODO close server in the server thread when it's not blocking (make it non-blocking BEFORE)
		this.server.close();
	}
}
