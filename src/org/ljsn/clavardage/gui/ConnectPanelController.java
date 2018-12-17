package org.ljsn.clavardage.gui;

import org.ljsn.clavardage.core.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConnectPanelController {
	
	// Application part
	private ClavardageApp app;
	private Session session;
	
	public ConnectPanelController(ClavardageApp app) {
		this.app = app;
		this.session = app.getSession();
	}

	// FXML part
	
	@FXML
	private TextField pseudoText;
	
	@FXML
	private Button connectButton;
	
	@FXML
	protected void handleConnect(ActionEvent evt) {
		this.app.setMainView();
	}
}
