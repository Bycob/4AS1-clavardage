package org.ljsn.clavardage.gui;

import java.util.LinkedList;

import org.ljsn.clavardage.core.SessionListener;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class GUISessionListener implements SessionListener {

	private ClavardageApp app;
	
	private LinkedList<Task> taskQueue = new LinkedList<Task>();
	
	public GUISessionListener(ClavardageApp app) {
		this.app = app;
	}
	
	@Override
	public void onConnectionFailed(Exception error) {
		runTask(new ExecOnJavaFXThread() {
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println(Thread.currentThread().getName());
			}
		});
	}

	@Override
	public void onConnectionSuccess() {
		
	}

	@Override
	public void onMessageSent() {
		
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
