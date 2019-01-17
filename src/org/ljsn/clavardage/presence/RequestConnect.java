package org.ljsn.clavardage.presence;

public class RequestConnect extends Request {

	public enum Result {
		SUCCESS,
		ALREADY_CONNECTED,
		PSEUDO_ALREADY_USED
	}
	
	private String pseudo;
	private int tcpPort;
	private Result result = null;
	
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
	
	public void setResult(Result result) {
		this.result = result;
	}
}
