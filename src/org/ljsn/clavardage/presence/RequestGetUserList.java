package org.ljsn.clavardage.presence;

import org.ljsn.clavardage.core.UserList;

public class RequestGetUserList extends Request {
	
	private UserList userList = null;
	
	public RequestGetUserList() {
		super(RequestType.GET_USER_LIST);
	}
	
	public void setUserList(UserList list) {
		this.userList = list;
	}
}
