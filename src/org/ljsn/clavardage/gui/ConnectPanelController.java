package org.ljsn.clavardage.gui;

import java.io.IOException;

import org.ljsn.clavardage.core.Session;
import org.ljsn.clavardage.presence.PresenceClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
	private CheckBox presenceServerCheck;
	
	@FXML
	private TextField hostText;
	
	@FXML
	private TextField portText;
	
	@FXML
	private Button connectButton;
	
	@FXML
	protected void onPresenceServerCheckChange(ActionEvent evt) {
		boolean active = this.presenceServerCheck.isSelected();
		this.hostText.setDisable(!active);
		this.portText.setDisable(!active);
	}
	
	@FXML
	protected void handleConnect(ActionEvent evt) {
		
		try {
			PresenceClient presenceClient = null;
			
			if (this.presenceServerCheck.isSelected()) {
				try {
					int port = Integer.valueOf(portText.getText());
					
					if (port < 0 || port > 0xFFFF) {
						throw new NumberFormatException("Bad port");
					}
					else {
						presenceClient = new PresenceClient(hostText.getText(), port);
					}
				}
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("Port should be a number between 0 and 65535");
				}
			}
			
			this.app.session = new Session(this.pseudoText.getText(), new GUISessionListener(this.app), presenceClient);
			this.connectButton.setDisable(true);
		}
		catch (IllegalArgumentException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Can't connect");
			alert.setContentText(e.getMessage());
			alert.show();
		}
	}
}
