// This file is part of SE7ENLib, created on 22/10/2023 (04:55 AM)
// Name : DatabaseOperations
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.database;

import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseOperations {

	/**
	 * For loading the databases tables
	 * @param tablename The name of table to load (usually obtained from Database Type
	 * @return {@link Table}
	 */
	public Table loadTable(String tablename);

	/**
	 * Used for creating tables in the database
	 * @param table The table to create (also look at {@link Table})
	 * @return {@link boolean}
	 */
	public boolean createTable(Table table, DatabaseType dbtype);

	/**
	 * Used for deleting the table from database
	 * @param tablename The name of the table in the database
	 * @return {@link boolean}
	 */
	public boolean dropTable(String tablename);

	// maybe also add func. for altering the table schema ??

	/**
	 * For inserting the data in the table
	 * @param tablename The name of the table in the database
	 * @param columns Usually known as the row of data ({@link List<>}<{@link Column}> = Row)
	 * @return {@link boolean}
	 */
	public boolean insertData(String tablename, List<Column> columns);

	/**
	 * For updating the data in the table
	 * @param tablename The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @param columns The column value to update (either full row or just one column)
	 * @return {@link boolean}
	 */
	public boolean updateData(String tablename, Column primaryKey, List<Column> columns);

	/**
	 * Delete the data from the table
	 * @param tablename The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link boolean}
	 */
	public boolean deleteData(String tablename, Column primaryKey);

	/**
	 * @param tablename The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link List<>}<{@link Column}>
	 */
	public List<Column> getExactData(String tablename, Column primaryKey);

	/**
	 * @param tablename The name of the table in the database
	 * @param column The column data to search for
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	public List<List<Column>> findData(String tablename, Column column);

	/**
	 * Get all the data in given table
	 * @param tablename The name of the table in the database
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	public List<List<Column>> getAllDatas(String tablename);


}
