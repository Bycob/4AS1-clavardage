package org.ljsn.clavardage.gui;

import java.io.IOException;

import org.ljsn.clavardage.core.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConnectPanelController {
	
	// Application part
	private ClavardageApp app;
	
	public ConnectPanelController(ClavardageApp app) {
		this.app = app;
	}
	
	/** Method called after disabling or deconnection from a session. */
	public void reset() {
		try {
			this.app.session.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.app.session = null;
		
		this.app.setScene(this.app.connectPanel);
		this.connectButton.setDisable(false);
	}
	
	// FXML part
	
	@FXML
	private TextField pseudoText;
	
	@FXML
	private Button connectButton;
	
	@FXML
	protected void handleConnect(ActionEvent evt) {
		try {
			this.app.session = new Session(this.pseudoText.getText(), new GUISessionListener(this.app));
			this.connectButton.setDisable(true);
		}
		catch (IllegalArgumentException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Wrong pseudo");
			alert.setContentText(e.getMessage());
			alert.show();
		}
	}
}
