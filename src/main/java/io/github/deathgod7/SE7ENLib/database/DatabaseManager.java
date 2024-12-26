package io.github.deathgod7.SE7ENLib.database;

import io.github.deathgod7.SE7ENLib.Logger;
import io.github.deathgod7.SE7ENLib.database.component.Table;
import io.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import io.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import io.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;

import java.sql.*;
import java.util.*;

/**
 * Represents the Database Manager
 * @version 1.0
 * @since 1.0
 */
public class DatabaseManager {
	private static DatabaseManager _dbmInstance;
	/**
	 * Get the instance of DatabaseManager
	 * @return {@link DatabaseManager}
	 */
	public static DatabaseManager getInstance() {
		return _dbmInstance;
	}

	private SQLite _sqlite;
	/**
	 * Get the SQLite
	 * @return {@link SQLite}
	 */
	public SQLite getSQLite() { return this._sqlite; }
	private MySQL _mysql;
	/**
	 * Get the MySQL
	 * @return {@link MySQL}
	 */
	public MySQL getMySQL() { return this._mysql; }

	private MongoDB _mongodb;
	/**
	 * Get the MongoDB
	 * @return {@link MongoDB}
	 */
	public MongoDB getMongoDB() {
		return this._mongodb;
	}

	private DatabaseInfo _dbInfo;

	/**
	 * Get the DatabaseInfo
	 * @return {@link DatabaseInfo}
	 */
	public DatabaseInfo getDbInfo() {
		return this._dbInfo;
	}

	/**
	 * Check if the database is connected
	 * @return {@link Boolean}
	 */
	public boolean isConnected() {
		if (_dbInfo.getDbType() == DatabaseType.SQLite || _dbInfo.getDbType() == DatabaseType.MySQL) {
			try (Connection con = (Connection) this.getConnection()) {
				return con != null;
			} catch (SQLException ex) {
				Logger.log("[CONNECTION CHECK ERROR] " + ex.getMessage());
				return false;
			}
		}
		else if (_dbInfo.getDbType() == DatabaseType.MongoDB) {
			return this.getMongoDB().isConnected();
		}
		else {
			return false;
		}
	}

	private boolean debugMode = false;

	/**
	 * Set the debug mode
	 * @param value The value to set
	 */
	public void setDebugMode(boolean value) {
		this.debugMode = value;
	}

	/**
	 * Check the debug mode
	 * @return {@link Boolean}
	 */
	public boolean getDebugMode() {
		return this.debugMode;
	}

	/**
	 * Get the connection of database
	 * @return {@link Object}
	 */
	public Object getConnection() {
		if (_dbInfo.getDbType() == DatabaseType.SQLite) { return this.getSQLite().getConnection(); }
		else if (_dbInfo.getDbType() == DatabaseType.MySQL){ return this.getMySQL().getConnection(); }
		else if (_dbInfo.getDbType() == DatabaseType.MongoDB) { return this.getMongoDB().getConnection(); }
		else { return null; }
	}

	/**
	 * Get the database (all)
	 * @return {@link Object}
	 */
	public Object getDatabase() {
		if (_dbInfo.getDbType() == DatabaseType.SQLite) { return this.getSQLite(); }
		else if (_dbInfo.getDbType() == DatabaseType.MySQL){ return this.getMySQL(); }
		else if (_dbInfo.getDbType() == DatabaseType.MongoDB) { return this.getMongoDB(); }
		else { return null; }
	}

	/**
	 * Get the database name
	 * @return {@link String}
	 */
	public String getDBName() {
		return this._dbInfo.getDbName();
	}

	/**
	 * Represents the Data Type
	 */
	public enum DataType {
		/**
		 * Varchar Data Type
		 */
		VARCHAR,
		/**
		 * Text Data Type
		 */
		TEXT,
		/**
		 * Integer Data Type
		 */
		INTEGER,
		/**
		 * Boolean Data Type
		 */
		BOOLEAN,
		/**
		 * Float Data Type
		 */
		FLOAT,
		/**
		 * Double Data Type
		 */
		DOUBLE,
		/**
		 * Date Data Type
		 */
		DATE,
		/**
		 * Time Data Type
		 */
		TIME,
		/**
		 * DateTime Data Type
		 */
		DATETIME,

		// MongoDB Special
		/**
		 * Array Data Type
		 */
		ARRAY,
		/**
		 * Document Data Type
		 */
		DOCUMENT,
		/**
		 * ObjectID Data Type
		 */
		OBJECTID
	}

	/**
	 * Represents the Order Type
	 */
	public enum OrderType {
		/**
		 * Ascending Order
		 */
		ASCENDING,
		/**
		 * Descending Order
		 */
		DESCENDING
	}

	/**
	 * Represents the Database Type
	 */
	public enum DatabaseType {
		/**
		 * SQLite Database
		 */
		MySQL,
		/**
		 * MySQL Database
		 */
		SQLite,
		/**
		 * MongoDB Database
		 */
		MongoDB
	}

	/**
	 * DatabaseManager constructor that takes DatabaseInfo
	 * @param dbinfo The DatabaseInfo object
	 */
	public DatabaseManager(DatabaseInfo dbinfo){
		_dbmInstance = this;
		loadDB(dbinfo);
	}

	/**
	 * Close the SQL connection
	 * @param ps The PreparedStatement
	 * @param rs The ResultSet
	 */
	public void closeConnection(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			Logger.log("[CON CLOSE ERROR] " + ex.getMessage());
		}
	}

	/**
	 * Close the SQL connection for HikariPool and/or SQLite too (if useful)
	 * @param con The connection to close
	 */
	public void closeConnection(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (SQLException ex) {
			Logger.log("[CON CLOSE ERROR] " + ex.getMessage());
		}
	}

	/**
	 * Load the database based on the DatabaseInfo
	 * @param dbinfo The DatabaseInfo object provided while creating instance of this class
	 */
	private void loadDB(DatabaseInfo dbinfo) {
		_dbInfo = dbinfo;

		if (dbinfo.getDbType() == DatabaseType.SQLite){
			_sqlite = new SQLite(dbinfo);
			_sqlite.loadSqliteTables();
		}
		else if (dbinfo.getDbType() == DatabaseType.MySQL){
			// to do mysql support
			_mysql = new MySQL(dbinfo);
			_mysql.loadMysqlTables();
		}
		else if (dbinfo.getDbType() == DatabaseType.MongoDB){
			_mongodb = new MongoDB(dbinfo);
			_mongodb.loadMongoTables();
		}
		else {
			throw new RuntimeException("Invalid or unsupported database type given!");
		}
	}

	/**
	 * Get the tables from the database
	 * @return {@link LinkedHashMap}
	 */
	public LinkedHashMap<String, Table> getTables() {
		if (_dbInfo.getDbType() == DatabaseType.SQLite) {
			return _sqlite.getTables();
		}
		else if (_dbInfo.getDbType() == DatabaseType.MySQL) {
			return _mysql.getTables();
		}
		else if (_dbInfo.getDbType() == DatabaseType.MongoDB) {
			return _mongodb.getTables();
		}
		else {
			return null;
		}
	}


}
