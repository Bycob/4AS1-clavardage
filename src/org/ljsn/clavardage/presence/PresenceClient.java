package org.ljsn.clavardage.presence;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.ljsn.clavardage.core.UserList;

/** This class represents a client of one PresenceServer.
 * Its methods provide a simple way to send synchronous requests
 * to the PresenceServer.
 * <p>
 * The presence client <i>is</i> thread safe*/
public class PresenceClient {

	private String serverAddress;
	private int serverPort;
	
	
	/** Create a client attached to a presence server.
	 * @param serverAddress The address of the presence server this client
	 * is attached to.
	 * @param port the port of the presence server*/
	public PresenceClient(String serverAddress, int port) {
		this.serverAddress = serverAddress;
		this.serverPort = port;
	}
	
	/** Connect to presence server
	 * @param pseudo User initial pseudo
	 * @param tcpPort TCP port that this instance of the application is using
	 * to receive messages. 
	 * @throws IOException */
	public void connect(String pseudo, int tcpPort) throws IOException {
		Request request = new RequestConnect(pseudo, tcpPort);
		RequestConnect responseConnect = (RequestConnect) sendRequest(request);
		switch (responseConnect.getResult()) {
		case ALREADY_CONNECTED:
			throw new IOException("Already connected");
		case PSEUDO_ALREADY_USED:
			throw new IllegalArgumentException("Pseudo already used !");
		case SUCCESS:
			break;
		default:
			break;
		}
	}
	
	/** Notice the presence server with a "change pseudo" request.
	 * The pseudo may be refused by the Presence Server. 
	 * @throws IOException */
	public void changePseudo(String pseudo) throws IOException {
		RequestChangePseudo request = new RequestChangePseudo(pseudo);
		RequestChangePseudo response = (RequestChangePseudo) sendRequest(request);
		
	}
	
	/** Disconnect from the presence server. 
	 * @throws IOException */
	public void disconnect() throws IOException {
		RequestDisconnect request = new RequestDisconnect();
		RequestDisconnect response = (RequestDisconnect) sendRequest(request);
	}
	
	/** Request user list from Presence Server 
	 * @throws IOException */
	public UserList requestUserList() throws IOException {
		RequestGetUserList request = new RequestGetUserList();
		RequestGetUserList response = (RequestGetUserList) sendRequest(request);
		
		return response.getUserList();
	}
	
	private Request sendRequest(Request request) throws IOException {
		// Connection
		URL url = new URL("http://" + serverAddress + ":" + serverPort);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		connection.setConnectTimeout(500);
		connection.setReadTimeout(1000);
		
		connection.setDoOutput(true);
		request.writeToStream(connection.getOutputStream());
		
		// Reading data
		Request response = Request.readFromStream(connection.getInputStream());
		return response;
	}
}
