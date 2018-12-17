package org.ljsn.clavardage.gui;

import org.ljsn.clavardage.core.SessionListener;

public class GUISessionListener implements SessionListener {

	private ClavardageApp app;
	
	public GUISessionListener(ClavardageApp app) {
		this.app = app;
	}
	
	@Override
	public void onConnectionFailed(Exception error) {
		
	}

	@Override
	public void onConnectionSuccess() {
		
	}

	@Override
	public void onMessageSent() {
		
	}
}
