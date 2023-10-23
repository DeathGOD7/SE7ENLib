package com.github.deathgod7.SE7ENLib.database;

import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import com.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import com.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
	private static DatabaseManager _dbmInstance;
	public static DatabaseManager getInstance() {
		return _dbmInstance;
	}

	private SQLite _sqlite;
	public SQLite getSQLite() { return this._sqlite; }
	private MySQL _mysql;
	public MySQL getMySQL() { return this._mysql; }

	private MongoDB _mongodb;
	public MongoDB getMongoDB() {
		return this._mongodb;
	}

	private DatabaseInfo _dbInfo;
	public DatabaseInfo getDbInfo() {
		return this._dbInfo;
	}

	public boolean isConnected() {
		return (this.getConnection() != null);
	}

	public Connection getConnection() {
		if (_dbInfo.getDbType() == DatabaseType.SQLite) {
			return  _sqlite.getConnection();
		}
		else if (_dbInfo.getDbType() == DatabaseType.MySQL){
			// to do mysql support
			return _mysql.getConnection();
		}
		else {
			return null;
		}
	}

	public String getDBName() {
		return this._dbInfo.getDbName();
	}

	public enum DataType {
		VARCHAR,
		TEXT,
		INTEGER,
		BOOLEAN,
		FLOAT,
		DOUBLE,
		DATE,
		TIME,
		DATETIME
	}

	public enum OrderType {
		ASCENDING,
		DESCENDING
	}

	public enum DatabaseType {
		MySQL,
		SQLite,
		MongoDB
	}

	public DatabaseManager(DatabaseInfo dbinfo){
		_dbmInstance = this;
		loadDB(dbinfo);
	}


	public void closeConnection(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

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
		else {
			throw new RuntimeException("Invalid or unsupported database type given!");
		}
	}



	public DataType parseDataTypeString(String dtype) {
		if (dtype.toUpperCase().contains("INTEGER") || dtype.toUpperCase().contains("INT")) {
			return DataType.INTEGER;
		}
		else if (dtype.toUpperCase().contains("VARCHAR")) {
			return DataType.VARCHAR;
		}
		else if (dtype.toUpperCase().contains("TEXT")) {
			return DataType.TEXT;
		}
		else if (dtype.toUpperCase().contains("BOOLEAN")) {
			return DataType.BOOLEAN;
		}
		else if (dtype.toUpperCase().contains("FLOAT") || dtype.toUpperCase().contains("REAL")) {
			return DataType.FLOAT;
		}
		else if (dtype.toUpperCase().contains("DOUBLE")) {
			return DataType.DOUBLE;
		}
		else if (dtype.toUpperCase().contains("DATE")) {
			return DataType.DATE;
		}
		else if (dtype.toUpperCase().contains("TIME")) {
			return DataType.TIME;
		}
		else if (dtype.toUpperCase().contains("DATETIME")) {
			return DataType.DATETIME;
		}
		else {
			return null;
		}
	}

	public HashMap<String, Table> getTables() {
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
