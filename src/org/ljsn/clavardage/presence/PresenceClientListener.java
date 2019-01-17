package org.ljsn.clavardage.presence;

import org.ljsn.clavardage.core.UserList;

public interface PresenceClientListener {

	default void onConnectResponse() { }
	default void onConnectFailure(Exception e) { }
	default void onGetUserListResponse(UserList ul) { }
	default void onGetUserListFailure(Exception e) { }
	default void onDisconnect() { }
	default void onDisconnectFailure(Exception e) { }
	default void onChangePseudo() { }
	default void onChangePseudoFailure(Exception e) { }
}
