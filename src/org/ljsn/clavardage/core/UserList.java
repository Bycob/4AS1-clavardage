package org.ljsn.clavardage.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class UserList implements Serializable, Iterable<User> {
	private static final long serialVersionUID = -2785537094544793469L;
	private ArrayList<User> users;
	
	public UserList() {
		this.users = new ArrayList<User>();
	}
	
	
	// clone UserList
	public UserList(UserList clone) {
	    this.users = new ArrayList<User>(clone.users);
	}
	
	/** Adds every unkown user from the other list */
	public void addUserList(UserList other) {
		for (User user : other) {
			if (!this.hasUser(user)) {
				this.users.add(user);
			}
		}
	}
	
	public void addUser(User u) {
		users.add(u);
	}
	
	public boolean removeUser(User u) {
		return users.remove(u);
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

	@Override
	public Iterator<User> iterator() {
		return this.users.iterator();
	}
	
	// returns user corresponding to pseudo and ip addr if exists
	public User pseudoMatchesIP(String pseudo, String ipAddr) {
		Iterator<User> userIterator = this.users.iterator();
		while(userIterator.hasNext()) {
			User currentUser = userIterator.next();
			if (currentUser.getPseudo() == pseudo && currentUser.getIpAddr() == ipAddr)
				return currentUser;
		}
		return null;
	}
	
	public boolean isEmpty() {
		return users.isEmpty();
	}
}
