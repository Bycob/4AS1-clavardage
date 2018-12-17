package org.ljsn.clavardage.gui;

import org.ljsn.clavardage.core.Session;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClavardageApp extends Application {
	
	private Stage primaryStage;
	
	private Session session;
	
	private Parent connectPanel;
	private Parent mainView;
	
	
	public ClavardageApp() {
		
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		stage.setWidth(800);
		stage.setHeight(600);
		stage.setTitle("sCOOp-di-POOp");
		
		FXMLLoader loader = new FXMLLoader(ClavardageApp.class.getResource("fxml/connect-panel.fxml"));
		loader.setController(new ConnectPanelController(this));
		connectPanel = loader.load();
		
		loader = new FXMLLoader(ClavardageApp.class.getResource("fxml/main-view.fxml"));
		loader.setController(new MainViewController(this));
		mainView = loader.load();
		
		setConnectPanel();
		
		stage.show();
	}
	
	public Session getSession() {
		return this.session;
	}
	
	public void setConnectPanel() {
		Scene scene = new Scene(this.connectPanel);
		this.primaryStage.setScene(scene);
	}
	
	public void setMainView() {
		Scene scene = new Scene(this.mainView);
		this.primaryStage.setScene(scene);
	}
	
	public static void main(String[] args) {
		Application.launch(ClavardageApp.class, args);
	}
}

