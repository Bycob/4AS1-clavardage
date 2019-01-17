package org.ljsn.clavardage.presence;

/** This class represents a client of one PresenceServer.
 * Its methods provide simple way to send asynchronous requests
 * to the PresenceServer. */
public class PresenceClient {

	private String serverAddress;
	private int port;
	
	private PresenceClientListener listener;
	
	
	/** Create a client attached to a presence server.
	 * @param serverAddress The address of the presence server this client
	 * is attached to.
	 * @param port the port of the presence server*/
	public PresenceClient(String serverAddress, int port) {
		this.serverAddress = serverAddress;
		this.port = port;
	}
	
	
	public void setListener(PresenceClientListener listener) {
		this.listener = listener;
	}
	
	/** Connect to presence server
	 * @param pseudo User initial pseudo
	 * @param tcpPort TCP port that this instance of the application is using
	 * to receive messages. */
	public void connect(String pseudo, int tcpPort) {
		
	}
	
	/** Notice the presence server with a "change pseudo" request.
	 * The pseudo may be refused by the Presence Server. */
	public void changePseudo(String pseudo) {
		
	}
	
	/** Disconnect from the presence server. */
	public void disconnect() {
		
	}
	
	/** Request user list from Presence Server */
	public void requestUserList() {
		
	}
}
