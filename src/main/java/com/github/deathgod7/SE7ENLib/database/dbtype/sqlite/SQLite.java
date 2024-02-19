package com.github.deathgod7.SE7ENLib.database.dbtype.sqlite;

import com.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import com.github.deathgod7.SE7ENLib.database.handler.SQLOperations;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLite extends SQLOperations {

	private final String dbName;
	private final String dirDB;
	private Connection connection;

	// old name = sqlite_master ( < 3.33.0 ) // works in all newer version
	// new name = sqlite_schema ( >= 3.33.0 ) // won't work in older version

	public String getDBName(){
		return dbName;
	}

	public boolean isConnected(){
		return (connection != null);
	}

	public Connection getConnection() {
		return  connection;
	}

	private final HashMap<String, Table> tables = new HashMap<>();
	public HashMap<String, Table> getTables() {
		return tables;
	}

	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}

	public void removeTable(String tablename) {
		tables.remove(tablename);
	}
	public SQLite(String dbName, String directory){
		this.dbName = dbName;
		this.dirDB = directory;
		this.connection = connectSQLite();
	}

	public SQLite(DatabaseInfo dbInfo){
		this.dbName = dbInfo.getDbName();
		this.dirDB = dbInfo.getDirDb();
		this.connection = connectSQLite();
	}

	private Connection connectSQLite() {
		Connection temp;
		File dbFile = new File(this.dirDB + "/" + dbName + ".db");

		if (!dbFile.exists()) {
			try {
				dbFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			temp = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			return temp;
		} catch (SQLException | ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void closeConnection() {
		if (isConnected()) {
			try {
				if (!(connection.isClosed())) {
					connection.close();
					connection = null;
				} else {
					connection = null;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void loadSqliteTables() {
		String sqlitever;
		int[] sqliteverformatted = new int[3];

		try {
			// Crrate a query
			String query = "SELECT sqlite_version()";

			// Create a statement
			PreparedStatement ps = this.getConnection().prepareStatement(query);

			// Execute the query to retrieve the SQLite version
			ResultSet resultSet = ps.executeQuery();

			// Retrieve the result
			if (resultSet.next()) {
				sqlitever = resultSet.getString(1);
				ps.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		// old name = sqlite_master ( < 3.33.0 ) // works in all newer version
		// new name = sqlite_schema ( >= 3.33.0 ) // won't work in older version
		String schematable = "sqlite_master";

		String query = "SELECT tbl_name FROM " + schematable +
				" WHERE type = 'table' AND name NOT LIKE 'sqlite_%' " +
				"ORDER BY tbl_name";
		try {
			PreparedStatement ps = this.getConnection().prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tablename = rs.getString(1); // rs.getString("tbl_name");
				tables.put(tablename, this.loadTable(tablename));
			}
			ps.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	// sqlite based querys
	public Table loadTable(String tablename) {
		// query all the column name and its type
		String query = "SELECT * FROM pragma_table_info('" + tablename +"');";
		HashMap<String, Column> columns = new HashMap<>();
		String primarykey = "";

		try {
			PreparedStatement ps = this.getConnection().prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				String type = rs.getString("type");
				int notNull = rs.getInt("notnull");
				String defaultValue = rs.getString("dflt_value");

				if (rs.getInt("pk") == 1) {
					primarykey = name;
				}

				DataType dataType = parseDataTypeString(type);
				Column column = new Column(name, dataType);
				column.setNullable(notNull != 1);
				column.setAutoIncrement(primarykey.equals(name));
				column.setDefaultValue(defaultValue);
				column.setLimit(getLimitFromText(type));

				columns.put(name, column);
			}
			ps.close();
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}

		// create table scructure
		Column primaryKey = columns.get(primarykey);
		columns.remove(primarykey);

		return new Table(tablename, primaryKey, columns.values());
	}

	private int getLimitFromText(String input) {
		// Define a regular expression pattern to match the integer within parentheses
		Pattern pattern = Pattern.compile("\\((\\d+)\\)");

		// Create a matcher object
		Matcher matcher = pattern.matcher(input);

		int result = 0;
		// Check if the pattern matches
		if (matcher.find()) {
			// Extract the integer from the first capturing group
			String intValue = matcher.group(1);

			// Parse the string to an integer
			result = Integer.parseInt(intValue);

		}

		return result;
	}

}
