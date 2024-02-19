package com.github.deathgod7.SE7ENLib.database.component;

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
		this.primaryKey.setNullable(false);
	}

	public Table(String name,  Column primaryKey, Collection<Column> columns) {
		this.name = name;
		this.primaryKey = primaryKey;
		this.columns.addAll(columns);
		this.primaryKey.setNullable(false);
	}

	public Table(String name, Collection<Column> columns, Column primaryKey) {
		this.name = name;
		this.primaryKey = primaryKey;
		this.columns.addAll(columns);
		this.primaryKey.setNullable(false);
	}

	public Table(String name, Column... columns) {
		this.name = name;
		this.columns.addAll(Arrays.asList(columns));
		this.primaryKey = this.columns.get(0);
		this.primaryKey.setNullable(false);
	}

	public Table(String name, Collection<Column> columns) {
		this.name = name;
		this.columns.addAll(columns);
		this.primaryKey = this.columns.get(0);
		this.primaryKey.setNullable(false);
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


}
