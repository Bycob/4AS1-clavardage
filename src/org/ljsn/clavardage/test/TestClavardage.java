package org.ljsn.clavardage.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.UDPMessager;

public class TestClavardage {
	
	public static void main(String... args) throws IOException {
		DatagramChannel sender = DatagramChannel.open();
		
		UDPMessager messager = new UDPMessager(5555);
		ByteBuffer buffer = ByteBuffer.wrap(new byte[] {5, 5, 5});
		sender.send(buffer, new InetSocketAddress("localhost", 5555));
		messager.broadcast(new PacketHello("mikouri", 5555));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("MASSAGE");
	}
}
