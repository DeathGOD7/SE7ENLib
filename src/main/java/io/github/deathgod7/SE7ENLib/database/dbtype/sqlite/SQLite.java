package io.github.deathgod7.SE7ENLib.database.dbtype.sqlite;

import io.github.deathgod7.SE7ENLib.Logger;
import io.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.handler.SQLOperations;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the SQLite Database
 * @version 1.0
 * @since 1.0
 */
public class SQLite extends SQLOperations {
	private final String dbName;
	private final String dirDB;
	private Connection connection;

	// old name = sqlite_master ( < 3.33.0 ) // works in all newer version
	// new name = sqlite_schema ( >= 3.33.0 ) // won't work in older version

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


	/**
	 * Check if the database is connected
	 * @return {@link Boolean}
	 */
	public boolean isConnected(){
		return (connection != null);
	}

	/**
	 * Get the connection
	 * @return {@link Connection}
	 */
	public Connection getConnection() {
		return connection;
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
	 * @param table {@link Table} The Table object to add
	 */
	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}

	/**
	 * Remove the table from table list
	 * @param tablename {@link String} The name of the table to remove
	 */
	public void removeTable(String tablename) {
		tables.remove(tablename);
	}

	/**
	 * Create a new SQLite object
	 * @param dbName {@link String} The name of the database file
	 * @param directory {@link String} The directory of the database file
	 */
	public SQLite(String dbName, String directory){
		this.dbName = dbName;
		this.dirDB = directory;
		this.connection = connectSQLite();
		this.dbInfo = new DatabaseInfo(dbName, directory);
	}

	/**
	 * Create a new SQLite object
	 * @param dbInfo {@link DatabaseInfo} The DatabaseInfo object
	 */
	public SQLite(DatabaseInfo dbInfo){
		this.dbInfo = dbInfo;
		this.dbName = dbInfo.getDbName();
		this.dirDB = dbInfo.getDirDb();
		this.connection = connectSQLite();
	}

	private Connection connectSQLite() {
		Connection temp;
		File dbFile = new File(this.dirDB + "/" + dbName + ".db");

		if (!dbFile.exists()) {
			try {
				boolean filecreate = dbFile.createNewFile();
				if (!filecreate) {
					Logger.log("[ERROR] Unable to create database file");
				}

			} catch (IOException ex) {
				Logger.log("[ERROR] " + ex.getMessage());
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
			Logger.log("[ERROR] " + ex.getMessage());
			return null;
		}
	}

	/**
	 * Close the SQL connection
	 */
	public void closeConnection() {
		if (isConnected()) {
			try {
				if (!(connection.isClosed())) {
					connection.close();
				}
				connection = null;
			} catch (SQLException ex) {
				Logger.log("[ERROR] " + ex.getMessage());
			}
		}
	}

	/**
	 * Load the SQLite tables
	 */
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
			Logger.log("[ERROR] " + ex.getMessage());
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
			Logger.log("[ERROR] " + ex.getMessage());
		}
	}

	/**
	 * Load the table from the database
	 * @param tablename The name of the table
	 * @return {@link Table}
	 */
	public Table loadTable(String tablename) {
		// query all the column name and its type
		// String query = "SELECT * FROM pragma_table_info('" + tablename +"');";
		String query = "PRAGMA table_info(`" + tablename + "`);";
		LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
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

				DatabaseManager.DataType dataType = parseDataTypeString(type);
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
			Logger.log("[ERROR] " + ex.getMessage());
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
