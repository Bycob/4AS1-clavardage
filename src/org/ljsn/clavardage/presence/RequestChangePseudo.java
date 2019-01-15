package org.ljsn.clavardage.presence;

public class RequestChangePseudo extends Request {

	private String pseudo;
	
	public RequestChangePseudo(String pseudo) {
		super(RequestType.CHANGE_PSEUDO);
		
		this.pseudo = pseudo;
	}
	
	public String getPseudo() {
		return this.pseudo;
	}
}
