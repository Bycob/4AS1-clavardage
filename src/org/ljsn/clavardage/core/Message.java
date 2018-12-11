package org.ljsn.clavardage.core;

public class Message {
    private Date timestamp;
    private String content;
    private User author;
    
    public Message(Date timestamp, String content, User author) {
    	this.timestamp = timestamp;
    	this.content = content;
    	this.author = author;
    	return this;
    }
    
    public getTime() {
    	return this.timestamp;
    }
    
    public getContent() {
    	return this.content;
    }
    
    public getAuthor() {
    	return this.author;
    } 
}
