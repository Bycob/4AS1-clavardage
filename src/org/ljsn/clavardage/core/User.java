package org.ljsn.clavardage.core;

public class User {
	private String pseudo;
	
	public User(String p) {
		this.pseudo = p;
	}
	
	public String getPseudo() {
		return this.pseudo;
	}
	
	public void setPseudo(String p) {
		this.pseudo = p;
	}
}
