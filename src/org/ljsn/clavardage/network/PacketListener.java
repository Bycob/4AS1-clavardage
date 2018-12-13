package org.ljsn.clavardage.network;

import java.net.InetAddress;

public interface PacketListener {
	void onPacket(InetAddress address, Packet packet);
}
