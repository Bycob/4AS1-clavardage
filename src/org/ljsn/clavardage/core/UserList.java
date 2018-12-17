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
	
	public User getByIpAddress(String addr) {
		for (User user : this.users) {
			if (user.getIpAddr().equals(addr)) {
				return user;
			}
		}
		return null;
	}
	
	public User getByPseudo(String pseudo) {
		for (User user : this.users) {
			if (user.getPseudo().equals(pseudo)) {
				return user;
			}
		}
		return null;
	}
	
	public boolean hasPseudo(String pseudo) {
		return getByPseudo(pseudo) != null;
	}
	
	public boolean isEmpty() {
		return users.isEmpty();
	}
}
