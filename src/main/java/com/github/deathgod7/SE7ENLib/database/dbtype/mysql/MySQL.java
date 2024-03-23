package com.github.deathgod7.SE7ENLib.database.dbtype.mysql;

import com.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import com.github.deathgod7.SE7ENLib.database.handler.SQLOperations;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQL extends SQLOperations {
	private final String host;
	private final String username;
	private final String password;
	private final String dbName;

	public String getDBName(){
		return dbName;
	}

	private final Connection connection;
	public Connection getConnection(){
		if (connection == null){
			//connection = connectSQLite();
			return  connection;
		}
		return  connection;
	}
	public boolean isConnected(){
		return (connection != null);
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

	public MySQL(String dbname, String host, String username, String password){
		this.host = host;
		this.username = username;
		this.password = password;
		this.dbName = dbname;
		this.connection = connectMySQL();
	}

	public MySQL(DatabaseInfo dbInfo){
		this.host = dbInfo.getHostAddress();
		this.username = dbInfo.getUsername();
		this.password = dbInfo.getPassword();
		this.dbName = dbInfo.getDbName();
		this.connection = connectMySQL();
	}

	private Connection connectMySQL() {
		HikariConfig config = new HikariConfig();
		String cleanedUrl = this.host.replaceFirst("^(https?://)?", "");
		Connection temp;

		config.setJdbcUrl("jdbc:mysql://" + cleanedUrl + "/" + this.dbName);
		config.setUsername(this.username);
		config.setPassword(this.password);
		config.setDriverClassName("com.mysql.cj.jdbc.Driver");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		try {
			if (connection != null && !connection.isClosed()) {
				return connection;
			}

			HikariDataSource dataSource = new HikariDataSource(config);
			temp = dataSource.getConnection();
			return temp;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}


	public void loadMysqlTables() {
		String query = "SELECT table_name FROM information_schema.tables " +
				"WHERE table_schema = '"+ this.getDBName() +"' AND table_type = 'base table' " +
				"ORDER BY table_name";
		try {
			PreparedStatement ps = this.getConnection().prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tablename = rs.getString(1);
				tables.put(tablename, this.loadTable(tablename));
			}
			ps.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	public Table loadTable(String tablename) {
		// query all the column name and its type
		String query = "SHOW COLUMNS FROM " + tablename + ";";
		LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
		String primarykey = "";

		try {
			PreparedStatement ps = this.getConnection().prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("Field");
				String type = rs.getString("Type");
				String isNull = rs.getString("Null");
				String defaultValue = rs.getString("Default");

				DataType dataType = parseDataTypeString(type);
				Column column = new Column(name, dataType);

				if (rs.getString("Key").equalsIgnoreCase("PRI")) {
					primarykey = name;
				}
				//Extra
				if (rs.getString("Extra").equalsIgnoreCase("auto_increment")) {
					column.setAutoIncrement(true);
				}

				column.setNullable(isNull.equalsIgnoreCase("YES"));
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
