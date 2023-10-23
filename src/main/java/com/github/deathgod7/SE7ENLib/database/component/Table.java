package com.github.deathgod7.SE7ENLib.database.component;

import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Table {

	private String name;
	private List<Column> columns = new ArrayList<>();
	private Column primaryKey;

	public Table(String name, Column primaryKey, Column... columns) {
		this.name = name;
		this.primaryKey = primaryKey;
		this.columns.addAll(Arrays.asList(columns));
	}

	public Table(String name,  Column primaryKey, Collection<Column> columns) {
		this.name = name;
		this.primaryKey = primaryKey;
		this.columns.addAll(columns);
	}

	public Table(String name, Collection<Column> columns, Column primaryKey) {
		this.name = name;
		this.primaryKey = primaryKey;
		this.columns.addAll(columns);
	}

	public Table(String name, Column... columns) {
		this.name = name;
		this.columns.addAll(Arrays.asList(columns));
		this.primaryKey = this.columns.get(0);
	}

	public Table(String name, Collection<Column> columns) {
		this.name = name;
		this.columns.addAll(columns);
		this.primaryKey = this.columns.get(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public Column getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Column primaryKey) {
		this.primaryKey = primaryKey;
	}

	public static String getInputDataType(DataType dataType) {
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

	// default query of the table
	public String getDefaultQuery() {
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + getName() + " (");
		for (Column column : getColumns()) {
			query.append("`").append(column.getName()).append("` ");
			query.append(Table.getInputDataType(column.getDataType()));
			query.append(column.getLimit() > 0 ? " (" + column.getLimit() + "), " : ", ");
		}
		query.append("PRIMARY KEY (`").append(primaryKey.getName()).append("`)");
		query.append(");");
		return query.toString();
	}


}
