package org.ljsn.clavardage.network;

import org.ljsn.clavardage.core.UserList;

public class PacketHelloBack extends Packet {
	
	private static final long serialVersionUID = 3720002291814787773L;
	
	private UserList activeUsers;
	
	private Boolean pseudoUsed = false;
	
	public PacketHelloBack() {
		this((UserList) null);
	}
	
	public PacketHelloBack(UserList ul) {
		super(Type.HELLO_BACK);
		this.activeUsers = ul;
	}
	
	public PacketHelloBack(Boolean b) {
		super(Type.HELLO_BACK);
		this.activeUsers = null;
		this.pseudoUsed = true;
	}
	
	public UserList getActiveUsers() {
		return this.activeUsers;
	}
	
	public Boolean pseudoUsed() {
		return this.pseudoUsed;
	}
	
	/*
	@Override
	public void writeContent(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		ArrayList<User> u = activeUsers.getUsers();
		for (int i = 0; i < u.size(); i++) {
			sb.append(u.get(i)).append("\n");
		}
		
		ByteBuffer bufToWrite = StandardCharsets.UTF_8.encode(sb.toString());
		buffer.put(bufToWrite);
	}

	@Override
	public void readContent(ByteBuffer from) {
		CharBuffer decoded = StandardCharsets.UTF_8.decode(from);
		String[] lines = decoded.toString().split("\n");
		
		// TODO manage errors
		for (int i = 0; i < lines.length; i++) {
			String[] params = lines[i].split(" ");
			this.activeUsers.addUser(new User(params[1], Integer.parseInt(params[3]), params[5]));
		}
	}*/
}
