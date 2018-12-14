package org.ljsn.clavardage.core;

import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable {
	private static final long serialVersionUID = -2785537094544793469L;
	private ArrayList<User> users;
	
	public UserList() {
		this.users = new ArrayList<User>();
	}
	
	public void addUser(User u) {
		users.add(u);
	}
	
	public boolean hasUser(User u) {
		return users.contains(u);
	}
	
	public boolean isEmpty() {
		return users.isEmpty();
	}
}
