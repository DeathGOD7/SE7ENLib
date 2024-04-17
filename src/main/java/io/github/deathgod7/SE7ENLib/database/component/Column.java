package io.github.deathgod7.SE7ENLib.database.component;

import io.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a Column as well as Row in the database
 * @version 1.0
 * @since 1.0
 */
public class Column {
	private String name;
	private DataType dataType;
	private int limit;
	private Object value;
	private Object defaultValue;
	private boolean autoIncrement;
	private boolean isNullable;


	/**
	 * Creates a new Column
	 * @param name The name of the column
	 * @param dataType The data type of the column
	 */
	public Column(String name, DataType dataType) {
		this.name = name;
		this.dataType = dataType;
		this.limit = 0;
		this.value = this.getDefaultDataValue(dataType);
		this.isNullable = true;
	}

	/**
	 * Creates a new Column
	 * @param name The name of the column
	 * @param dataType The data type of the column
	 * @param limit The limit of the column
	 */
	public Column(String name, DataType dataType, int limit) {
		this.name = name;
		this.dataType = dataType;
		this.limit = limit;
		this.value = this.getDefaultDataValue(dataType);
		this.isNullable = true;
	}

	/**
	 * Creates a new Column
	 * @param name The name of the column
	 * @param value The value of the column
	 * @param dataType The data type of the column
	 */
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

	/**
	 * Creates a new Column
	 * @param name The name of the column
	 * @param value The value of the column
	 * @param dataType The data type of the column
	 * @param limit The limit of the column
	 */
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

	/**
	 * Get default value for the data type
	 * @param type
	 * @return {@link Object}
	 */
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

	/**
	 * Get the name of the column
	 * @return {@link String}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the column
	 * @param name The name of the column
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the data type of the column
	 * @return {@linkplain DataType}
	 */
	public DataType getDataType() {
		return this.dataType;
	}

	/**
	 * Set the data type of the column
	 * @param dataType The data type of the column
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * Get the limit of the column
	 * @return {@link int}
	 */
	public int getLimit() {
		return this.limit;
	}

	/**
	 * Set the limit of the column
	 * @param limit The limit of the column
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Get the value of the column
	 * @return {@link Object}
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Set the value of the column
	 * @param value The value of the column
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Get if the column is nullable
	 * @return {@link boolean}
	 */
	public boolean isNullable() {
		return this.isNullable;
	}

	/**
	 * Set if the column is nullable
	 * @param value The boolean to enable or disable nullable
	 */
	public void setNullable(boolean value) {
		this.isNullable = value;
	}

	/**
	 * Get if the column is auto increment
	 * @return {@link boolean}
	 */
	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	/**
	 * Set if the column is auto increment
	 * @param value The boolean value to enable or disable auto increment
	 */
	public void setAutoIncrement(boolean value) {
		this.autoIncrement = value;
	}

	/**
	 * Get the default value of the column
	 * @return {@link Object}
	 */
	public Object getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Set the default value of the column
	 * @param value The default value of the column
	 */
	public void setDefaultValue(Object value) {
		this.defaultValue = value;
	}

}
