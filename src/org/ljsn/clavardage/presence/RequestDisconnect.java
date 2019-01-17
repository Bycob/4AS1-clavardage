package org.ljsn.clavardage.presence;

public class RequestDisconnect extends Request {
	
	public enum Result {
		SUCCESS,
		USER_NOT_FOUND;
	}
	
	
	private Result result = null;
	
	public RequestDisconnect() {
		super(RequestType.DISCONNECT);
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
}
