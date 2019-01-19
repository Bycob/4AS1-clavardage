package org.ljsn.clavardage.core;

import java.io.Serializable;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Message implements Serializable {
    
	private static final long serialVersionUID = 3526921158965796701L;
	
	private Date timestamp;
    private String content;
    private User author;
    
    public Message(Date timestamp, String content, User author) {
    	this.timestamp = timestamp;
    	this.content = content;
    	this.author = author;
    }
    
    public void setAuthor(User author) {
    	this.author = author;
    }
    
    public Date getTime() {
    	return this.timestamp;
    }
    
    public String getContent() {
    	return this.content;
    }
    
    public User getAuthor() {
    	return this.author;
    } 
    
    public DBObject MessageToDBO() {
    	return new BasicDBObject().append("timestamp",this.timestamp)
    							  .append("content",this.content)
    							  .append("author",this.author.getIpAddr());
    }
}
