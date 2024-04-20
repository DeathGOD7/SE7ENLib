// This file is part of SE7ENLib, created on 06/10/2023 (03:30 AM)
// Name : DatabaseInfo
// Author : Death GOD 7

package io.github.deathgod7.SE7ENLib.database;


/**
 * Represents the Database Information
 * @version 1.0
 * @since 1.0
 */
public class DatabaseInfo {
	// common
	private final DatabaseManager.DatabaseType _dbType;

	/**
	 * Returns the database type
	 * @return {@link DatabaseManager.DatabaseType DatabseType}
	 * @since 1.0
	 */
	public DatabaseManager.DatabaseType getDbType() {
		return _dbType;
	}
	private String _dbName = "";

	/**
	 * Returns the database file if sqlite or database name if mysql
	 * @return {@link String}
	 */
	public String getDbName() {
		return _dbName;
	}

	// mysql
	private String _hostAddress = "";

	/**
	 * Returns the host of database (usually IP or domain address along with port)
	 * @return {@link String}
	 */
	public String getHostAddress() {
		return _hostAddress;
	}

	private String _username = "";

	/**
	 * Returns the username used to connect to database
	 * @return {@link String}
	 */
	public String getUsername() {
		return _username;
	}
	private String _password = "";

	/**
	 * Returns the password of the given username
	 * @return {@link String}
	 */
	public String getPassword() {
		return _password;
	}

	// sqlite
	private String _dirDB = "";

	/**
	 * Returns the path where database is located (only for SQLite)
	 * @return {@link String}
	 */
	public String getDirDb() {
		return _dirDB;
	}

	private PoolSettings _poolSettings;
	/**
	 * Returns the pool settings
	 * @return {@link PoolSettings}
	 */
	public PoolSettings getPoolSettings() {
		return _poolSettings;
	}

	/**
	 * Database Info constructor for SQLite database
	 * @param dbname name of the database file
	 * @param dbdir path where the database file is located
	 * @since 1.0
	 */
	public DatabaseInfo(String dbname, String dbdir) {
		_dbType = DatabaseManager.DatabaseType.SQLite;
		_dbName = dbname;
		_dirDB = dbdir;
	}

	/**
	 * Database Info constructor for MySQL or MongoDB database
	 * @param dbname name of the database
	 * @param host host name of the database
	 * @param username username used to login into the databse
	 * @param password password of the given username
	 * @param dbtype type of database
	 * @param poolSettings pool settings
	 * @since 1.0
	 */
	public DatabaseInfo(String dbname, String host, String username, String password, DatabaseManager.DatabaseType dbtype, PoolSettings poolSettings) {
		_dbType = dbtype;
		_dbName = dbname;
		_hostAddress = host;
		_username = username;
		_password = password;
		_poolSettings = poolSettings;
	}

}
