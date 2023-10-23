package com.github.deathgod7.SE7ENLib.database.component;

import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Column {
	private String name;
	private DataType dataType;
	private int limit;
	private Object value;
	private Object defaultValue;
	private boolean autoIncrement;
	private boolean isNullable;


	public Column(String name, DataType dataType) {
		this.name = name;
		this.dataType = dataType;
		this.limit = 0;
		this.value = this.getDefaultDataValue(dataType);
		this.isNullable = true;
	}

	public Column(String name, DataType dataType, int limit) {
		this.name = name;
		this.dataType = dataType;
		this.limit = limit;
		this.value = this.getDefaultDataValue(dataType);
		this.isNullable = true;
	}

	public Column(String name, Object value, DataType dataType) {
		this.name = name;
		this.dataType = dataType;
		this.limit = 0;
		if (dataType == DataType.BOOLEAN && value instanceof Boolean) {
			this.value = getBoolInt((boolean) value);
		}
		else {
			this.value = value;
		}
		this.isNullable = true;
	}

	public Column(String name, Object value, DataType dataType, int limit) {
		this.name = name;
		this.dataType = dataType;
		this.limit = limit;
		if (dataType == DataType.BOOLEAN && value instanceof Boolean) {
			this.value = getBoolInt((boolean) value);
		}
		else {
			this.value = value;
		}
		this.isNullable = true;
	}

	private Object getDefaultDataValue(DataType type) {
		switch (type) {
			case INTEGER:
				return 0;
			case FLOAT:
			case DOUBLE:
				return 0.0;
			case VARCHAR:
			case TEXT:
				return "";
			case BOOLEAN:
				return getBoolInt(false);
			case DATE:
				LocalDateTime currentDate = LocalDateTime.now();
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				return currentDate.format(dateFormatter);
			case TIME:
				LocalDateTime currentTime = LocalDateTime.now();
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				return currentTime.format(timeFormatter);
			case DATETIME:
				LocalDateTime currentDateTime = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				return currentDateTime.format(formatter);
			default:
				return null;
		}
	}

	private int getBoolInt(boolean bool) {
		return bool ? 1 : 0;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return this.dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}


	public boolean isNullable() {
		return this.isNullable;
	}

	public void setNullable(boolean value) {
		this.isNullable = value;
	}

	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	public void setAutoIncrement(boolean value) {
		this.autoIncrement = value;
	}

	public Object getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(Object value) {
		this.defaultValue = value;
	}

}
