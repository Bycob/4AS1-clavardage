package org.ljsn.clavardage.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Conversation {
	private ArrayList<Message> messageList;
	private int version;
	
	public Conversation() {
		this.messageList = new ArrayList<Message>();
		// conversation version is initiated at 0
		this.version = 0;
	}
	
	public void addMessage(Message m) {
		// conversation version is updated at each new message
		this.version++;
		this.messageList.add(m);
	}
	
	public ArrayList<Message> getMessages() {
		return this.messageList;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public ArrayList<DBObject> MessageListToDBO() {
		ArrayList<DBObject> messageListDBO = new ArrayList<DBObject>();
		Iterator<Message> messageIterator = this.messageList.iterator();
		
		while(messageIterator.hasNext()) {
			Message currentMsg = messageIterator.next();
			messageListDBO.add(currentMsg.MessageToDBO());
		}
		
		return messageListDBO;
	}
	
	public static ArrayList<Message> DBOToMessageList(Object o, UserList ul, User current) {
		ArrayList<Message> ml = new ArrayList<Message>();
		
		// cast to array list of dbobject
		@SuppressWarnings("unchecked")
		ArrayList<DBObject> a_dbo = (ArrayList<DBObject>) o;
		// iterate over array
		Iterator<DBObject> aDboIterator = a_dbo.iterator();
		while(aDboIterator.hasNext()) {
			DBObject currentMsg = aDboIterator.next();
			Date timestamp = (Date)currentMsg.get("timestamp");
			String content = (String)currentMsg.get("content");
			String author = (String)currentMsg.get("author");
			User u = ul.getByIpAddress(author);
			if (u == null)
			{
				u = current;
			}
			Message msg = new Message(timestamp, content, u);
			ml.add(msg);
		}
		return ml;
	}
	
}
