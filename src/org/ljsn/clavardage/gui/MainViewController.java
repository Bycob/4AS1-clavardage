package org.ljsn.clavardage.gui;

import java.net.URL;
import java.util.ResourceBundle;

import org.ljsn.clavardage.core.User;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	// Application part
	
	private ClavardageApp app;
	
	private User currentUser;
	
	public MainViewController(ClavardageApp app) {
		this.app = app;
	}
	
	public void updateUsers() {
		this.usersBox.getChildren().clear();
		
		if (this.app.session == null)
			return;
		
		for (User user : this.app.session.getUserList()) {
			Button userButton = new Button();
			userButton.setText(user.getPseudo());
			userButton.setAlignment(Pos.CENTER);
			userButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					openConversation(user);
				}
			});
			
			this.usersBox.getChildren().add(userButton);
		}
	}
	
	public void openConversation(User user) {
		this.currentUser = user;
		this.app.session.getConversation(this.currentUser);
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
		if (this.currentUser != null && this.app.session != null) {
			this.app.session.sendMessage(this.currentUser, this.messageText.getText());
			this.messageText.setText("");
		}
	}
}
