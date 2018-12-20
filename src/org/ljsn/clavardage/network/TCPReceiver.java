package org.ljsn.clavardage.network;

import java.io.IOException;
import java.net.InetAddress;
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

	/** This thread runs the socket creation process. */
	private Thread serverThread;
	/** This thread runs the read operations performed on already opened sockets. */
	private Thread socketsThread;
	private boolean running;
	
	private ServerSocketChannel server;
	private Selector selector;
	private HashMap<String, SocketReader> sockets = new HashMap<String, SocketReader>();

	
	public TCPReceiver() throws IOException {
		this.packetListeners = new ArrayList<PacketListener>();

		try {
			this.server = ServerSocketChannel.open();
			this.server.configureBlocking(false);
			this.server.socket().bind(null);
			this.port = this.server.socket().getLocalPort();

			this.selector = null;
			this.selector = Selector.open();
		} catch (IOException e) {
			throw e;
		}

		this.serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (running) {
					try {
						// Server
						SocketChannel newSocket = server.accept();
						
						if (newSocket != null) {
							newSocket.configureBlocking(false);
							
							synchronized (sockets) {
								newSocket.register(selector, SelectionKey.OP_READ);
								sockets.put(newSocket.getRemoteAddress().toString(), new SocketReader(newSocket));
							}
						}
					} catch (IOException e) {
						// TODO handle errors
						System.err.println("Something went wrong. Error not handled yet. Error below");
						e.printStackTrace();
					}
				}
				
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "TCP receiver thread");
		
		this.socketsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					try {
						int readyChannels = selector.selectNow();

						if (readyChannels == 0)
							continue;

						Set<SelectionKey> selectedKeys = selector.selectedKeys();
						Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

						while (keyIterator.hasNext()) {

							SelectionKey key = keyIterator.next();
							SocketReader reader;
							
							synchronized (sockets) {
								reader = sockets.get(((SocketChannel)key.channel()).getRemoteAddress().toString());
							}
							//System.out.println(((SocketChannel)key.channel()).getRemoteAddress().toString());
							//System.out.println(reader);

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
						System.err.println("Something went wrong. Error not handled yet. Error below");
						e.printStackTrace();
					}
				}
			}
		});

		//this.receiveThread.setDaemon(true);
		this.running = true;
		this.serverThread.start();
		this.socketsThread.start();
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
		this.selector.close();
	}
}
