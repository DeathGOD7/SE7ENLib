// This file is part of SE7ENLib, created on 17/02/2024 (14:07 PM)
// Name : MongoOperations
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.database.handler;

import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.dbtype.mongodb.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;

public class MongoOperations implements DatabaseOperations {

	public boolean collectionExists(String collectionName, MongoDatabase database) {
		for (String existingCollection : database.listCollectionNames()) {
			if (existingCollection.equals(collectionName)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * For loading the databases tables
	 *
	 * @param tablename The name of table to load (usually obtained from Database Type
	 * @return {@link Table}
	 */
	@Override
	public Table loadTable(String tablename) {
		MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();
		MongoCollection<Document> collection;
		if (this.collectionExists(tablename, db)) {
			collection = db.getCollection(tablename);
			HashMap<String, Column> columns = new HashMap<>();
			String _pKey = "_id";
			Column primaryKey = new Column(_pKey, DatabaseManager.DataType.TEXT);

			// iterate each document of the collection
			Document dc = collection.find().first();
			assert dc != null;
			for (String key : dc.keySet()) {
				if (key.equals(_pKey)) {
					continue;
				}
				Class<?> dataType = dc.get(key).getClass();
				//System.out.println("Key : " + key + " Original DataType : " + dataType.getName());
				Column column = new Column(key, DatabaseManager.getInstance().parseDataTypeClass(dataType));
				columns.put(key, column);
			}

			return new Table(tablename, primaryKey, columns.values());

		}
		else {
			return null;
		}
	}

	/**
	 * Used for creating tables in the database
	 *
	 * @param table  The table to create (also look at {@link Table})
	 * @param dbtype
	 * @return {@link boolean}
	 */
	@Override
	public boolean createTable(Table table, DatabaseManager.DatabaseType dbtype) {
		return false;
	}

	/**
	 * Used for deleting the table from database
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link boolean}
	 */
	@Override
	public boolean dropTable(String tablename) {
		return false;
	}

	/**
	 * For inserting the data in the table
	 *
	 * @param tablename The name of the table in the database
	 * @param columns   Usually known as the row of data ({@link List <>}<{@link Column}> = Row)
	 * @return {@link boolean}
	 */
	@Override
	public boolean insertData(String tablename, List<Column> columns) {
		return false;
	}

	/**
	 * For updating the data in the table
	 *
	 * @param tablename  The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @param columns    The column value to update (either full row or just one column)
	 * @return {@link boolean}
	 */
	@Override
	public boolean updateData(String tablename, Column primaryKey, List<Column> columns) {
		return false;
	}

	/**
	 * Delete the data from the table
	 *
	 * @param tablename  The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link boolean}
	 */
	@Override
	public boolean deleteData(String tablename, Column primaryKey) {
		return false;
	}

	/**
	 * @param tablename  The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link List<>}<{@link Column}>
	 */
	@Override
	public List<Column> getExactData(String tablename, Column primaryKey) {
		return null;
	}

	/**
	 * @param tablename The name of the table in the database
	 * @param column    The column data to search for
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	@Override
	public List<List<Column>> findData(String tablename, Column column) {
		return null;
	}

	/**
	 * Get all the data in given table
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	@Override
	public List<List<Column>> getAllDatas(String tablename) {
		return null;
	}
}
