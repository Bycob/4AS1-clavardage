package org.ljsn.clavardage.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.ljsn.clavardage.network.Packet;
import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.PacketListener;
import org.ljsn.clavardage.network.TCPReceiver;
import org.ljsn.clavardage.network.TCPSender;
import org.ljsn.clavardage.network.UDPMessager;

public class TestClavardage {
	
	public static void main(String... args) throws IOException {
		testTCP();
	}
	
	private static void testTCP() throws IOException {
		TCPReceiver receiver = new TCPReceiver(5555);
		receiver.addPacketListener(new PacketListener() {
			@Override
			public void onPacket(InetAddress address, Packet packet) {
				if (packet instanceof PacketHello) {
					System.out.println(((PacketHello) packet).getPseudo());
					System.out.println(((PacketHello) packet).getTcpPort());
				}
			}
		});
		
		TCPSender sender = new TCPSender("localhost", 5555);
		sender.sendPacket(new PacketHello("dab", 5555));
		sender.sendPacket(new PacketHello("dab2", 5555));
		sender.sendPacket(new PacketHello("dab2", 5555));
		sender.sendPacket(new PacketHello("dab4", 5555));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void testUDP() throws IOException {
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
