package org.ljsn.clavardage.gui;

import java.util.LinkedList;

import org.ljsn.clavardage.core.SessionListener;
import org.ljsn.clavardage.core.User;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GUISessionListener implements SessionListener {

	private ClavardageApp app;
	
	private LinkedList<Task> taskQueue = new LinkedList<Task>();
	
	public GUISessionListener(ClavardageApp app) {
		this.app = app;
	}
	
	@Override
	public void onConnectionFailed(Exception error) {
		error.printStackTrace();
		runTask(new ExecOnJavaFXThread() {
			@Override
			public void handle(WorkerStateEvent event) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Connection failed");
				alert.setContentText(error.getMessage());
				
				alert.showAndWait();
				
				app.connectPanelController.reset();
			}
		});
	}

	@Override
	public void onConnectionSuccess() {
		runTask(new ExecOnJavaFXThread() {
			@Override
			public void handle(WorkerStateEvent event) {
				app.setScene(app.mainView);
				app.mainViewController.updateUsers();
			}
		});
	}
	
	@Override
	public void onUserListChange() {
		runTask(new ExecOnJavaFXThread() {
			@Override
			public void handle(WorkerStateEvent event) {
				app.mainViewController.updateUsers();
			}
		});
	}

	@Override
	public void onMessageSent(User user) {
		runTask(new ExecOnJavaFXThread() {
			@Override
			public void handle(WorkerStateEvent event) {
				app.mainViewController.openConversation(user);
			}
		});
	}
	
	@Override
	public void onMessageReceived(User user) {
		runTask(new ExecOnJavaFXThread() {
			@Override
			public void handle(WorkerStateEvent event) {
				app.mainViewController.updateConversation(user);
			}
		});
	}
	
	
	private void runTask(Task task) {
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
	
	private abstract class ExecOnJavaFXThread extends Task<Boolean> implements EventHandler<WorkerStateEvent> {

		public ExecOnJavaFXThread() {
			setOnSucceeded(this);
		}
		
		@Override
		protected Boolean call() {
			return true;
		}
	}
}
