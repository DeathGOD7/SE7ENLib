// This file is part of SE7ENLib, created on 18/02/2024 (18:11 PM)
// Name : MongoDB_Test
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.Database;

import com.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class MongoDB_Test {
	@Test
	//@Disabled
	@DisplayName("Test : MongoDB database")
	public void MongoDatabaseConnection() {

		final String host = Dotenv.load().get("MONGO_HOST");
		final String username = Dotenv.load().get("MONGO_USERNAME");
		final String password = Dotenv.load().get("MONGO_PASSWORD");

		DatabaseInfo dbInfo = new DatabaseInfo("sample_training", host, username, password, DatabaseManager.DatabaseType.MongoDB);
		DatabaseManager dbManager = new DatabaseManager(dbInfo);

		MongoDB mongo = dbManager.getMongoDB();
		MongoDatabase db = mongo.getConnection();

		System.out.println("Connected to MongoDB : " + db.getName());
		long collectionCount = 0;
		for (String collectionName : mongo.getTables().keySet()) {
			System.out.println("\nCollection Name : " + collectionName);
			Column pkey = mongo.getTables().get(collectionName).getPrimaryKey();
			System.out.println("Primary Key : " + pkey.getName() + " | Key Type : " + pkey.getDataType());
			for (Column con : mongo.getTables().get(collectionName).getColumns()) {
				System.out.println("Column Name : " + con.getName() + " | Column Type : " + con.getDataType());
			}
			collectionCount++;
		}

		// Print the collection count
		System.out.println("Number of Collections in Database: " + collectionCount);


	}
}
