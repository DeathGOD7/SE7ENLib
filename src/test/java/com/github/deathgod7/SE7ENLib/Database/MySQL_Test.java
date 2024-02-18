// This file is part of SE7ENLib, created on 18/02/2024 (18:10 PM)
// Name : MySQL_Test
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.Database;

import com.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQL_Test {
	@Test
	@Disabled
	@DisplayName("Test : MySQL database")
	public void MySQLDatabaseConnection() {
		DatabaseInfo dbInfo = new DatabaseInfo("test", "http://localhost", "root", "", DatabaseManager.DatabaseType.MySQL);
		DatabaseManager dbManager = new DatabaseManager(dbInfo);

		MySQL db = dbManager.getMySQL();
		Connection con = db.getConnection();

		Column pk = new Column("id", DatabaseManager.DataType.INTEGER);
		pk.setAutoIncrement(true);

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

//		for (Table tb: DatabaseManager.getInstance().getTables().values()) {
//			System.out.println(tb.getName());
//			System.out.println(tb.getPrimaryKey().getName());
//		}


		Column vpk = new Column("id", 2,  DatabaseManager.DataType.INTEGER);
		Column vfirst = new Column("varchars", "meow?data", DatabaseManager.DataType.VARCHAR);
		Column vsecond = new Column("integars", 169, DatabaseManager.DataType.INTEGER);
		Column vthird = new Column("floats", 25691.7, DatabaseManager.DataType.FLOAT);
		Column vfourth = new Column("texts", "damn it reallyyy works...again v2", DatabaseManager.DataType.TEXT);

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
