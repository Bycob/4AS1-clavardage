package org.ljsn.clavardage.gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.ljsn.clavardage.core.Conversation;
import org.ljsn.clavardage.core.Message;
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
		updateConversation();
	}
	
	public void updateConversation() {
		Conversation conv = this.app.session.getConversation(this.currentUser);
		
		StringBuilder content = new StringBuilder();
		content.append("Conversation with : " + this.currentUser.getPseudo() + "\n\n\n");
		
		ArrayList<Message> messages = conv.getMessages();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		// TODO add a maximum amount of messages displayed.
		for (Message message : messages) {
			content.append("[").append(format.format(message.getTime())).append("] ");
			content.append(message.getAuthor().getPseudo());
			content.append(" : ");
			content.append(message.getContent());
			content.append("\n\n");
		}
		
		this.conversationArea.setText(content.toString());
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
