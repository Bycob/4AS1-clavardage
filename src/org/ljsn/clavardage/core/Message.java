package org.ljsn.clavardage.core;

import java.util.Date;

public class Message {
    private Date timestamp;
    private String content;
    private User author;
    
    public Message(Date timestamp, String content, User author) {
    	this.timestamp = timestamp;
    	this.content = content;
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
}
