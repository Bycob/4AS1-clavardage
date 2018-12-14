package org.ljsn.clavardage.gui;

import org.ljsn.clavardage.core.Session;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClavardageApp extends Application {
	
	private Stage primaryStage;
	private BorderPane root = new BorderPane();
	
	private Session session;
	
	public ClavardageApp() {
		
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		stage.setWidth(800);
		stage.setHeight(600);
		stage.setTitle("sCOOp-di-POOp");
		
		Scene scene = new Scene(this.root);
		stage.setScene(scene);
		
		createSession();
		
		stage.show();
	}
	
	private void createSession() {
		this.session = new Session("jean-michel");
	}
	
	public static void main(String[] args) {
		Application.launch(ClavardageApp.class, args);
	}
}

