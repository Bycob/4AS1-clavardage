package org.ljsn.clavardage.presence;

public class RequestChangePseudo extends Request {

	public enum Result {
		SUCCESS,
		ALREADY_USED_PSEUDO;
	}
	
	private String pseudo;
	private Result result;
	
	public RequestChangePseudo(String pseudo) {
		super(RequestType.CHANGE_PSEUDO);
		
		this.pseudo = pseudo;
	}
	
	public String getPseudo() {
		return this.pseudo;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
}
