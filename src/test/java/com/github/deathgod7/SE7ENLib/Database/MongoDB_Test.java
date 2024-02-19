// This file is part of SE7ENLib, created on 18/02/2024 (18:11 PM)
// Name : MongoDB_Test
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.Database;

import com.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;


public class MongoDB_Test {
	@Test
	//@Disabled
	@DisplayName("Test : MongoDB database")
	public void MongoDatabaseConnection() {

		final String host = Dotenv.load().get("MONGO_HOST");
		final String username = Dotenv.load().get("MONGO_USERNAME");
		final String password = Dotenv.load().get("MONGO_PASSWORD");

		DatabaseInfo dbInfo = new DatabaseInfo("sample_training", host, username, password, DatabaseType.MongoDB);
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

		Column pk = new Column("usr_id", DataType.INTEGER);

		Table table = getTable(pk);

		//db.getCollection("tempdb").drop();

		//mongo.createTable(table, DatabaseType.MongoDB);


	}

	private static Table getTable(Column pk) {
		Column first = new Column("varchars", DataType.VARCHAR);
		first.setLimit(10);
		first.setDefaultValue("defaultv");
		first.setNullable(false);
		Column second = new Column("integers", DataType.INTEGER);
		second.setDefaultValue(10);
		Column third = new Column("floats", DataType.FLOAT);
		third.setDefaultValue(15.00);
		Column fourth = new Column("texts", DataType.TEXT);
		fourth.setDefaultValue("HELLOOOOO WORLD!!");
		fourth.setNullable(false);

		Column f_1 = new Column("f_1", DataType.ARRAY);
		f_1.setNullable(false);
		Column f_2 = new Column("f_2", DataType.DOCUMENT);
		Column f_3 = new Column("f_3", DataType.DOUBLE);
		f_3.setNullable(false);
		Column f_4 = new Column("f_4", DataType.TEXT);
		List<Column> f_t = Arrays.asList(f_1, f_2, f_3, f_4);


		Column fifth = new Column("multidocs", f_t, DataType.DOCUMENT);

		Collection<Column> tempp = new ArrayList<>();
		tempp.add(first);
		tempp.add(second);
		tempp.add(third);
		tempp.add(fourth);
		tempp.add(fifth);

		return new Table("tempdb", pk, tempp);
	}

	private static Table getTableData(Column pk) {
		Column first = new Column("varchars", DataType.VARCHAR);
		first.setValue("mongodbbbbbb");
		Column second = new Column("integers", DataType.INTEGER);
		second.setValue(10);
		Column third = new Column("floats", DataType.FLOAT);
		third.setValue(15.00);
		Column fourth = new Column("texts", DataType.TEXT);
		fourth.setValue("HELLOOOOO MONGOOOOOOOOOO!!");

		Column f_1 = new Column("f_1", DataType.ARRAY);
		f_1.setValue(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		Column f_2 = new Column("f_2", DataType.DOCUMENT);
		Column f_3 = new Column("f_3", DataType.DOUBLE);
		f_3.setValue(1.00);
		Column f_4 = new Column("f_4", DataType.TEXT);
		f_4.setValue("Looks good to me!!!");
		List<Column> f_t = Arrays.asList(f_1, f_2, f_3, f_4);


		Column fifth = new Column("multidocs", f_t, DataType.DOCUMENT);

		Collection<Column> tempp = new ArrayList<>();
		tempp.add(first);
		tempp.add(second);
		tempp.add(third);
		tempp.add(fourth);
		tempp.add(fifth);

		pk.setValue(1);

		return new Table("tempdb", pk, tempp);
	}


}
