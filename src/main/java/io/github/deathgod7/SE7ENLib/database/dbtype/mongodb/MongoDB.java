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

	/**
	 * Get the database name
	 * @return {@link String}
	 */
	public String getDBName(){
		return dbName;
	}

	private final DatabaseInfo dbInfo;

	/**
	 * Get the database information
	 * @return {@link DatabaseInfo}
	 */
	public DatabaseInfo getDbInfo() {
		return dbInfo;
	}

	private final MongoDatabase connection;
	/**
	 * Get the connection
	 * @return {@link MongoDatabase}
	 */
	public MongoDatabase getConnection(){
		return  connection;
	}
	/**
	 * Check if the database is connected
	 * @return {@link Boolean}
	 */
	public boolean isConnected(){
		return (connection != null);
	}
	private final LinkedHashMap<String, Table> tables = new LinkedHashMap<>();
	/**
	 * Get the tables
	 * @return {@link LinkedHashMap}
	 */
	public LinkedHashMap<String, Table> getTables() {
		return tables;
	}

	/**
	 * Add the table in table list
	 * @param table The Table object to add
	 */
	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}

	/**
	 * Remove the table from table list
	 * @param tablename The name of the table to remove
	 */
	public void removeTable(String tablename) {
		tables.remove(tablename);
	}

	/**
	 * Creates a new MongoDB
	 * @param dbinfo The DatabaseInfo object
	 */
	public MongoDB(DatabaseInfo dbinfo){
		this.dbInfo = dbinfo;
		this.host = dbinfo.getHostAddress();
		this.username = dbinfo.getUsername();
		this.password = dbinfo.getPassword();
		this.dbName = dbinfo.getDbName();
		this.connection = connectMongoDB();
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

	/**
	 * Load the tables from the database
	 */
	public void loadMongoTables() {
		for (String tablename : this.getConnection().listCollectionNames()) {
			tables.put(tablename, this.loadTable(tablename));
		}
	}

}
