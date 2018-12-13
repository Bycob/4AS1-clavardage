package org.ljsn.clavardage.network;

import org.ljsn.clavardage.core.User;

public interface PacketListener {
	void onPacket(User user, Packet packet);
}
