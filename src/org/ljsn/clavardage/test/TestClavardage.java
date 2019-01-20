package org.ljsn.clavardage.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.ljsn.clavardage.core.User;
import org.ljsn.clavardage.core.UserList;
import org.ljsn.clavardage.database.Database;
import org.ljsn.clavardage.network.Packet;
import org.ljsn.clavardage.network.PacketHello;
import org.ljsn.clavardage.network.PacketHelloBack;
import org.ljsn.clavardage.network.PacketListener;
import org.ljsn.clavardage.network.TCPReceiver;
import org.ljsn.clavardage.network.TCPSender;
import org.ljsn.clavardage.network.UDPMessager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TestClavardage {
	
	public static void main(String... args) throws IOException {
		// JOptionPane.showMessageDialog(null, "Test", "hey", JOptionPane.CANCEL_OPTION);
		testDB();
		testTCP();
	}
	
	private static void testDB() {
		class Grade {
			public Date date;
			public String grade;
			public Integer score;
			
			public Grade(Date d, String s, Integer i) {
				this.date = d;
				this.grade = s;
				this.score = i;
			}
		}
		ArrayList<Double> coord = new ArrayList<Double>();
		ArrayList<Grade> grades = new ArrayList<Grade>();
		Grade a = new Grade(new Date(), "A++", 12);
		grades.add(a);
		coord.add(1.2333);
		coord.add(4.5777);
		
		Database db = new Database();
		db.initializeClient();
		db.initializeDB("clavardage");
		DBObject dbo = new BasicDBObject("_id", new ObjectId())
											.append("address", new BasicDBObject("building", "GEI")
														.append("coord", new BasicDBObject("type", "Point")
														.append("coordinates", coord))
													.append("street", "avenue des sciences appliquees")
													.append("zipcode", "31400"))
											.append("borough", "Gotham")
											.append("cuisine", "Indian")
											.append("grades", new BasicDBObject())
											.append("name", "The most royal magnificent taj mahal")
											.append("restaurant_id", "12345");
		db.saveToDb("conversations", dbo);
		
		DBObject res = db.getFromDb("conversations", new BasicDBObject("name", "The most royal magnificent taj mhal"));
		if (res != null) System.out.println("So the fetch works cause the id of the restaurant is"+res.get("restaurant_id"));
		else System.out.println("Find does not work");
		db.closeMongoDB();
		
	}
	
	private static void testTCP() throws IOException {
		TCPReceiver receiver = new TCPReceiver();
		receiver.addPacketListener(new PacketListener() {
			@Override
			public void onPacket(InetAddress address, Packet packet) {
				if (packet instanceof PacketHello) {
					System.out.print(((PacketHello) packet).getPseudo() + " ");
					System.out.println(((PacketHello) packet).getTcpPort());
				}
				else if (packet instanceof PacketHelloBack) {
					System.out.println(((PacketHelloBack) packet).getActiveUsers());
				}
			}
		});
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		TCPSender sender = new TCPSender("localhost", receiver.getPort());
		sender.sendPacket(new PacketHello("dab1", 1));
		sender.sendPacket(new PacketHello("dab2", 2));
		sender.sendPacket(new PacketHello("dab3", 3));
		sender.sendPacket(new PacketHello("dab4", 4));

		User u1 = new User("toto", 22, "123.456.789.10");
		User u2 = new User("louis jean", 22, "123.456.789.10");
		User u3 = new User("singapore", 22, "123.456.789.10");
		
		UserList ul = new UserList();
		ul.addUser(u1);
		ul.addUser(u2);
		ul.addUser(u3);
		
		sender.sendPacket(new PacketHelloBack(ul));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		receiver.stop();
		sender.close();
	}
	
	private static void testUDP() throws IOException {
		DatagramChannel sender = DatagramChannel.open();
		
		UDPMessager messager = new UDPMessager("225.4.5.6", 5555);
		ByteBuffer buffer = ByteBuffer.wrap(new byte[] {5, 5, 5});
		sender.send(buffer, new InetSocketAddress("localhost", 5555));
		messager.multicast(new PacketHello("mikouri", 5555));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("MASSAGE");
	}
}
