// This file is part of SE7ENLib, created on 17/02/2024 (14:07 PM)
// Name : MongoOperations
// Author : Death GOD 7

package com.github.deathgod7.SE7ENLib.database.handler;

import com.github.deathgod7.SE7ENLib.database.DatabaseManager;
import com.github.deathgod7.SE7ENLib.database.component.Column;
import com.github.deathgod7.SE7ENLib.database.component.Table;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DataType;
import com.github.deathgod7.SE7ENLib.database.DatabaseManager.DatabaseType;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
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
			Column primaryKey = null;

			// Convert the validator to a Document
			Document commandResult = db.runCommand(new Document("listCollections", 1.0)
					.append("filter", new Document("name", tablename)));
			Document firstBatch = (Document) commandResult.get("cursor", Document.class).get("firstBatch", List.class).get(0);
			Document validatorDocument = (Document) firstBatch.get("options", Document.class).get("validator");


			// Get the properties of the validator
			Document dc = null;
			if (validatorDocument != null) {
				dc = validatorDocument.get("$jsonSchema", Document.class).get("properties", Document.class);
				for (String key : dc.keySet()) {
					String dataType = dc.get(key, Document.class).get("bsonType").toString();
					DataType type = getDataTypeFromString(dataType);

					if (key.equals(_pKey)) {
						primaryKey = new Column(key, type);
						continue;
					}

					Column column = new Column(key, type);

					columns.put(key, column);
				}
			} else {
				dc = collection.find().first();
				assert dc != null;
				for (String key : dc.keySet()) {
					DataType type = parseDataTypeClass(dc.get(key).getClass());

					if (key.equals(_pKey)) {
						primaryKey = new Column(key, type);
						continue;
					}

					Column column = new Column(key, type);

					columns.put(key, column);
				}
			}

			if (primaryKey == null) {
				return null;
			}

			return new Table(tablename, primaryKey, columns.values());

		}
		else {
			return null;
		}
	}

	private <T> boolean checkListElementType(List<?> list, Class<T> elementType) {
		for (Object element : list) {
			if (!elementType.isInstance(element)) {
				return false;
			}
		}
		return true;
	}

	private List<String> extractColumnNames(List<Column> columns) {
		List<String> columnNames = new ArrayList<>();
		for (Column column : columns) {
			columnNames.add(column.getName());
		}
		return columnNames;
	}

	public Document getObjAsDocument(Object value, DataType type) {
		if (type != DataType.DOCUMENT) {
			return null;
		}
		List<Column> alltempcols;
		List<Column> requiredtempCols = new ArrayList<>();

		Document docProperties = new Document();
		Document doc = new Document();

		if (value instanceof List<?> && this.checkListElementType((List<?>) value, Column.class)) {
			alltempcols = (List<Column>) value;
			for (Column c : alltempcols) {
				if (!c.isNullable()) { requiredtempCols.add(c); }

				if (c.getDataType() == DataType.DOCUMENT) {
					Document tempdoc = getObjAsDocument(c.getValue(), c.getDataType());
					docProperties.append(c.getName(), tempdoc);
				} else {
					docProperties.append(c.getName(), new Document("bsonType", this.getDataTypeForMongo(c.getDataType())));
				}
			}

			doc.append("bsonType", "object")
				.append("required", extractColumnNames(requiredtempCols))
				.append("properties", docProperties);

//			System.out.println(doc.toJson() + "\n");
			return doc;
		}
		else {
			doc.append("bsonType", "object");
//			System.out.println(doc.toJson() + "\n");
			return doc; }
	}

	public String getDataTypeForMongo(DataType type) {
		switch (type) {
			case DATE:
				return "date";
			case INTEGER:
				return "int";
			case FLOAT:
			case DOUBLE:
				return "double";
			case BOOLEAN:
				return "bool";
			case ARRAY:
				return "array";
			case DOCUMENT:
				return "object";
			case TEXT:
			case VARCHAR:
			default:
				return "string";
		}
	}

	public DataType parseDataTypeClass(Class<?> clazz) {
		if (clazz == String.class) {
			return DataType.TEXT;
		}
		else if (clazz == Integer.class) {
			return DataType.INTEGER;
		}
		else if (clazz == Boolean.class) {
			return DataType.BOOLEAN;
		}
		else if (clazz == Float.class) {
			return DataType.FLOAT;
		}
		else if (clazz == Double.class) {
			return DataType.DOUBLE;
		}
		else if (clazz == Date.class) {
			return DataType.DATETIME;
		}
		else if (clazz == ArrayList.class) {
			return DataType.ARRAY;
		}
		else if (clazz == Document.class) {
			return DataType.DOCUMENT;
		}
		else if (clazz == ObjectId.class) {
			return DataType.OBJECTID;
		}
		else {
			return null;
		}
	}

	public DataType getDataTypeFromString(String type) {
		switch (type) {
			case "date":
				return DataType.DATE;
			case "int":
				return DataType.INTEGER;
			case "double":
				return DataType.DOUBLE;
			case "bool":
				return DataType.BOOLEAN;
			case "array":
				return DataType.ARRAY;
			case "object":
				return DataType.DOCUMENT;
			case "string":
			default:
				return DataType.VARCHAR;
		}
	}

	/**
	 * Used for creating tables in the database
	 *
	 * @param table  The table to create (also look at {@link Table})
	 * @param dbtype The type of database to work with
	 * @return {@link boolean}
	 */
	@Override
	public boolean createTable(Table table, DatabaseType dbtype) {
		if (dbtype != DatabaseType.MongoDB) {
			return false;
		}

		MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

		// Define schema rules for the collection
		List<Column> allCols = table.getColumns();

		if (!table.getPrimaryKey().getName().equals("_id")) {
			return false;
		}
		else {
			allCols.add(0, table.getPrimaryKey());
		}

		Document mainProperties = getObjAsDocument(allCols, DataType.DOCUMENT);

		Document schemaDocument = new Document()
				.append("$jsonSchema", mainProperties);

		ValidationOptions schema = new ValidationOptions().validator(schemaDocument);

		// Create the collection with schema validation
		CreateCollectionOptions validationOptions = new CreateCollectionOptions().validationOptions(schema);
		db.createCollection(table.getName(), validationOptions);

		return true;
	}

	/**
	 * Used for deleting the table from database
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link boolean}
	 */
	@Override
	public boolean dropTable(String tablename) {
		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();
			db.getCollection(tablename).drop();
			return true;
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
			return false;
		}
	}

	public Document getColsAsDocument(List<Column> value) {
		Document doc = new Document();

		for (Column c : value) {
			if (c.getDataType() == DataType.DOCUMENT) {
				Document tempdoc = getColsAsDocument((List<Column>) c.getValue());
				doc.append(c.getName(), tempdoc);
			} else {
				Object val = c.getValue();
				if (val == null) {
					val = "{}";
				}
				doc.append(c.getName(), val);
			}
		}

		return doc;

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
		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

			Table table = DatabaseManager.getInstance().getTables().get(tablename);

			if (table == null) {
				System.out.println("Table not found");
				return false;
			}



			MongoCollection<Document> collection = db.getCollection(tablename);

			Document doc = getColsAsDocument(columns);

			collection.insertOne(doc);

			return true;
		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
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
		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

			Table table = DatabaseManager.getInstance().getTables().get(tablename);

			if (table == null) {
				throw new Exception("Table " + tablename + " not found.");
			}

			if (!table.getPrimaryKey().getName().equals(primaryKey.getName())) {
				throw new Exception("Primary Key is not correct for table " + tablename + " | Expected : " + table.getPrimaryKey().getName() + " | Given : " + primaryKey.getName());
			}

			MongoCollection<Document> collection = db.getCollection(tablename);

			Document docToFind = new Document(primaryKey.getName(), primaryKey.getValue());

			Document doc = getColsAsDocument(columns);

			collection.updateOne(docToFind, new Document("$set", doc));

			return true;
		} catch (Exception ex) {
			System.out.println("Error : " + ex.getMessage());
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
		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

			Table table = DatabaseManager.getInstance().getTables().get(tablename);

			if (table == null) {
				throw new Exception("Table " + tablename + " not found.");
			}

			if (!table.getPrimaryKey().getName().equals(primaryKey.getName())) {
				throw new Exception("Primary Key is not correct for table " + tablename + " | Expected : " + table.getPrimaryKey().getName() + " | Given : " + primaryKey.getName());
			}

			MongoCollection<Document> collection = db.getCollection(tablename);

			Document docToFind = new Document(primaryKey.getName(), primaryKey.getValue());

			collection.deleteOne(docToFind);

			return true;

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
			return false;
		}
	}

	// parse the document to list of columns
	public List<Column> parseDocToColumns(Document doc) {
		List<Column> columns = new ArrayList<>();

		doc.forEach((key, value) -> {
			DataType dataType = this.parseDataTypeClass(value.getClass());
			Column temp = new Column(key, dataType);
			if (dataType == DataType.DOCUMENT) {
				List<Column> tempcol = this.parseDocToColumns(doc.get(key, Document.class));
				temp.setValue(tempcol);
			} else if (dataType == DataType.ARRAY) {
				List <?> tempvalue = (List<?>) value;
				if (tempvalue.get(0).getClass() == Document.class) {
					List<List<Column>> tempcol = new ArrayList<>();
					for (Document d : (List<Document>) value) {
						tempcol.add(this.parseDocToColumns(d));
					}
					temp.setValue(tempcol);
				} else {
					temp.setValue(value);
				}
			} else {
				temp.setValue(doc.get(key));
			}
			columns.add(temp);
		});

		return columns;
	}

	/**
	 * @param tablename  The name of the table in the database
	 * @param primaryKey Unique Identifier of the Row
	 * @return {@link List<>}<{@link Column}>
	 */
	@Override
	public List<Column> getExactData(String tablename, Column primaryKey) {
		List<Column> columns = null;

		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

			Table table = DatabaseManager.getInstance().getTables().get(tablename);

			if (table == null) {
				throw new Exception("Table " + tablename + " not found.");
			}

			if (!table.getPrimaryKey().getName().equals(primaryKey.getName())) {
				throw new Exception("Primary Key is not correct for table " + tablename + " | Expected : " + table.getPrimaryKey().getName() + " | Given : " + primaryKey.getName());
			}

			columns = new ArrayList<>();

			MongoCollection<Document> collection = db.getCollection(tablename);

			Document docToFind = new Document(primaryKey.getName(), primaryKey.getValue());

			Document res = collection.find(docToFind).first();

			if (res != null) {
				columns = parseDocToColumns(res);
			}

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		return columns;
	}

	/**
	 * @param tablename The name of the table in the database
	 * @param column    The column data to search for
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	@Override
	public List<List<Column>> findData(String tablename, Column column) {
		List<List<Column>> allData = new ArrayList<>();

		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

			Table table = DatabaseManager.getInstance().getTables().get(tablename);

			if (table == null) {
				throw new Exception("Table " + tablename + " not found.");
			}

			List<Column> columns;

			MongoCollection<Document> collection = db.getCollection(tablename);

			Document docToFind = new Document(column.getName(), column.getValue());

			for (Document doc : collection.find(docToFind)) {
				columns = parseDocToColumns(doc);
				allData.add(columns);
			}

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		return allData;
	}

	/**
	 * Get all the data in given table
	 *
	 * @param tablename The name of the table in the database
	 * @return {@link List<>}<{@link List<>}<{@link Column}>>
	 */
	@Override
	public List<List<Column>> getAllDatas(String tablename) {
		List<List<Column>> allData = new ArrayList<>();

		try {
			MongoDatabase db = (MongoDatabase) DatabaseManager.getInstance().getConnection();

			Table table = DatabaseManager.getInstance().getTables().get(tablename);

			if (table == null) {
				throw new Exception("Table " + tablename + " not found.");
			}

			List<Column> columns;

			MongoCollection<Document> collection = db.getCollection(tablename);

			for (Document doc : collection.find()) {
				columns = parseDocToColumns(doc);
				allData.add(columns);
			}

		} catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		return allData;
	}

	public int getDocumentCount(String tablename) {
		return this.getAllDatas(tablename).size();
	}
}
