package org.ljsn.clavardage.core;

public interface SessionListener {

	public void onConnectionFailed(Exception error);
	public void onConnectionSuccess();
	public void onUserListChange();
	public void onMessageSent(User user);
	public void onMessageReceived(User user);
}
