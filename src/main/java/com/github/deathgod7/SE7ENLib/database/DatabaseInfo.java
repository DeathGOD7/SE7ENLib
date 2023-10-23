// This file is part of SE7ENLib, created on 06/10/2023 (03:30 AM)
// Name : DatabaseInfo
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.database;

import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;

public class DatabaseInfo {
	// common
	private final DatabaseType _dbType;

	/**
	 * Returns the database type
	 * @return {@link DatabaseManager.DatabaseType DatabseType}
	 * @since 1.0
	 */
	public DatabaseType getDbType() {
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

	/**
	 * database Info constructor for SQLite database
	 * @param dbname name of the database file
	 * @param dbdir path where the database file is located
	 * @since 1.0
	 */
	public DatabaseInfo(String dbname, String dbdir) {
		_dbType = DatabaseType.SQLite;
		_dbName = dbname;
		_dirDB = dbdir;
	}

	/**
	 * database Info constructor for MySQL database
	 * @param dbname name of the database
	 * @param host host name of the database
	 * @param port port number of the database
	 * @param username username used to login into the databse
	 * @param password password of the given username
	 * @since 1.0
	 */
	public DatabaseInfo(String dbname, String host, String username, String password) {
		_dbType = DatabaseType.MySQL;
		_dbName = dbname;
		_hostAddress = host;
		_username = username;
		_password = password;
	}

}
