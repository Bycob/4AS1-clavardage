package org.ljsn.clavardage.core;

import java.util.ArrayList;

public class UserList {
	private ArrayList<User> users;
	
	public UserList() {
		this.users = new ArrayList<User>();
	}
	
	public ArrayList<User> getUsers() {
		return this.users;
	}
	
	public void addUser(User u) {
		users.add(u);
	}
}
