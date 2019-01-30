package org.ljsn.clavardage.database;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;

public class Database {
	public String test;

	private MongoClient mongoClient;

	private boolean dbUnreachable = false;
	private DB mongoDatabase;


	public void saveToDb(String collection, DBObject dbo) {
		if (this.mongoDatabase == null || this.dbUnreachable) {
			// test if database is not set
			throw new DatabaseException("Database is not set");
		} else {
			// get collection whose name was passed
			DBCollection dbc = this.mongoDatabase.getCollection(collection);
			// insert database object in collection
			try {
				dbc.insert(dbo);
			}
			catch (MongoTimeoutException e) {
				e.printStackTrace();
				this.dbUnreachable = true;
				throw new DatabaseException("Object was inserted");
			}
		}
	}

	public void updateInDb(String collection, DBObject query, DBObject dbo) {
		if (this.mongoDatabase == null || this.dbUnreachable) {
			throw new DatabaseException("Database is not set");
		}

		try {
			this.mongoDatabase.getCollection(collection).update(query, dbo);
		}
		catch (MongoTimeoutException e) {
			e.printStackTrace();
			this.dbUnreachable = true;
			throw new DatabaseException("Unable to update the object");
		}
	}

	public DBObject getFromDb(String collection, DBObject query) {
		if (this.mongoDatabase == null || this.dbUnreachable) {
			throw new DatabaseException("Database is not set");
		}

		try {
			DBCursor cursor = this.mongoDatabase.getCollection(collection).find(query);
			return cursor.one();
		}
		catch(MongoTimeoutException e) {
			e.printStackTrace();
			this.dbUnreachable = true;
			throw new DatabaseException("Unable to retrieve the object");
		}

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
