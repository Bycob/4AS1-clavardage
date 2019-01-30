package org.ljsn.clavardage.gui;

import org.ljsn.clavardage.core.Session;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClavardageApp extends Application {
	
	private Stage primaryStage;
	
	// Package accessible fields
	Session session;
	
	Parent connectPanel;
	ConnectPanelController connectPanelController;
	Parent mainView;
	MainViewController mainViewController;
	
	
	public ClavardageApp() {
		
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		stage.setWidth(800);
		stage.setHeight(600);
		stage.setTitle("QUASI-MIR");
		
		FXMLLoader loader = new FXMLLoader(ClavardageApp.class.getResource("fxml/connect-panel.fxml"));
		loader.setController(connectPanelController = new ConnectPanelController(this));
		connectPanel = loader.load();
		
		loader = new FXMLLoader(ClavardageApp.class.getResource("fxml/main-view.fxml"));
		loader.setController(mainViewController = new MainViewController(this));
		mainView = loader.load();
		
		setScene(connectPanel);
		
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		if (this.session != null)
			this.session.destroy();
	}
	
	void setScene(Parent sceneRoot) {
		Scene currentScene = this.primaryStage.getScene();
		if (currentScene == null) {
			this.primaryStage.setScene(new Scene(sceneRoot));
		}
		else if (currentScene.getRoot() != sceneRoot) {
			currentScene.setRoot(sceneRoot);
		}
	}
	
	public static void main(String[] args) {
		Application.launch(ClavardageApp.class, args);
	}
}

