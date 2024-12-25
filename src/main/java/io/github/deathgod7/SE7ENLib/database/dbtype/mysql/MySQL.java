package io.github.deathgod7.SE7ENLib.database.dbtype.mysql;

import io.github.deathgod7.SE7ENLib.Logger;
import io.github.deathgod7.SE7ENLib.database.DatabaseInfo;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import io.github.deathgod7.SE7ENLib.database.PoolSettings;
import io.github.deathgod7.SE7ENLib.database.handler.SQLOperations;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the MySQL Database
 * @version 1.0
 * @since 1.0
 */
public class MySQL extends SQLOperations {
	private final String host;
	private final String username;
	private final String password;
	private final String dbName;

	private final DatabaseInfo dbInfo;

	/**
	 * Get the database information
	 * @return {@link DatabaseInfo}
	 */
	public DatabaseInfo getDbInfo() {
		return dbInfo;
	}

	/**
	 * Get the database name
	 * @return {@link String}
	 */
	public String getDBName(){
		return dbName;
	}

	private HikariDataSource hikariDataSource;
	/**
	 * Get the connection
	 * @return {@link Connection}
	 */
	public Connection getConnection(){
		try {
			Connection connection = hikariDataSource.getConnection();

			if (isConnectionValid(connection)) { return connection; }
			else {
				Logger.log("[GET CONNECTION || MySQL] Connection invalid. Closing the connection");
				connection.close();
				return null;
			}
		}
		catch (SQLException ex) {
			Logger.log("[GET CON ERROR] " + ex.getMessage());
			return null;
		}
	}

	/**
	 * Check if the connection is valid
	 * @param connection The connection to check
	 * @return {@link Boolean}
	 */
	public boolean isConnectionValid(Connection connection) {
		try {
			return connection.isValid(2);
		} catch (SQLException e) {
			Logger.log("[CON VALID ERROR] " + e.getMessage());
			return false;
		}
	}

	/**
	 * Check if the database is connected or its open
	 * @deprecated Since version 1.1.1, use {@link #isConnectionValid(Connection)} instead
	 * @return {@link Boolean}
	 */
	@Deprecated
	public boolean isConnected(){
		return false;
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
	 * @param table {@link Table}
	 */
	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}
	/**
	 * Remove the table from table list
	 * @param tablename {@link String}
	 */
	public void removeTable(String tablename) {
		tables.remove(tablename);
	}

	/**
	 * Create a new MySQL object
	 * @param dbname {@link String}
	 * @param host {@link String}
	 * @param username {@link String}
	 * @param password {@link String}
	 */
	public MySQL(String dbname, String host, String username, String password){
		this.host = host;
		this.username = username;
		this.password = password;
		this.dbName = dbname;
		this.dbInfo = new DatabaseInfo(dbname, host, username, password, DatabaseType.MySQL, null);
		if (connectMySQL()) {
			Logger.log("[OBJECT CREATION INFO] MySQL connected successfully");
		}else {
			Logger.log("[OBJECT CREATION INFO] MySQL connection failed");
		}
	}

	/**
	 * Create a new MySQL object
	 * @param dbInfo {@link DatabaseInfo}
	 */
	public MySQL(DatabaseInfo dbInfo){
		this.dbInfo = dbInfo;
		this.host = dbInfo.getHostAddress();
		this.username = dbInfo.getUsername();
		this.password = dbInfo.getPassword();
		this.dbName = dbInfo.getDbName();
		if (connectMySQL()) {
			Logger.log("[OBJECT CREATION INFO] MySQL connected successfully");
		}else {
			Logger.log("[OBJECT CREATION INFO] MySQL connection failed");
		}
	}

	private boolean connectMySQL() {
		HikariConfig hikariConfig = new HikariConfig();
		String cleanedUrl = this.host.replaceFirst("^(https?://)?", "");

		hikariConfig.setJdbcUrl("jdbc:mysql://" + cleanedUrl + "/" + this.dbName);
		hikariConfig.setUsername(this.username);
		hikariConfig.setPassword(this.password);
		//config.setDriverClassName("com.mysql.cj.jdbc.Driver");
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.setLeakDetectionThreshold(2000);

		PoolSettings poolSettings = this.dbInfo.getPoolSettings();
		if (poolSettings != null) {
			hikariConfig.setMinimumIdle(poolSettings.getMinIdleConnections());
			hikariConfig.setMaximumPoolSize(poolSettings.getMaxPoolSize());
			hikariConfig.setIdleTimeout(poolSettings.getIdleTimeout());
			hikariConfig.setConnectionTimeout(poolSettings.getConnectionTimeout());
			hikariConfig.setMaxLifetime(poolSettings.getMaxLifetime());
		}

		try {
			hikariDataSource = new HikariDataSource(hikariConfig);
			return true;
		} catch (Exception ex) {
			Logger.log("[ERROR] " + ex.getMessage());
			return false;
		}
	}

	/**
	 * Load the MySQL tables
	 */
	public void loadMysqlTables() {
		String query = "SELECT table_name FROM information_schema.tables " +
				"WHERE table_schema = '"+ this.getDBName() +"' AND table_type = 'base table' " +
				"ORDER BY table_name";
		try (Connection con = this.getConnection()) {
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tablename = rs.getString(1);
				tables.put(tablename, this.loadTable(tablename));
			}
			// close the fricking connection
			// DatabaseManager.getInstance().closeConnection(ps,rs);
			// DatabaseManager.getInstance().closeConnection(con);
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
		String query = "SHOW COLUMNS FROM `" + tablename + "`;";
		LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
		String primarykey = "";

		try (Connection con = this.getConnection()) {
			PreparedStatement ps = con.prepareStatement(query);
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
			// close the fricking connection here too you dumb human
			// DatabaseManager.getInstance().closeConnection(ps,rs);
			// DatabaseManager.getInstance().closeConnection(con);
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
