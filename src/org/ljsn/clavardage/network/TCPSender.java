package org.ljsn.clavardage.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPSender {
	
	private SocketChannel channel;

	public TCPSender(String address, int port) throws IOException {
		this.channel = SocketChannel.open(new InetSocketAddress(address, port));
	}
	
	/** Sends packets to the destination */
	public void sendPacket(Packet packet) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(0xffff);
		buffer.position(4);
		packet.write(buffer);
		buffer.flip();
		// Setting the length field. Everything included.
		buffer.putInt(buffer.limit());
		buffer.position(0);
		
		while (buffer.hasRemaining()) {
			this.channel.write(buffer);
		}
	}
	
	public void close() throws IOException {
		try {
			this.channel.close();
		} catch (Exception e) {
			// TODO
			System.out.println("Socket could not close.");
		}
	}
}
