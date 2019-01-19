package org.ljsn.clavardage.database;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Database {
	public String test;
	
	private MongoClient mongoClient;
	
	private DB mongoDatabase;
	

	public void saveToDb(String collection, DBObject dbo) {
		if (this.mongoDatabase == null) {
			// test if database is not set 
			System.out.println("Database is not set");
		} else {
			// get collection whose name was passed
			DBCollection dbc = this.mongoDatabase.getCollection(collection);
			// insert database object in collection 
			dbc.insert(dbo);
			System.out.println("Object was inserted");
		}
	}
	
	public void updateInDb(String collection, DBObject query, DBObject dbo) {
		this.mongoDatabase.getCollection(collection).update(query, dbo);
	}
	
	public DBObject getFromDb(String collection, DBObject query) {
		DBCursor cursor = this.mongoDatabase.getCollection(collection).find(query);
		return cursor.one();
	}
	
	public void initializeClient() {
		try {
			this.mongoClient = new MongoClient();			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initializeDB(String name) {
		this.mongoDatabase = mongoClient.getDB(name);
	}
	
	public void closeMongoDB() {
		this.mongoClient.close();
	}
}
