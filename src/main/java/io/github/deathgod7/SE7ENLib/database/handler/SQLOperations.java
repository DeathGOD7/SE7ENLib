// This file is part of SE7ENLib, created on 23/10/2023 (01:09 AM)
// Name : SQLOperations
// Author : Death GOD 7

package io.github.deathgod7.SE7ENLib.database.handler;

import io.github.deathgod7.SE7ENLib.Logger;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import io.github.deathgod7.SE7ENLib.database.component.Column;
import io.github.deathgod7.SE7ENLib.database.component.Table;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the SQL Operations
 * @version 1.0
 * @since 1.0
 */
public abstract class SQLOperations implements DatabaseOperations {

	/**
	 * For loading the databases tables
	 *
	 * @param tablename The name of table to load (usually obtained from Database Type
	 * @return {@link Table}
	 */
	@Override
	public abstract Table loadTable(String tablename);

	/**
	 * Parse the data type string to {@link DataType}
	 *
	 * @param dtype The data type string
	 * @return {@link DataType}
	 */
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

	/**
	 * Parse the {@link DataType} to string
	 *
	 * @param dataType The data type
	 * @return {@link String}
	 */
	public String parseInputDataType(DataType dataType) {
		switch(dataType) {
			case VARCHAR:
				return "VARCHAR";
			case TEXT:
				return "TEXT";
			case INTEGER:
				return "INTEGER";
			case FLOAT:
			case DOUBLE:
				return "FLOAT";
			case BOOLEAN:
				return "BOOLEAN";
			case DATE:
				return "DATE";
			case TIME:
				return "TIME";
			case DATETIME:
				return "DATETIME";
			default:
				return null;
		}
	}
	
	/**
	 * Used for creating tables in the database
	 *
	 * @param table The table to create (also look at {@link Table})
	 * @return {@link boolean}
	 */
	@Override
	public boolean createTable(Table table, DatabaseType dbtype) {
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + table.getName() + "` (");
		// primary key columns
		query.append("`").append(table.getPrimaryKey().getName()).append("` ");
		query.append(this.parseInputDataType(table.getPrimaryKey().getDataType()));
		query.append(table.getPrimaryKey().getLimit() > 0 ? " (" + table.getPrimaryKey().getLimit() + ")" : "");
		query.append(" PRIMARY KEY");
		if (dbtype == DatabaseType.SQLite && table.getPrimaryKey().isAutoIncrement()) {
			query.append(" AUTOINCREMENT");
		}
		else if (dbtype == DatabaseType.MySQL && table.getPrimaryKey().isAutoIncrement()) {
			query.append(" AUTO_INCREMENT");
		}
		query.append(", ");
		// remaining columns
		for (Column column : table.getColumns()) {
			query.append("`").append(column.getName()).append("` ");
			query.append(this.parseInputDataType(column.getDataType()));
			query.append(column.getLimit() > 0 ? " (" + column.getLimit() + ")" : "");
			query.append(column.getDefaultValue() != null ? " DEFAULT '" + column.getDefaultValue() + "'" : "");
			query.append(!column.isNullable() ? " NOT NULL, " : ", ");
		}
		// end closing
		query.replace(query.length() - 2, query.length(), ");");

		Logger.log("[QUERY] " + query);

		try {
			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(query.toString());
			ps.executeUpdate();
			ps.close();
			DatabaseManager dbm = DatabaseManager.getInstance();
			DatabaseType dbType = dbm.getDbInfo().getDbType();
			switch (dbType) {
				case SQLite:
					dbm.getSQLite().addTable(table);
					return true;
				case MySQL:
					dbm.getMySQL().addTable(table);
					return true;
				default:
					return false;

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * Used for deleting the table from database
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link boolean}
	 */
	@Override
	public boolean dropTable(String tablename) {
		StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS `" + tablename + "`;");

		Logger.log("[QUERY] " + query);

		try {
			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(query.toString());
			ps.executeUpdate();
			ps.close();
			DatabaseManager dbm = DatabaseManager.getInstance();
			DatabaseType dbType = dbm.getDbInfo().getDbType();
			switch (dbType) {
				case SQLite:
					dbm.getSQLite().removeTable(tablename);
					return true;
				case MySQL:
					dbm.getMySQL().removeTable(tablename);
					return true;
				default:
					return false;

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * For inserting the data in the table
	 *
	 * @param tablename The name of the table in the database
	 * @param columns   Usually known as the row of data ({@link List}&lt;{@link Column}&gt; = Row)
	 * @return {@link boolean}
	 */
	@Override
	public boolean insertData(String tablename, List<Column> columns) {
		if (columns != null) {
			StringBuilder query = new StringBuilder("INSERT INTO `" + tablename + "` (");
			for (Column column : columns) {
				if (columns.indexOf(column) < columns.size() - 1) {
					query.append("`").append(column.getName()).append("`, ");
				} else {
					query.append("`").append(column.getName()).append("`) ");
				}
			}
			query.append("VALUES (");
			for (int i = 0; i < columns.size(); i++) {
				if (i < columns.size() - 1) {
					query.append("?, ");
				} else {
					query.append("?)");
				}
			}
			query.append(";");

			Logger.log("[QUERY] " + query);

			try {
				Connection con = (Connection) DatabaseManager.getInstance().getConnection();
				PreparedStatement s = con.prepareStatement(query.toString());
				for (int i = 0; i < columns.size(); i++) {
					switch (columns.get(i).getDataType()) {
						case VARCHAR:
						case TEXT:
							s.setString(i + 1, columns.get(i).getValue().toString());
							break;
						case INTEGER:
						case BOOLEAN:
							s.setInt(i + 1, Integer.parseInt(columns.get(i).getValue().toString()));
							break;
						case FLOAT:
						case DOUBLE:
							s.setFloat(i + 1, Float.parseFloat(columns.get(i).getValue().toString()));
							break;
						case DATE:
							Date date;
							try {
								date = new Date(
										new SimpleDateFormat("yyyy-MM-dd")
											.parse(columns.get(i).getValue().toString())
											.getTime()
								);
								s.setDate(i + 1, date);
							}
							catch (ParseException ex) {
								ex.printStackTrace();
							}
							break;
						case TIME:
							Time time;
							try {
								time = new Time(
										new SimpleDateFormat("HH:mm:ss")
												.parse(columns.get(i).getValue().toString())
												.getTime()
								);
								s.setTime(i + 1, time);
							}
							catch (ParseException ex) {
								ex.printStackTrace();
							}
							break;
						case DATETIME:
							Timestamp timestamp;
							try {
								timestamp = new Timestamp(
										new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
												.parse(columns.get(i).getValue().toString())
												.getTime()
								);
								s.setTimestamp(i + 1, timestamp);
							}
							catch (ParseException ex) {
								ex.printStackTrace();
							}
							break;
						default:
							// Handle unknown data types or provide a default behavior
							break;
					}
				}

				s.executeUpdate();
				s.close();
				return true;
			} catch (SQLException e) {
				Logger.log("[ERROR] " + e.getMessage());
				return false;
			}
		}
		else {
			return false;
		}
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
		StringBuilder query = new StringBuilder("UPDATE `" + tablename + "` SET ");

		for (Column column : columns) {
			if (column.getDataType() == DataType.VARCHAR || column.getDataType() == DataType.TEXT) {
				query.append("`").append(column.getName()).append("`='").append(column.getValue().toString()).append("'");
			} else {
				query.append("`").append(column.getName()).append("`=").append(column.getValue().toString());
			}
			if (columns.indexOf(column) == columns.size() - 1) {
				query.append(" ");
			} else {
				query.append(", ");
			}
		}
		query.append("WHERE `").append(primaryKey.getName()).append("` = ");
		if (primaryKey.getDataType() == DataType.VARCHAR || primaryKey.getDataType() == DataType.TEXT) {
			query.append("'").append(primaryKey.getValue().toString()).append("'");
		} else {
			query.append(primaryKey.getValue().toString());
		}
		query.append(";");

		Logger.log("[QUERY] " + query);

		try {
			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement s = con.prepareStatement(query.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			Logger.log("[ERROR] " + e.getMessage());
			return false;
		}

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
		String query = "DELETE FROM `" + tablename + "` WHERE `" + primaryKey.getName() + "` = ?";

		Logger.log("[QUERY] " + query);

		try {
			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement s = con.prepareStatement(query);
			switch (primaryKey.getDataType()) {
				case VARCHAR:
				case TEXT:
					s.setString(1, primaryKey.getValue().toString());
					break;
				case INTEGER:
				case BOOLEAN:
					s.setInt( 1, Integer.parseInt(primaryKey.getValue().toString()));
					break;
				case FLOAT:
				case DOUBLE:
					s.setFloat(1, Float.parseFloat(primaryKey.getValue().toString()));
					break;
				case DATE:
					Date date;
					try {
						date = new Date(
								new SimpleDateFormat("yyyy-MM-dd")
										.parse(primaryKey.getValue().toString())
										.getTime()
						);
						s.setDate(1, date);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				case TIME:
					Time time;
					try {
						time = new Time(
								new SimpleDateFormat("HH:mm:ss")
										.parse(primaryKey.getValue().toString())
										.getTime()
						);
						s.setTime(1, time);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				case DATETIME:
					Timestamp timestamp;
					try {
						timestamp = new Timestamp(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.parse(primaryKey.getValue().toString())
										.getTime()
						);
						s.setTimestamp(1, timestamp);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				default:
					// Handle unknown data types or provide a default behavior
					break;
			}
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			Logger.log("[ERROR] " + e.getMessage());
			return false;
		}
	}

	/**
	 * @param tablename  The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link List}&lt;{@link Column}&gt;
	 */
	@Override
	public List<Column> getExactData(String tablename, Column primaryKey) {
		List<Column> result = new ArrayList<>();
		Table table = DatabaseManager.getInstance().getTables().get(tablename);
		String query = "SELECT * FROM `" + tablename + "` WHERE `" + primaryKey.getName() + "` = ?;";

		// float being weird
		if (primaryKey.getDataType() == DataType.FLOAT) {
			query = query.replace("= ?", "LIKE ?");
		}

		Logger.log("[QUERY] " + query);

		try {
			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement s = con.prepareStatement(query);

			switch (primaryKey.getDataType()) {
				case VARCHAR:
				case TEXT:
					s.setString(1, primaryKey.getValue().toString());
					break;
				case INTEGER:
				case BOOLEAN:
					s.setInt( 1, Integer.parseInt(primaryKey.getValue().toString()));
					break;
				case FLOAT:
				case DOUBLE:
					s.setFloat(1, Float.parseFloat(primaryKey.getValue().toString()));
					break;
				case DATE:
					Date date;
					try {
						date = new Date(
								new SimpleDateFormat("yyyy-MM-dd")
										.parse(primaryKey.getValue().toString())
										.getTime()
						);
						s.setDate(1, date);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				case TIME:
					Time time;
					try {
						time = new Time(
								new SimpleDateFormat("HH:mm:ss")
										.parse(primaryKey.getValue().toString())
										.getTime()
						);
						s.setTime(1, time);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				case DATETIME:
					Timestamp timestamp;
					try {
						timestamp = new Timestamp(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.parse(primaryKey.getValue().toString())
										.getTime()
						);
						s.setTimestamp(1, timestamp);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				default:
					// Handle unknown data types or provide a default behavior
					break;
			}

			ResultSet rs = s.executeQuery();

			// primary key and so on
			List<Column> allTableCols = new ArrayList<>(table.getColumns());
			allTableCols.add(0, primaryKey);

			// if there is result found then proceed
			if (rs.next()) {
				try {
					for (int i = 0; i < allTableCols.size(); i++) {
						// for each column in the row
						Column rCol = new Column(allTableCols.get(i).getName(),
								allTableCols.get(i).getDataType(),
								allTableCols.get(i).getLimit()
						);

						switch (rCol.getDataType()) {
							case VARCHAR:
							case TEXT:
								rCol.setValue(rs.getString(i + 1));
								break;
							case INTEGER:
							case BOOLEAN:
								rCol.setValue(rs.getInt(i + 1));
								break;
							case FLOAT:
							case DOUBLE:
								rCol.setValue(rs.getFloat(i + 1));
								break;
							case DATE:
								rCol.setValue(rs.getDate(i + 1));
								break;
							case TIME:
								rCol.setValue(rs.getTime(i + 1));
								break;
							case DATETIME:
								rCol.setValue(rs.getTimestamp(i + 1));
								break;
							default:
								// Handle unknown data types or provide a default behavior
								break;
						}

						result.add(rCol);
					}

					// close the PS and RS
					DatabaseManager.getInstance().closeConnection(s, rs);
				} catch (SQLException e) {
					s.close();
					Logger.log("[ERROR] " + e.getMessage());
				}
			}
		} catch (SQLException e) {
			Logger.log("[ERROR] " + e.getMessage());
		}

		return result;
	}

	/**
	 * @param tablename The name of the table in the database
	 * @param column    The column data to search for
	 * @return {@link List}&lt;{@link List}&lt;{@link Column}&gt;&gt;
	 */
	@Override
	public List<List<Column>> findData(String tablename, Column column) {
		List<List<Column>> results = new ArrayList<>();
		Table table = DatabaseManager.getInstance().getTables().get(tablename);
		String query = "SELECT * FROM `" + tablename + "` WHERE `" + column.getName() + "` = ?;";

		// float being weird
		if (column.getDataType() == DataType.FLOAT) {
			query = query.replace("= ?", "LIKE ?");
		}

		Logger.log("[QUERY] " + query);

		try {
			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement s = con.prepareStatement(query);

			switch (column.getDataType()) {
				case VARCHAR:
				case TEXT:
					s.setString(1, column.getValue().toString());
					break;
				case INTEGER:
				case BOOLEAN:
					s.setInt( 1, Integer.parseInt(column.getValue().toString()));
					break;
				case FLOAT:
				case DOUBLE:
					s.setFloat(1, Float.parseFloat(column.getValue().toString()));
					break;
				case DATE:
					Date date;
					try {
						date = new Date(
								new SimpleDateFormat("yyyy-MM-dd")
										.parse(column.getValue().toString())
										.getTime()
						);
						s.setDate(1, date);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				case TIME:
					Time time;
					try {
						time = new Time(
								new SimpleDateFormat("HH:mm:ss")
										.parse(column.getValue().toString())
										.getTime()
						);
						s.setTime(1, time);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				case DATETIME:
					Timestamp timestamp;
					try {
						timestamp = new Timestamp(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.parse(column.getValue().toString())
										.getTime()
						);
						s.setTimestamp(1, timestamp);
					}
					catch (ParseException ex) {
						ex.printStackTrace();
					}
					break;
				default:
					// Handle unknown data types or provide a default behavior
					break;
			}

			ResultSet rs = s.executeQuery();

			// primary key and so on
			List<Column> allTableCols = table.getColumns();
			allTableCols.add(0, table.getPrimaryKey());

			while (rs.next()) {
				List<Column> temp = new ArrayList<>();
				for (int i = 0; i < allTableCols.size(); i++) {
					// for each column in the row
					Column rCol = new Column(allTableCols.get(i).getName(),
							allTableCols.get(i).getDataType(),
							allTableCols.get(i).getLimit()
					);

					// set value
					switch (rCol.getDataType()) {
						case VARCHAR:
						case TEXT:
							rCol.setValue(rs.getString(i + 1));
							break;
						case INTEGER:
						case BOOLEAN:
							rCol.setValue(rs.getInt(i + 1));
							break;
						case FLOAT:
						case DOUBLE:
							rCol.setValue(rs.getFloat(i + 1));
							break;
						case DATE:
							rCol.setValue(rs.getDate(i + 1));
							break;
						case TIME:
							rCol.setValue(rs.getTime(i + 1));
							break;
						case DATETIME:
							rCol.setValue(rs.getTimestamp(i + 1));
							break;
						default:
							// Handle unknown data types or provide a default behavior
							break;
					}

					temp.add(rCol);
				}
				results.add(temp);
			}
			DatabaseManager.getInstance().closeConnection(s, rs);
		} catch (SQLException e) {
			Logger.log("[ERROR] " + e.getMessage());
		}

		return results;
	}

	/**
	 * Get all the data in given table
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link List}&lt;{@link List}&lt;{@link Column}&gt;&gt;
	 */
	@Override
	public List<List<Column>> getAllDatas(String tablename) {
		List<List<Column>> allDatas = new ArrayList<>();
		Table table = DatabaseManager.getInstance().getTables().get(tablename);
		String query = "SELECT * FROM `" + tablename + "`;";
		try {
			Logger.log("[QUERY] " + query);

			Connection con = (Connection) DatabaseManager.getInstance().getConnection();
			PreparedStatement s = con.prepareStatement(query);
			ResultSet rs = s.executeQuery();

			// primary key and so on
			List<Column> allTableCols = table.getColumns();
			allTableCols.add(0, table.getPrimaryKey());

			while (rs.next()) {
				// for each row
				List<Column> temp = new ArrayList<>();
				for (int i = 0; i < allTableCols.size(); i++) {
					// for each column in the row
					Column rCol = new Column(allTableCols.get(i).getName(),
											 allTableCols.get(i).getDataType(),
											 allTableCols.get(i).getLimit()
					);

					// set value
					switch (rCol.getDataType()) {
						case VARCHAR:
						case TEXT:
							rCol.setValue(rs.getString(i + 1));
							break;
						case INTEGER:
						case BOOLEAN:
							rCol.setValue(rs.getInt(i + 1));
							break;
						case FLOAT:
						case DOUBLE:
							rCol.setValue(rs.getFloat(i + 1));
							break;
						case DATE:
							rCol.setValue(rs.getDate(i + 1));
							break;
						case TIME:
							rCol.setValue(rs.getTime(i + 1));
							break;
						case DATETIME:
							rCol.setValue(rs.getTimestamp(i + 1));
							break;
						default:
							// Handle unknown data types or provide a default behavior
							break;
					}

					temp.add(rCol);
				}
				allDatas.add(temp);
			}
			// close both PS and RS
			DatabaseManager.getInstance().closeConnection(s, rs);
		} catch (SQLException e) {
			Logger.log("[ERROR] " + e.getMessage());
		}
		return allDatas;
	}
}
