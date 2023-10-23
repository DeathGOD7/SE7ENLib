// This file is part of SE7ENLib, created on 08/10/2023 (18:48 PM)
// Name : MongoDB
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.database.dbtype.mongodb;

import static com.mongodb.client.model.Filters.eq;

import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.HashMap;

public class MongoDB {

	private final String uri;
	private final String username;
	private final String password;
	private final String dbName;

	public String getDBName(){
		return dbName;
	}

	private MongoDatabase connection;
	public MongoDatabase getConnection(){
		if (connection == null){
			connection = connectMongoDB();
			return connection;
		}
		return  connection;
	}

	private final HashMap<String, Table> tables = new HashMap<>();
	public HashMap<String, Table> getTables() {
		return tables;
	}

	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}
	public MongoDB(String dbname, String uri, String username, String password){
		this.uri = uri;
		this.username = username;
		this.password = password;
		this.dbName = dbname;

		connection = connectMongoDB();
	}

	public boolean isConnected(){
		return (connection != null);
	}

	private MongoDatabase connectMongoDB() {
		try(MongoClient mongoClient = MongoClients.create(uri)) {
			return mongoClient.getDatabase(this.dbName);
		}
		catch (MongoClientException ex) {
			throw new MongoClientException("Connection to db error!!");
		}
	}
}
