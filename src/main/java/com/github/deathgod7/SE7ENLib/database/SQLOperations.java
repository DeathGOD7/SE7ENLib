// This file is part of SE7ENLib, created on 23/10/2023 (01:09 AM)
// Name : SQLOperations
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.database;

import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
		query.append(Table.getInputDataType(table.getPrimaryKey().getDataType()));
		query.append(" PRIMARY KEY ");
		if (dbtype == DatabaseType.SQLite) {
			query.append("AUTOINCREMENT");
		}
		else if (dbtype == DatabaseType.MySQL) {
			query.append("AUTO_INCREMENT");
		}
		query.append(", ");
		// remaining columns
		for (Column column : table.getColumns()) {
			query.append("`").append(column.getName()).append("` ");
			query.append(Table.getInputDataType(column.getDataType()));
			query.append(column.getLimit() > 0 ? " (" + column.getLimit() + ") " : "");
			query.append(column.getDefaultValue() != null ? " DEFAULT '" + column.getDefaultValue() + "' " : "");
			query.append(!column.isNullable() ? " NOT NULL, " : ", ");
		}
		// end closing
		query.replace(query.length() - 3, query.length(), ");");

		System.out.println(query);

		try {
			PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(query.toString());
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
		StringBuilder query = new StringBuilder("DROP TABLE IF EXISTS `" + tablename + ";");

		try {
			PreparedStatement ps = DatabaseManager.getInstance().getConnection().prepareStatement(query.toString());
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
	 * @param columns   Usually known as the row of data ({@link List<>}<{@link Column}> = Row)
	 * @return {@link boolean}
	 */
	@Override
	public boolean insertData(String tablename, List<Column> columns) {
		if (columns != null) {
			StringBuilder query = new StringBuilder("INSERT INTO " + tablename + " (");
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


			try {
				PreparedStatement s = DatabaseManager.getInstance().getConnection().prepareStatement(query.toString());
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
				System.out.println(s);

				s.executeUpdate();
				s.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
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
		StringBuilder query = new StringBuilder("UPDATE " + tablename + " SET ");

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

		try {
			PreparedStatement s = DatabaseManager.getInstance().getConnection().prepareStatement(query.toString());
			s.executeUpdate();
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
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
		String query = "DELETE FROM " + tablename + " WHERE `" + primaryKey.getName() + "` = ?";
		try {
			PreparedStatement s = DatabaseManager.getInstance().getConnection().prepareStatement(query);
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
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param tablename  The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link List<>}<{@link Column}>
	 */
	@Override
	public List<Column> getExactData(String tablename, Column primaryKey) {
		List<Column> result = new ArrayList<>();
		Table table = DatabaseManager.getInstance().getTables().get(tablename);
		String query = "SELECT * FROM " + tablename + " WHERE `" + primaryKey.getName() + "` = ?";

		try {
			PreparedStatement s = DatabaseManager.getInstance().getConnection().prepareStatement(query);

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

			// if there is result found then proceed
			if (rs.next()) {
				try {
					for (int i = 0; i < table.getColumns().size(); i++) {
						// for each column in the row
						Column rCol = new Column(table.getColumns().get(i).getName(),
								table.getColumns().get(i).getDataType(),
								table.getColumns().get(i).getLimit()
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
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @param tablename The name of the table in the database
	 * @param column    The column data to search for
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	@Override
	public List<List<Column>> findData(String tablename, Column column) {
		List<List<Column>> results = new ArrayList<>();
		Table table = DatabaseManager.getInstance().getTables().get(tablename);
		String query = "SELECT * FROM " + tablename + " WHERE `" + column.getName() + "` = ?";

		try {
			PreparedStatement s = DatabaseManager.getInstance().getConnection().prepareStatement(query);

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
			while (rs.next()) {
				List<Column> temp = new ArrayList<>();
				for (int i = 0; i < table.getColumns().size(); i++) {
					// for each column in the row
					Column rCol = new Column(table.getColumns().get(i).getName(),
							table.getColumns().get(i).getDataType(),
							table.getColumns().get(i).getLimit()
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
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * Get all the data in given table
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	@Override
	public List<List<Column>> getAllDatas(String tablename) {
		List<List<Column>> allDatas = new ArrayList<>();
		Table table = DatabaseManager.getInstance().getTables().get(tablename);
		String query = "SELECT * FROM " + tablename;
		try {
			PreparedStatement s = DatabaseManager.getInstance().getConnection().prepareStatement(query);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				// for each row
				List<Column> temp = new ArrayList<>();
				for (int i = 0; i < table.getColumns().size(); i++) {
					// for each column in the row
					Column rCol = new Column(table.getColumns().get(i).getName(),
											 table.getColumns().get(i).getDataType(),
											 table.getColumns().get(i).getLimit()
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
			e.printStackTrace();
		}
		return allDatas;
	}
}
