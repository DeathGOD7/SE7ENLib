// This file is part of SE7ENLib, created on 18/10/2023 (05:32 AM)
// Name : Main
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib;

import com.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import com.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
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


public class DatabaseTest {

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




		Column pk = new Column("id", DataType.INTEGER);

		Column first = new Column("varchars", DataType.VARCHAR);
		first.setLimit(10);
		first.setDefaultValue("defaultv");
		first.setNullable(false);
		Column second = new Column("integars", DataType.INTEGER);
		second.setDefaultValue(10);
		Column third = new Column("floats", DataType.FLOAT);
		third.setDefaultValue(15.00);
		Column fourth = new Column("texts", DataType.TEXT);
		fourth.setDefaultValue("HELLOOOOO WORLD!!");

		Collection<Column> tempp = new ArrayList<>();
		tempp.add(first);
		tempp.add(second);
		tempp.add(third);
		tempp.add(fourth);

		Table table = new Table("tempdb", pk, tempp);
		db.createTable(table, DatabaseManager.getInstance().getDbInfo().getDbType());

//		for (Table tb: DatabaseManager.getInstance().getTables().values()) {
//			System.out.println(tb.getName());
//			System.out.println(tb.getPrimaryKey().getName());
//		}

		Column vpk = new Column("id", 1,  DataType.INTEGER);
		Column vfirst = new Column("varchars", "meow?data", DataType.VARCHAR);
		Column vsecond = new Column("integars", 169, DataType.INTEGER);
		Column vthird = new Column("floats", 25691.7, DataType.FLOAT);
		Column vfourth = new Column("texts", "damn it reallyyy works...again v2", DataType.TEXT);

		List<Column> vtempp = new ArrayList<>();
		vtempp.add(vfirst);
		vtempp.add(vsecond);
		vtempp.add(vthird);
		vtempp.add(vfourth);

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

	@Test
	@Disabled
	@DisplayName("Test : MySQL database")
	public void MySQLDatabaseConnection() {
		DatabaseInfo dbInfo = new DatabaseInfo("test", "http://localhost", "root", "");
		DatabaseManager dbManager = new DatabaseManager(dbInfo);

		MySQL db = dbManager.getMySQL();
		Connection con = db.getConnection();

		Column pk = new Column("id", DataType.INTEGER);
		pk.setAutoIncrement(true);

		Column first = new Column("varchars", DataType.VARCHAR);
		first.setLimit(10);
		first.setDefaultValue("defaultv");
		first.setNullable(false);
		Column second = new Column("integars", DataType.INTEGER);
		second.setDefaultValue(10);
		Column third = new Column("floats", DataType.FLOAT);
		third.setDefaultValue(15.00);
		Column fourth = new Column("texts", DataType.TEXT);
		fourth.setDefaultValue("HELLOOOOO WORLD!!");

		Collection<Column> tempp = new ArrayList<>();
		tempp.add(first);
		tempp.add(second);
		tempp.add(third);
		tempp.add(fourth);

		Table table = new Table("tempdb", pk, tempp);
		db.createTable(table, DatabaseManager.getInstance().getDbInfo().getDbType());

//		for (Table tb: DatabaseManager.getInstance().getTables().values()) {
//			System.out.println(tb.getName());
//			System.out.println(tb.getPrimaryKey().getName());
//		}


		Column vpk = new Column("id", 2,  DataType.INTEGER);
		Column vfirst = new Column("varchars", "meow?data", DataType.VARCHAR);
		Column vsecond = new Column("integars", 169, DataType.INTEGER);
		Column vthird = new Column("floats", 25691.7, DataType.FLOAT);
		Column vfourth = new Column("texts", "damn it reallyyy works...again v2", DataType.TEXT);

		List<Column> vtempp = new ArrayList<>();
		vtempp.add(vfirst);
		vtempp.add(vsecond);
		vtempp.add(vthird);
		vtempp.add(vfourth);

//		db.insertData("tempdb", vtempp);
//		db.updateData("tempdb", vpk, vtempp);
//		List<List<Column>> test = db.getAllDatas("tempdb");

//		for (List<Column> temp : test) {
//			for (Column c: temp) {
//				System.out.println(c.getName() + " : " + c.getValue());
//			}
//		}


	}

}
