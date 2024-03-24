// This file is part of SE7ENLib, created on 08/10/2023 (18:48 PM)
// Name : MongoDB
// Author : Death GOD 7

package io.github.deathgod7.SE7ENLib.database.dbtype.mongodb;

import io.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.SE7ENLib.database.handler.MongoOperations;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

/**
 * Represents the MongoDB Database
 * @version 1.0
 * @since 1.0
 */
public class MongoDB extends MongoOperations {
	private final String host;
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

	private final LinkedHashMap<String, Table> tables = new LinkedHashMap<>();
	public LinkedHashMap<String, Table> getTables() {
		return tables;
	}

	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}

	public void removeTable(String tablename) {
		tables.remove(tablename);
	}
	public MongoDB(DatabaseInfo dbinfo){
		this.host = dbinfo.getHostAddress();
		this.username = dbinfo.getUsername();
		this.password = dbinfo.getPassword();
		this.dbName = dbinfo.getDbName();

		this.connection = connectMongoDB();
	}

	public boolean isConnected(){
		return (connection != null);
	}

	// mongodb uri creator using srv
	private String URICreator(String username, String password, String hostname) {
		return "mongodb+srv://" + username + ":" + password + "@" + hostname + "/?retryWrites=true&w=majority";
	}
	private MongoDatabase connectMongoDB() {
		String encodedPW;
		try {
			encodedPW = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
		}
		catch (Exception ex) {
			throw new MongoClientException("Password encoding error!!");
		}

		try {
			MongoClient mongoClient = MongoClients.create(URICreator(username, encodedPW, host));
			return mongoClient.getDatabase(this.dbName);
		}
		catch (MongoClientException ex) {
			throw new MongoClientException("Connection to db error!!");
		}
	}

	public void loadMongoTables() {
		for (String tablename : this.getConnection().listCollectionNames()) {
			tables.put(tablename, this.loadTable(tablename));
		}
	}
}
