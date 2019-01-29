package org.ljsn.clavardage.database;

import java.util.ArrayList;
import java.util.Iterator;

import org.bson.types.ObjectId;
import org.ljsn.clavardage.core.Conversation;
import org.ljsn.clavardage.core.Message;
import org.ljsn.clavardage.core.User;
import org.ljsn.clavardage.core.UserList;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DatabaseController {
	private Database db;
	
	public DatabaseController() {
		// initialize local database for conversations
		this.db = new Database();
		db.initializeClient();
		db.initializeDB("clavardage");
	}
	
	// saves an empty conversation with user u in database
	public void newConversation(User u) {
		DBObject dbo = new BasicDBObject("_id", new ObjectId())
				.append("ip_recepteur", u.getIpAddr());
		db.saveToDb("conversations", dbo);
	}
	
	// updates the conversation with user u in database with the message list in c
	public void updateConversation(Conversation c, User u) {
		DBObject query = new BasicDBObject()
				.append("ip_recepteur", u.getIpAddr());
				
		DBObject dbo = new BasicDBObject()
				.append("$set", new BasicDBObject().append("messages", c.MessageListToDBO()));
		this.db.updateInDb("conversations", query, dbo);
	}
	
	// fetches conversation corresponding to user u in database
	public Conversation getConversation(User u, UserList ul) {
		DBObject res = db.getFromDb("conversations", new BasicDBObject("ip_recepteur", u.getIpAddr()));
		if (res == null) return null;
		else {
			// convert DBObject to conversation 
			ArrayList<Message> messageList = Conversation.DBOToMessageList(res.get("messages"), ul);
			Conversation c = new Conversation();
			// iterate over message list and add each message to conversation
			Iterator<Message> messageIterator = messageList.iterator();
			
			while(messageIterator.hasNext()) {
				Message currentMsg = messageIterator.next();
				c.addMessage(currentMsg);
			}
			return c;
		}
	}
	
	
	
	public void close() {
		this.db.closeMongoDB();
	}

}
