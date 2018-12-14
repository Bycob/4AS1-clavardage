package org.ljsn.clavardage.core;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 4187250264934870484L;
	private String pseudo;
	private int tcpPort;
	private String ipAddr;
	
	public User(String p, int tcpp, String ip) {
		this.pseudo = p;
		this.tcpPort = tcpp;
		this.ipAddr = ip;
	}
	
	public String getPseudo() {
		return this.pseudo;
	}
	
	public int getTcpPort() {
		return this.tcpPort;
	}
	
	public String getIpAddr() {
		return this.ipAddr;
	}
	
	public void setPseudo(String p) {
		this.pseudo = p;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipAddr == null) ? 0 : ipAddr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (ipAddr == null) {
			if (other.ipAddr != null)
				return false;
		} else if (!ipAddr.equals(other.ipAddr))
			return false;
		return true;
	}

	public String toString() {
		return "User "+this.pseudo+" tcpPort "+this.tcpPort+" ipAddr "+this.ipAddr;
	}
	
}
