package org.ljsn.clavardage.gui;

import java.net.URL;
import java.util.ResourceBundle;

import org.ljsn.clavardage.core.Session;
import org.ljsn.clavardage.core.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	// Application part
	
	private ClavardageApp app;
	private Session session;
	
	private User currentUser;
	
	public MainViewController(ClavardageApp app) {
		this.app = app;
		this.session = this.app.session;
	}
	
	public void updateUsers() {
		// this.session.getUserList();
	}
	
	public void openConversation(User user) {
		this.currentUser = user;
		
		// TODO openConversation
	}
	
	public void updateConversation() {
		
		// TODO updateConversation
	}
	
	// FXML part
	@FXML
	private TextField messageText;
	
	@FXML
	private Button sendButton;
	
	@FXML
	private TextArea conversationArea;
	
	@FXML
	private VBox usersBox;
	
	@FXML
	private Button templateUserButton;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.usersBox.getChildren().remove(templateUserButton);
		
		updateUsers();
	}
	
	@FXML
	protected void handleSend(ActionEvent evt) {
		if (this.currentUser != null) {
			this.session.sendMessage(this.currentUser, this.messageText.getText());
			this.messageText.setText("");
		}
	}
}
