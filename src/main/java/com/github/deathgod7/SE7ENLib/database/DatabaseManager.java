package com.github.deathgod7.SE7ENLib.database;

import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import com.github.deathgod7.SE7ENLib.database.dbtype.mysql.MySQL;
import com.github.deathgod7.SE7ENLib.database.dbtype.sqlite.SQLite;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

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

	public Object getConnection() {
		if (_dbInfo.getDbType() == DatabaseType.SQLite) {
			return  _sqlite.getConnection();
		}
		else if (_dbInfo.getDbType() == DatabaseType.MySQL){
			// to do mysql support
			return _mysql.getConnection();
		} else if (_dbInfo.getDbType() == DatabaseType.MongoDB) {
			return _mongodb.getConnection();
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
		DATETIME,

		// MongoDB Special
		ARRAY,
		DOCUMENT,
		OBJECTID
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
		else if (dbinfo.getDbType() == DatabaseType.MongoDB){
			_mongodb = new MongoDB(dbinfo);
			_mongodb.loadMongoTables();
		}
		else {
			throw new RuntimeException("Invalid or unsupported database type given!");
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
