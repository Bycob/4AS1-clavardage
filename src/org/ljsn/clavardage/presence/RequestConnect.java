package org.ljsn.clavardage.presence;

public class RequestConnect extends Request {

	private String pseudo;
	private int tcpPort;
	
	public RequestConnect(String pseudo, int tcpPort) {
		super(RequestType.CONNECT);
		
		this.pseudo = pseudo;
		this.tcpPort = tcpPort;
	}
	
	public String getPseudo() {
		return this.pseudo;
	}
	
	public int getTcpPort() {
		return this.tcpPort;
	}
}
