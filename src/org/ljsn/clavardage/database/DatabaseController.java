package org.ljsn.clavardage.database;

import org.bson.types.ObjectId;
import org.ljsn.clavardage.core.Conversation;
import org.ljsn.clavardage.core.User;

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
	
	public void close() {
		this.db.closeMongoDB();
	}

}
