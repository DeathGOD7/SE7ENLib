// This file is part of SE7ENLib, created on 18/02/2024 (18:10 PM)
// Name : SQLite_Test
// Author : Death GOD 7

package io.github.deathgod7.SE7ENLib.Database;

import io.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLite_Test {
	public String GetDatabasePath() {
		Path resourceDirectory = Paths.get("src","test","resources", "tempdatabase");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath().replace('\\', '/');

		System.out.println(absolutePath);
		return absolutePath;
	}

	@Test
	@Disabled
	@DisplayName("Test : SQLite database")
	public void SQLiteDatabaseConnection() {
		String path = GetDatabasePath();
		File directory = new File(path);

		if (!directory.exists()) {
			// Attempt to create the directory
			boolean created = directory.mkdirs();
		}

		DatabaseInfo dbInfo = new DatabaseInfo("mydatabase", path);
		DatabaseManager dbManager = new DatabaseManager(dbInfo);

		SQLite db = dbManager.getSQLite();
		Connection con = db.getConnection();




		Column pk = new Column("id", DatabaseManager.DataType.INTEGER);

		Column first = new Column("varchars", DatabaseManager.DataType.VARCHAR);
		first.setLimit(10);
		first.setDefaultValue("defaultv");
		first.setNullable(false);
		Column second = new Column("integars", DatabaseManager.DataType.INTEGER);
		second.setDefaultValue(10);
		Column third = new Column("floats", DatabaseManager.DataType.FLOAT);
		third.setDefaultValue(15.00);
		Column fourth = new Column("texts", DatabaseManager.DataType.TEXT);
		fourth.setDefaultValue("HELLOOOOO WORLD!!");

		Collection<Column> tempp = new ArrayList<>();
		tempp.add(first);
		tempp.add(second);
		tempp.add(third);
		tempp.add(fourth);

		Table table = new Table("tempdb", pk, tempp);
		db.createTable(table, DatabaseManager.getInstance().getDbInfo().getDbType());


		Column vpk = new Column("id", 1,  DatabaseManager.DataType.INTEGER);
		Column vfirst = new Column("varchars", "meow?data", DatabaseManager.DataType.VARCHAR);
		Column vsecond = new Column("integars", 169, DatabaseManager.DataType.INTEGER);
		Column vthird = new Column("floats", 25691.7, DatabaseManager.DataType.FLOAT);
		Column vfourth = new Column("texts", "damn it reallyyy works...again v2", DatabaseManager.DataType.TEXT);

		List<Column> vtempp = new ArrayList<>();
		vtempp.add(vfirst);
		vtempp.add(vsecond);
		vtempp.add(vthird);
		vtempp.add(vfourth);

		int count = 0;

		for (List<Column> tt : db.getAllDatas("tempdb")) {
			System.out.println("Row : " + ++count);
			for (Column c: tt) {
				System.out.println(c.getName() + " : " + c.getValue());
			}
		}

//		db.insertData("tempdb", vtempp);
		//db.updateData("tempdb", vpk, vtempp);
//		List<List<Column>> test = db.findData("tempdb", vsecond);
//		List<List<Column>> test = db.getAllDatas("tempdb");

//		for (List<Column> temp : test) {
//			for (Column c: temp) {
//				System.out.println(c.getName() + " : " + c.getValue());
//			}
//		}


	}


}
