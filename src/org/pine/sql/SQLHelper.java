package org.pine.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pine.model.Product;
import org.pine.model.files.Category;
import org.pine.model.files.DataFile;
import org.pine.model.files.DataFileKey;
import org.pine.model.files.DataFileRow;
import org.pine.model.files.DataFileValue;
import org.pine.model.files.PostconditionValue;
import org.pine.model.files.PreconditionValue;
import org.pine.model.storages.DataStorage;
import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageRow;
import org.pine.model.storages.DataStorageValue;
import org.pine.model.storages.StorageCategory;
import org.pine.model.users.User;
import org.pine.model.users.UserPermission;


public class SQLHelper {

	private static String sqlserver = "localhost";
	private static String sqlport = "5432";
	private static String sqldatabase = "DataCenter";
	private static String sqllogin = "postgres";
	private static String sqlpassword = "brady";

	public SQLHelper() {
		initializeSQLDriver();
	}

	public List<Product> getProducts() {
		List<Product> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT \"Id\", \"Name\" FROM \"Products\"");

			while (rs.next()) {
				int id = rs.getInt("Id");
				String name = rs.getString("Name");
				Product product = new Product(id);
				product.setName(name);
				result.add(product);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public Product getProduct(int id) {
		Product result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT \"Name\" FROM \"Products\" WHERE \"Id\"=" + id);

			while (rs.next()) {
				String name = rs.getString("Name");
				Product product = new Product(id);
				product.setName(name);
				result = product;
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<Category> getCategories(int productId) {
		List<Category> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Categories\" WHERE \"ProductId\"=" + productId
					+ " ORDER BY \"Name\"");

			while (rs.next()) {
				int id = rs.getInt("Id");
				String name = rs.getString("Name");
				Category category = new Category(id);
				category.setName(name);
				category.setProductId(productId);
				result.add(category);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<StorageCategory> getStorageCategories(int productId) {
		List<StorageCategory> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"StorageCategories\" WHERE \"ProductId\"=" + productId
					+ " ORDER BY \"Name\"");

			while (rs.next()) {
				int id = rs.getInt("Id");
				String name = rs.getString("Name");
				StorageCategory category = new StorageCategory(id);
				category.setName(name);
				category.setProductId(productId);
				result.add(category);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataStorage> getDataStoragesByCategoryId(int categoryId) {
		List<DataStorage> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorages\" WHERE \"CategoryId\"=" + categoryId
					+ " ORDER BY \"Name\"");

			while (rs.next()) {
				DataStorage dataStorage = new DataStorage(rs.getInt("Id"));
				dataStorage.setCategoryId(categoryId);
				dataStorage.setName(rs.getString("Name"));
				dataStorage.setClassName(rs.getString("ClassName"));
				dataStorage.setShowUsage(rs.getBoolean("ShowUsage"));
				result.add(dataStorage);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataStorage> getDataStoragesByProductId(int productId) {
		List<DataStorage> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM \"DataStorages\" WHERE \"CategoryId\" IN (SELECT \"Id\" FROM \"StorageCategories\" WHERE \"ProductId\"="
							+ productId + ") ORDER BY \"Name\"");

			while (rs.next()) {
				DataStorage dataStorage = new DataStorage(rs.getInt("Id"));
				dataStorage.setCategoryId(rs.getInt("CategoryId"));
				dataStorage.setName(rs.getString("Name"));
				dataStorage.setClassName(rs.getString("ClassName"));
				dataStorage.setShowUsage(rs.getBoolean("ShowUsage"));
				result.add(dataStorage);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataFile> getDataFiles(int categoryId) {
		List<DataFile> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFiles\" WHERE \"CategoryId\"=" + categoryId
					+ " ORDER BY \"Name\"");

			while (rs.next()) {
				DataFile dataFile = new DataFile(rs.getInt("Id"));
				dataFile.setName(rs.getString("Name"));
				dataFile.setCategoryId(categoryId);
				result.add(dataFile);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataStorage> getDataStoragesUsingStorage(int refStorageId) {
		List<DataStorage> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorages\" WHERE \"Id\" IN "
					+ "(SELECT \"DataStorageId\" FROM \"DataStorageKeys\" WHERE \"ReferenceStorageId\"=" + refStorageId
					+ ")");

			while (rs.next()) {
				DataStorage dataStorage = new DataStorage(rs.getInt("Id"));
				dataStorage.setName(rs.getString("Name"));
				dataStorage.setClassName(rs.getString("ClassName"));
				dataStorage.setShowUsage(rs.getBoolean("ShowUsage"));
				result.add(dataStorage);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataFile> getDataFilesUsingStorage(int refStorageId) {
		List<DataFile> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM \"DataFiles\" WHERE \"Id\" IN "
							+ "(SELECT \"DataFileId\" FROM \"DataFileKeys\" WHERE \"ReferenceStorageId\"="
							+ refStorageId + ")");

			while (rs.next()) {
				DataFile dataFIle = new DataFile(rs.getInt("Id"));
				dataFIle.setName(rs.getString("Name"));
				result.add(dataFIle);
			}

			rs = stmt.executeQuery("SELECT * FROM \"DataFiles\" WHERE \"Id\" IN "
					+ "(SELECT \"DataFileId\" FROM \"Preconditions\" WHERE \"ReferenceStorageId\"=" + refStorageId
					+ ")");

			while (rs.next()) {
				DataFile dataFIle = new DataFile(rs.getInt("Id"));
				dataFIle.setName(rs.getString("Name"));
				result.add(dataFIle);
			}

			rs = stmt.executeQuery("SELECT * FROM \"DataFiles\" WHERE \"Id\" IN "
					+ "(SELECT \"DataFileId\" FROM \"Postconditions\" WHERE \"ReferenceStorageId\"=" + refStorageId
					+ ")");

			while (rs.next()) {
				DataFile dataFIle = new DataFile(rs.getInt("Id"));
				dataFIle.setName(rs.getString("Name"));
				result.add(dataFIle);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataStorage getDataStorage(int id) {
		DataStorage result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorages\" WHERE \"Id\"=" + id);

			if (rs.next()) {
				result = new DataStorage(rs.getInt("Id"));
				result.setCategoryId(rs.getInt("CategoryId"));
				result.setName(rs.getString("Name"));
				result.setClassName(rs.getString("ClassName"));
				result.setShowUsage(rs.getBoolean("ShowUsage"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public int insertDataStorage(String name, int categoryId, String className) {
		int id = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"DataStorages\"(\"CategoryId\", \"Name\", \"ClassName\") VALUES ("
					+ categoryId + ", '" + name + "', '" + className + "')");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorages\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				id = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return id;
	}

	public List<Integer> insertDataStorageKeys(int storageId, List<String> keys) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keys.size(); i++) {
				stmt.executeUpdate("INSERT INTO \"DataStorageKeys\""
						+ "(\"DataStorageId\", \"Name\", \"Order\", \"ReferenceStorageId\") VALUES (" + storageId
						+ ", '" + keys.get(i) + "', " + (i + 1) + ", NULL)");
				ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorageKeys\" ORDER BY \"Id\" DESC LIMIT 1");
				if (rs.next()) {
					result.add(rs.getInt("Id"));
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public int insertCategory(int productId, String name) {
		int id = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"Categories\"(\"ProductId\", \"Name\") VALUES (" + productId + ", '"
					+ name + "')");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"Categories\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				id = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return id;
	}

	public int getProductIdByDataFileId(int id) {
		int productId = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT \"ProductId\" FROM \"Categories\" WHERE \"Id\"="
					+ "(SELECT \"CategoryId\" FROM \"DataFiles\" WHERE \"Id\"=" + id + ")");
			if (rs.next()) {
				productId = rs.getInt("ProductId");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return productId;
	}

	public int getProductIdByDataStorageId(int id) {
		int productId = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT \"ProductId\" FROM \"StorageCategories\" WHERE \"Id\"="
					+ "(SELECT \"CategoryId\" FROM \"DataStorages\" WHERE \"Id\"=" + id + ")");
			if (rs.next()) {
				productId = rs.getInt("ProductId");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return productId;
	}

	public int insertStorageCategory(int productId, String name) {
		int id = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"StorageCategories\"(\"ProductId\", \"Name\") VALUES (" + productId
					+ ", '" + name + "')");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"StorageCategories\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				id = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return id;
	}

	public int insertDataFile(String name, int categoryId, boolean hasPreconditions, boolean hasPostconditions) {
		int id = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"DataFiles\"(\"CategoryId\", \"Name\", \"HasPreconditions\", \"HasPostconditions\") VALUES ("
					+ categoryId + ", '" + name + "', " + hasPreconditions + ", " + hasPostconditions + ")");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFiles\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				id = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return id;
	}

	public List<Integer> insertDataFileKeys(int fileId, List<String> keys) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keys.size(); i++) {
				stmt.executeUpdate("INSERT INTO \"DataFileKeys\""
						+ "(\"DataFileId\", \"Name\", \"Order\", \"ReferenceStorageId\") VALUES (" + fileId + ", '"
						+ keys.get(i) + "', " + (i + 1) + ", NULL)");
				ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFileKeys\" ORDER BY \"Id\" DESC LIMIT 1");
				if (rs.next()) {
					result.add(rs.getInt("Id"));
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataStorageKey> getDataStorageKeys(int storageId) {
		List<DataStorageKey> result = new ArrayList<DataStorageKey>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageKeys\" WHERE \"DataStorageId\"=" + storageId
					+ " ORDER BY \"Order\"");
			while (rs.next()) {
				DataStorageKey key = new DataStorageKey(rs.getInt("Id"));
				key.setStorageId(rs.getInt("DataStorageId"));
				key.setName(rs.getString("Name"));
				key.setOrder(rs.getInt("Order"));
				key.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
				result.add(key);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataStorageKey getDataStorageKey(int keyId) {
		DataStorageKey result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageKeys\" WHERE \"Id\"=" + keyId);
			if (rs.next()) {
				result = new DataStorageKey(rs.getInt("Id"));
				result.setStorageId(rs.getInt("DataStorageId"));
				result.setName(rs.getString("Name"));
				result.setOrder(rs.getInt("Order"));
				result.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataStorageKey getDataStorageKey(DataStorageValue value) {
		DataStorageKey result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM \"DataStorageKeys\" WHERE \"Id\"=(SELECT \"KeyId\" FROM \"DataStorageValues\" WHERE \"Id\"="
							+ value.getId() + ")");
			if (rs.next()) {
				result = new DataStorageKey(rs.getInt("Id"));
				result.setStorageId(rs.getInt("DataStorageId"));
				result.setName(rs.getString("Name"));
				result.setOrder(rs.getInt("Order"));
				result.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataStorageRow> getDataStorageRows(int storageId) {
		List<DataStorageRow> result = new ArrayList<DataStorageRow>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageRows\" WHERE \"DataStorageId\"=" + storageId
					+ " ORDER BY \"Order\"");
			while (rs.next()) {
				DataStorageRow row = new DataStorageRow(rs.getInt("Id"));
				row.setOrder(rs.getInt("Order"));
				result.add(row);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataStorageRow getDataStorageRow(int rowId) {
		DataStorageRow result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageRows\" WHERE \"Id\"=" + rowId);
			if (rs.next()) {
				result = new DataStorageRow(rs.getInt("Id"));
				result.setStorageId(rs.getInt("DataStorageId"));
				result.setOrder(rs.getInt("Order"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataStorageRow getDataStorageRow(int storageId, int order) {
		DataStorageRow result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageRows\" WHERE \"DataStorageId\"=" + storageId
					+ " AND \"Order\"=" + order);
			if (rs.next()) {
				result = new DataStorageRow(rs.getInt("Id"));
				result.setOrder(rs.getInt("Order"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<Integer> insertDataStorageRows(int storageId, int rowsNumber) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < rowsNumber; i++) {
				stmt.executeUpdate("INSERT INTO \"DataStorageRows\"" + "(\"DataStorageId\", \"Order\") VALUES ("
						+ storageId + ", " + (i + 1) + ")");
				ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorageRows\" ORDER BY \"Id\" DESC LIMIT 1");
				if (rs.next()) {
					result.add(rs.getInt("Id"));
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<Integer> insertDataFileRows(int fileId, int rowsNumber) {
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < rowsNumber; i++) {
				stmt.executeUpdate("INSERT INTO \"DataFileRows\"" + "(\"DataFileId\", \"Order\") VALUES (" + fileId
						+ ", " + (i + 1) + ")");
				ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFileRows\" ORDER BY \"Id\" DESC LIMIT 1");
				if (rs.next()) {
					result.add(rs.getInt("Id"));
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void insertDataStorageValues(List<Integer> rowIds, List<Integer> keyIds, ArrayList<ArrayList<String>> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < rowIds.size(); i++) {
				int rowId = rowIds.get(i);
				ArrayList<String> rowValues = values.get(i);
				for (int j = 0; j < keyIds.size(); j++) {
					int keyId = keyIds.get(j);
					String value = rowValues.get(j).replace("'", "''");
					stmt.executeUpdate("INSERT INTO \"DataStorageValues\""
							+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES (" + rowId
							+ ", " + keyId + ", '" + value + "', false, NULL)");
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertDataFileValues(List<Integer> rowIds, List<Integer> keyIds, ArrayList<ArrayList<String>> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < rowIds.size(); i++) {
				int rowId = rowIds.get(i);
				ArrayList<String> rowValues = values.get(i);
				for (int j = 0; j < keyIds.size(); j++) {
					int keyId = keyIds.get(j);
					String value = rowValues.get(j).replace("'", "''");
					stmt.executeUpdate("INSERT INTO \"DataFileValues\""
							+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES (" + rowId
							+ ", " + keyId + ", '" + value + "', false, NULL)");
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public ArrayList<ArrayList<DataStorageValue>> getDataStorageValues(List<DataStorageRow> rows,
			List<DataStorageKey> keys) {
		ArrayList<ArrayList<DataStorageValue>> result = new ArrayList<ArrayList<DataStorageValue>>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < rows.size(); i++) {
				int rowId = rows.get(i).getId();
				ArrayList<DataStorageValue> rowValues = new ArrayList<DataStorageValue>();
				for (int j = 0; j < keys.size(); j++) {
					int keyId = keys.get(j).getId();
					ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageValues\" WHERE \"RowId\"=" + rowId
							+ " AND \"KeyId\"=" + keyId + " ORDER BY \"Id\"");
					if (rs.next()) {
						DataStorageValue value = new DataStorageValue(rs.getInt("Id"));
						value.setKeyId(keyId);
						value.setRowId(rowId);
						value.setValue(rs.getString("Value"));
						value.setIsStorage(rs.getBoolean("IsStorage"));
						if (rs.getArray("StorageRows") == null) {
							value.setStorageIds(null);
						} else {
							value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
						}
						rowValues.add(value);
					}
				}
				result.add(rowValues);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public ArrayList<DataStorageValue> getDataStorageValues(DataStorageKey key) {
		ArrayList<DataStorageValue> result = new ArrayList<DataStorageValue>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageValues\" WHERE \"KeyId\"=" + key.getId()
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				DataStorageValue value = new DataStorageValue(rs.getInt("Id"));
				value.setKeyId(key.getId());
				value.setRowId(rs.getInt("RowId"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public ArrayList<DataStorageValue> getDataStorageValues(int[] ids) {
		ArrayList<DataStorageValue> result = new ArrayList<DataStorageValue>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			String strIds = "(";
			for (int i = 0; i < ids.length; i++) {
				strIds += (i > 0) ? ("," + ids[i]) : ids[i];
			}
			strIds += ")";
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageValues\" WHERE \"Id\" IN " + strIds
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				DataStorageValue value = new DataStorageValue(rs.getInt("Id"));
				value.setKeyId(rs.getInt("KeyId"));
				value.setRowId(rs.getInt("RowId"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") != null) {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				} else {
					value.setStorageIds(null);
				}
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public ArrayList<DataStorageValue> getDataStorageValues(DataStorageRow row) {
		ArrayList<DataStorageValue> result = new ArrayList<DataStorageValue>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT values.\"Id\", values.\"RowId\", values.\"KeyId\", values.\"Value\", "
							+ "values.\"IsStorage\", values.\"StorageRows\" FROM \"DataStorageValues\" "
							+ "as values JOIN \"DataStorageKeys\" as keys ON values.\"RowId\"=" + row.getId()
							+ " AND values.\"KeyId\"=keys.\"Id\" ORDER BY keys.\"Order\"");
			while (rs.next()) {
				DataStorageValue value = new DataStorageValue(rs.getInt("Id"));
				value.setKeyId(rs.getInt("KeyId"));
				value.setRowId(row.getId());
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public ArrayList<DataFileValue> getDataFileValues(DataFileRow row) {
		ArrayList<DataFileValue> result = new ArrayList<DataFileValue>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT values.\"Id\", values.\"RowId\", values.\"KeyId\", values.\"Value\", "
							+ "values.\"IsStorage\", values.\"StorageRows\" FROM \"DataFileValues\" "
							+ "as values JOIN \"DataFileKeys\" as keys ON values.\"RowId\"=" + row.getId()
							+ " AND values.\"KeyId\"=keys.\"Id\" ORDER BY keys.\"Order\"");
			while (rs.next()) {
				DataFileValue value = new DataFileValue(rs.getInt("Id"));
				value.setKeyId(rs.getInt("KeyId"));
				value.setRowId(row.getId());
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataStorageValue getDataStorageValue(int valueId) {
		DataStorageValue result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageValues\" WHERE \"Id\"=" + valueId);
			if (rs.next()) {
				result = new DataStorageValue(valueId);
				result.setKeyId(rs.getInt("KeyId"));
				result.setRowId(rs.getInt("RowId"));
				result.setValue(rs.getString("Value"));
				result.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					result.setStorageIds(null);
				} else {
					result.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	private Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:postgresql://" + sqlserver + ":" + sqlport + "/"
				+ sqldatabase, sqllogin, sqlpassword);
		return conn;
	}

	private void initializeSQLDriver() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Unable to load driver.");
			e.printStackTrace();
		}
	}

	private static void printExeptionInformation(SQLException e) {
		System.out.println("========================= SQL Exception =========================");
		e.printStackTrace();
	}

	public void updateDataStorageKey(DataStorageKey key) {
		try {
			String refStorageId = (key.getReferenceStorageId() == 0) ? "NULL" : String.valueOf(key
					.getReferenceStorageId());
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataStorageKeys\" " + "SET \"Name\"='" + key.getName() + "', " + "\"Order\"="
					+ key.getOrder() + ", " + "\"ReferenceStorageId\"=" + refStorageId + " " + "WHERE \"Id\"="
					+ key.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataStorageValuesTypes(int keyId, Boolean isStorage, String storageIds) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataStorageValues\" " + "SET \"IsStorage\"=" + isStorage + ", "
					+ "\"StorageRows\"=" + storageIds + " " + "WHERE \"KeyId\"=" + keyId);
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataStorageValue(DataStorageValue value) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataStorageValues\" " + "SET \"RowId\"=" + value.getRowId() + ", \"KeyId\"="
					+ value.getKeyId() + ", \"Value\"='" + value.getValue().replace("'", "''") + "', \"IsStorage\"="
					+ value.isStorage() + ", " + "\"StorageRows\"=" + value.getStorageIdsAsString() + " "
					+ "WHERE \"Id\"=" + value.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	/**
	 * @param sotageId
	 * @return true, if storage was deleted, otherwise false.
	 */
	public boolean deleteStorage(int sotageId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"DataStorageValues\" WHERE \"RowId\" IN "
					+ "(SELECT \"Id\" FROM \"DataStorageRows\" WHERE \"DataStorageId\"=" + sotageId + ")");
			stmt.executeUpdate("DELETE FROM \"DataStorageKeys\" WHERE \"DataStorageId\"=" + sotageId);
			stmt.executeUpdate("DELETE FROM \"DataStorageRows\" WHERE \"DataStorageId\"=" + sotageId);
			stmt.executeUpdate("DELETE FROM \"DataStorages\" WHERE \"Id\"=" + sotageId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorages\" WHERE \"Id\"=" + sotageId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void insertPreconditions(int dataFileId, HashMap<String, String> precondition) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (String key : precondition.keySet()) {
				stmt.executeUpdate("INSERT INTO \"Preconditions\""
						+ "(\"DataFileId\", \"Key\", \"Value\", \"IsStorage\", \"StorageRows\", \"ReferenceStorageId\") VALUES ("
						+ dataFileId + ", '" + key + "', '" + precondition.get(key).replace("'", "''")
						+ "', false, NULL, NULL)");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertPrecondition(int anotherPreconditionId) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("INSERT INTO \"Preconditions\""
					+ "(\"DataFileId\", \"Key\", \"Value\", \"IsStorage\", \"StorageRows\", \"ReferenceStorageId\") VALUES ("
					+ "(SELECT \"DataFileId\" FROM \"Preconditions\" WHERE \"Id\"=" + anotherPreconditionId
					+ "), 'edit-me', '', false, NULL, NULL)");
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertPostcondition(int anotherPostconditionId) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();

			stmt.executeUpdate("INSERT INTO \"Postconditions\""
					+ "(\"DataFileId\", \"Key\", \"Value\", \"IsStorage\", \"StorageRows\", \"ReferenceStorageId\") VALUES ("
					+ "(SELECT \"DataFileId\" FROM \"Postconditions\" WHERE \"Id\"=" + anotherPostconditionId
					+ "), 'edit-me', '', false, NULL, NULL)");
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertPostconditions(int dataFileId, HashMap<String, String> postcondition) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (String key : postcondition.keySet()) {
				stmt.executeUpdate("INSERT INTO \"Postconditions\""
						+ "(\"DataFileId\", \"Key\", \"Value\", \"IsStorage\", \"StorageRows\", \"ReferenceStorageId\") VALUES ("
						+ dataFileId + ", '" + key + "', '" + postcondition.get(key).replace("'", "''")
						+ "', false, NULL, NULL)");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public List<DataFileKey> getDataFileKeys(int dataFileId) {
		List<DataFileKey> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileKeys\" WHERE \"DataFileId\"=" + dataFileId
					+ " ORDER BY \"Order\"");
			while (rs.next()) {
				DataFileKey key = new DataFileKey(rs.getInt("Id"));
				key.setFileId(rs.getInt("DataFileId"));
				key.setName(rs.getString("Name"));
				key.setOrder(rs.getInt("Order"));
				key.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
				result.add(key);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataFileRow> getDataFileRows(int dataFileId) {
		List<DataFileRow> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileRows\" WHERE \"DataFileId\"=" + dataFileId
					+ " ORDER BY \"Order\"");
			while (rs.next()) {
				DataFileRow row = new DataFileRow(rs.getInt("Id"));
				row.setOrder(rs.getInt("Order"));
				result.add(row);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public ArrayList<ArrayList<DataFileValue>> getDataFileValues(List<DataFileRow> rows, List<DataFileKey> keys) {
		ArrayList<ArrayList<DataFileValue>> result = new ArrayList<ArrayList<DataFileValue>>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < rows.size(); i++) {
				int rowId = rows.get(i).getId();
				ArrayList<DataFileValue> rowValues = new ArrayList<DataFileValue>();
				for (int j = 0; j < keys.size(); j++) {
					int keyId = keys.get(j).getId();
					ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileValues\" WHERE \"RowId\"=" + rowId
							+ " AND \"KeyId\"=" + keyId + " ORDER BY \"Id\"");
					if (rs.next()) {
						DataFileValue value = new DataFileValue(rs.getInt("Id"));
						value.setKeyId(keyId);
						value.setRowId(rowId);
						value.setValue(rs.getString("Value"));
						value.setIsStorage(rs.getBoolean("IsStorage"));
						if (rs.getArray("StorageRows") == null) {
							value.setStorageIds(null);
						} else {
							value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
						}
						rowValues.add(value);
					}
				}
				result.add(rowValues);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataFileKey getDataFileKey(int keyId) {
		DataFileKey result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileKeys\" WHERE \"Id\"=" + keyId);
			if (rs.next()) {
				result = new DataFileKey(rs.getInt("Id"));
				result.setFileId(rs.getInt("DataFileId"));
				result.setName(rs.getString("Name"));
				result.setOrder(rs.getInt("Order"));
				result.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataFileKey(DataFileKey key) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataFileKeys\" " + "SET \"Name\"='" + key.getName() + "', " + "\"Order\"="
					+ key.getOrder() + ", " + "\"ReferenceStorageId\"=" + key.getReferenceStorageIdAsString() + " "
					+ "WHERE \"Id\"=" + key.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataFileValuesTypes(int keyId, boolean isStorage, String storageIds) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataFileValues\" " + "SET \"IsStorage\"=" + isStorage + ", "
					+ "\"StorageRows\"=" + storageIds + " " + "WHERE \"KeyId\"=" + keyId);
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public ArrayList<DataFileValue> getDataFileValues(DataFileKey key) {
		ArrayList<DataFileValue> result = new ArrayList<DataFileValue>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileValues\" WHERE \"KeyId\"=" + key.getId()
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				DataFileValue value = new DataFileValue(rs.getInt("Id"));
				value.setKeyId(key.getId());
				value.setRowId(rs.getInt("RowId"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataFileValue(DataFileValue value) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataFileValues\" " + "SET \"RowId\"=" + value.getRowId() + ", \"KeyId\"="
					+ value.getKeyId() + ", \"Value\"='" + value.getValue().replace("'", "''") + "', \"IsStorage\"="
					+ value.isStorage() + ", " + "\"StorageRows\"=" + value.getStorageIdsAsString() + " "
					+ "WHERE \"Id\"=" + value.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public DataFileRow getDataFileRow(int rowId) {
		DataFileRow result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileRows\" WHERE \"Id\"=" + rowId);
			if (rs.next()) {
				result = new DataFileRow(rs.getInt("Id"));
				result.setFileId(rs.getInt("DataFileId"));
				result.setOrder(rs.getInt("Order"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataFileValue getDataFileValue(int valueId) {
		DataFileValue result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileValues\" WHERE \"Id\"=" + valueId);
			if (rs.next()) {
				result = new DataFileValue(valueId);
				result.setKeyId(rs.getInt("KeyId"));
				result.setRowId(rs.getInt("RowId"));
				result.setValue(rs.getString("Value"));
				result.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					result.setStorageIds(null);
				} else {
					result.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public boolean deleteDataFile(int fileId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"Preconditions\" WHERE \"DataFileId\"=" + fileId);
			stmt.executeUpdate("DELETE FROM \"Postconditions\" WHERE \"DataFileId\"=" + fileId);
			stmt.executeUpdate("DELETE FROM \"DataFileValues\" WHERE \"RowId\" IN "
					+ "(SELECT \"Id\" FROM \"DataFileRows\" WHERE \"DataFileId\"=" + fileId + ")");
			stmt.executeUpdate("DELETE FROM \"DataFileKeys\" WHERE \"DataFileId\"=" + fileId);
			stmt.executeUpdate("DELETE FROM \"DataFileRows\" WHERE \"DataFileId\"=" + fileId);
			stmt.executeUpdate("DELETE FROM \"DataFiles\" WHERE \"Id\"=" + fileId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFiles\" WHERE \"Id\"=" + fileId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataFile getDataFile(int id) {
		DataFile result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFiles\" WHERE \"Id\"=" + id);

			if (rs.next()) {
				result = new DataFile(rs.getInt("Id"));
				result.setName(rs.getString("Name"));
				result.setCategoryId(rs.getInt("CategoryId"));
				result.setHasPreconditions(rs.getBoolean("HasPreconditions"));
				result.setHasPostconditions(rs.getBoolean("HasPostconditions"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<PreconditionValue> getPreconditionValues(int dataFileId) {
		ArrayList<PreconditionValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Preconditions\" WHERE \"DataFileId\"=" + dataFileId
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				PreconditionValue value = new PreconditionValue(rs.getInt("Id"));
				value.setDataFileid(dataFileId);
				value.setKey(rs.getString("Key"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				value.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<PostconditionValue> getPostconditionValues(int dataFileId) {
		ArrayList<PostconditionValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Postconditions\" WHERE \"DataFileId\"=" + dataFileId
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				PostconditionValue value = new PostconditionValue(rs.getInt("Id"));
				value.setDataFileid(dataFileId);
				value.setKey(rs.getString("Key"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				value.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public PreconditionValue getPreconditionValue(int id) {
		PreconditionValue result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Preconditions\" WHERE \"Id\"=" + id);
			if (rs.next()) {
				result = new PreconditionValue(rs.getInt("Id"));
				result.setDataFileid(id);
				result.setKey(rs.getString("Key"));
				result.setValue(rs.getString("Value"));
				result.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					result.setStorageIds(null);
				} else {
					result.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				result.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public PostconditionValue getPostconditionValue(int id) {
		PostconditionValue result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Postconditions\" WHERE \"Id\"=" + id);
			if (rs.next()) {
				result = new PostconditionValue(rs.getInt("Id"));
				result.setDataFileid(id);
				result.setKey(rs.getString("Key"));
				result.setValue(rs.getString("Value"));
				result.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					result.setStorageIds(null);
				} else {
					result.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				result.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updatePreconditionValue(PreconditionValue key) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"Preconditions\" " + "SET \"Key\"='" + key.getKey() + "', \"Value\"='"
					+ key.getValue().replace("'", "''") + "', \"IsStorage\"=" + key.isStorage() + ", \"StorageRows\"="
					+ key.getStorageIdsAsString() + ", \"ReferenceStorageId\"=" + key.getReferenceStorageIdAsString()
					+ " WHERE \"Id\"=" + key.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updatePreconditionValues(ArrayList<PreconditionValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (PreconditionValue value : values) {
				stmt.executeUpdate("UPDATE \"Preconditions\" " + "SET \"Key\"='" + value.getKey() + "', \"Value\"='"
						+ value.getValue().replace("'", "''") + "', \"IsStorage\"=" + value.isStorage()
						+ ", \"StorageRows\"=" + value.getStorageIdsAsString() + ", \"ReferenceStorageId\"="
						+ value.getReferenceStorageIdAsString() + " WHERE \"Id\"=" + value.getId());
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updatePostconditionValues(ArrayList<PostconditionValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (PostconditionValue value : values) {
				stmt.executeUpdate("UPDATE \"Postconditions\" " + "SET \"Key\"='" + value.getKey() + "', \"Value\"='"
						+ value.getValue().replace("'", "''") + "', \"IsStorage\"=" + value.isStorage()
						+ ", \"StorageRows\"=" + value.getStorageIdsAsString() + ", \"ReferenceStorageId\"="
						+ value.getReferenceStorageIdAsString() + " WHERE \"Id\"=" + value.getId());
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updatePostconditionValue(PostconditionValue key) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"Postconditions\" " + "SET \"Key\"='" + key.getKey() + "', \"Value\"='"
					+ key.getValue().replace("'", "''") + "', \"IsStorage\"=" + key.isStorage() + ", \"StorageRows\"="
					+ key.getStorageIdsAsString() + ", \"ReferenceStorageId\"=" + key.getReferenceStorageIdAsString()
					+ " WHERE \"Id\"=" + key.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataStorageValues(List<DataStorageValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataStorageValue value : values) {
				stmt.executeUpdate("UPDATE \"DataStorageValues\" " + "SET \"RowId\"=" + value.getRowId()
						+ ", \"KeyId\"=" + value.getKeyId() + ", \"Value\"='" + value.getValue().replace("'", "''")
						+ "', \"IsStorage\"=" + value.isStorage() + ", " + "\"StorageRows\"="
						+ value.getStorageIdsAsString() + " " + "WHERE \"Id\"=" + value.getId());
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataStorageKeys(String[] keyIds, String[] keyValues) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keyIds.length; i++) {
				stmt.executeUpdate("UPDATE \"DataStorageKeys\" SET \"Name\"='" + keyValues[i] + "' " + "WHERE \"Id\"="
						+ keyIds[i]);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataStorageRows(List<Integer> modifiedRowIds, List<Integer> oldRowNumbers,
			List<Integer> modifiedRowNumbers) {
		try {
			List<DataStorageValue> dsValues = new ArrayList<>();
			List<DataFileValue> dfValues = new ArrayList<>();
			List<PreconditionValue> prValues = new ArrayList<>();
			List<PostconditionValue> postValues = new ArrayList<>();
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < modifiedRowIds.size(); i++) {
				stmt.executeUpdate("UPDATE \"DataStorageRows\" SET \"Order\"=" + modifiedRowNumbers.get(i) + " "
						+ "WHERE \"Id\"=" + modifiedRowIds.get(i));

				ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorageValues\" " + "WHERE "
						+ modifiedRowIds.get(i) + " = ANY (\"StorageRows\")");
				while (rs.next()) {
					DataStorageValue dsValue = new DataStorageValue(rs.getInt("Id"));
					dsValue.setKeyId(rs.getInt("KeyId"));
					dsValue.setRowId(rs.getInt("RowId"));
					dsValue.setValue(rs.getString("Value"));
					dsValue.setIsStorage(true);
					dsValue.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
					dsValues.add(dsValue);
				}

				rs = stmt.executeQuery("SELECT * FROM \"DataFileValues\" " + "WHERE " + modifiedRowIds.get(i)
						+ " = ANY (\"StorageRows\")");
				while (rs.next()) {
					DataFileValue dfValue = new DataFileValue(rs.getInt("Id"));
					dfValue.setKeyId(rs.getInt("KeyId"));
					dfValue.setRowId(rs.getInt("RowId"));
					dfValue.setValue(rs.getString("Value"));
					dfValue.setIsStorage(true);
					dfValue.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
					dfValues.add(dfValue);
				}

				rs = stmt.executeQuery("SELECT * FROM \"Preconditions\" " + "WHERE " + modifiedRowIds.get(i)
						+ " = ANY (\"StorageRows\")");
				while (rs.next()) {
					PreconditionValue prValue = new PreconditionValue(rs.getInt("Id"));
					prValue.setDataFileid(rs.getInt("DataFileId"));
					prValue.setKey(rs.getString("Key"));
					prValue.setValue(rs.getString("Value"));
					prValue.setIsStorage(rs.getBoolean("IsStorage"));
					prValue.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
					prValue.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
					prValues.add(prValue);
				}

				rs = stmt.executeQuery("SELECT * FROM \"Postconditions\" " + "WHERE " + modifiedRowIds.get(i)
						+ " = ANY (\"StorageRows\")");
				while (rs.next()) {
					PostconditionValue postValue = new PostconditionValue(rs.getInt("Id"));
					postValue.setDataFileid(rs.getInt("DataFileId"));
					postValue.setKey(rs.getString("Key"));
					postValue.setValue(rs.getString("Value"));
					postValue.setIsStorage(rs.getBoolean("IsStorage"));
					postValue.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
					postValue.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
					postValues.add(postValue);
				}
				rs.close();
			}

			for (DataStorageValue dsValue : dsValues) {
				String oldValueRows[] = dsValue.getValue().split(";");
				String newValueRows[] = oldValueRows.clone();
				for (int i = 0; i < oldValueRows.length; i++) {
					for (int j = 0; j < oldRowNumbers.size(); j++) {
						if (oldValueRows[i].equals(String.valueOf(oldRowNumbers.get(j)))) {
							newValueRows[i] = String.valueOf(modifiedRowNumbers.get(j));
						}
					}
				}
				String newValue = StringUtils.join(newValueRows, ";");
				dsValue.setValue(newValue);
				updateDataStorageValue(dsValue);
			}

			for (DataFileValue dfValue : dfValues) {
				String oldValueRows[] = dfValue.getValue().split(";");
				String newValueRows[] = oldValueRows.clone();
				for (int i = 0; i < oldValueRows.length; i++) {
					for (int j = 0; j < oldRowNumbers.size(); j++) {
						if (oldValueRows[i].equals(String.valueOf(oldRowNumbers.get(j)))) {
							newValueRows[i] = String.valueOf(modifiedRowNumbers.get(j));
						}
					}
				}
				String newValue = StringUtils.join(newValueRows, ";");
				dfValue.setValue(newValue);
				updateDataFileValue(dfValue);
			}

			for (PreconditionValue prValue : prValues) {
				String oldValueRows[] = prValue.getValue().split(";");
				String newValueRows[] = oldValueRows.clone();
				for (int i = 0; i < oldValueRows.length; i++) {
					for (int j = 0; j < oldRowNumbers.size(); j++) {
						if (oldValueRows[i].equals(String.valueOf(oldRowNumbers.get(j)))) {
							newValueRows[i] = String.valueOf(modifiedRowNumbers.get(j));
						}
					}
				}
				String newValue = StringUtils.join(newValueRows, ";");
				prValue.setValue(newValue);
				updatePreconditionValue(prValue);
			}

			for (PostconditionValue postValue : postValues) {
				String oldValueRows[] = postValue.getValue().split(";");
				String newValueRows[] = oldValueRows.clone();
				for (int i = 0; i < oldValueRows.length; i++) {
					for (int j = 0; j < oldRowNumbers.size(); j++) {
						if (oldValueRows[i].equals(String.valueOf(oldRowNumbers.get(j)))) {
							newValueRows[i] = String.valueOf(modifiedRowNumbers.get(j));
						}
					}
				}
				String newValue = StringUtils.join(newValueRows, ";");
				postValue.setValue(newValue);
				updatePostconditionValue(postValue);
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataFileKeys(String[] keyIds, String[] keyValues) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keyIds.length; i++) {
				stmt.executeUpdate("UPDATE \"DataFileKeys\" SET \"Name\"='" + keyValues[i] + "' " + "WHERE \"Id\"="
						+ keyIds[i]);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataFileRows(List<Integer> modifiedRowIds, List<Integer> modifiedRowNumbers) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < modifiedRowIds.size(); i++) {
				stmt.executeUpdate("UPDATE \"DataFileRows\" SET \"Order\"=" + modifiedRowNumbers.get(i) + " "
						+ "WHERE \"Id\"=" + modifiedRowIds.get(i));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataFileKeysOrder(int[] modifiedKeyIds, int[] modifiedKeyNumbers) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < modifiedKeyIds.length; i++) {
				stmt.executeUpdate("UPDATE \"DataFileKeys\" SET \"Order\"=" + modifiedKeyNumbers[i] + " "
						+ "WHERE \"Id\"=" + modifiedKeyIds[i]);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public ArrayList<DataFileValue> getDataFileValues(int[] ids) {
		ArrayList<DataFileValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			String strIds = "(";
			for (int i = 0; i < ids.length; i++) {
				strIds += (i > 0) ? ("," + ids[i]) : ids[i];
			}
			strIds += ")";
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFileValues\" WHERE \"Id\" IN " + strIds
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				DataFileValue value = new DataFileValue(rs.getInt("Id"));
				value.setKeyId(rs.getInt("KeyId"));
				value.setRowId(rs.getInt("RowId"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") != null) {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				} else {
					value.setStorageIds(null);
				}
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public DataFileKey getDataFileKey(DataFileValue value) {
		DataFileKey result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM \"DataFileKeys\" WHERE \"Id\"=(SELECT \"KeyId\" FROM \"DataFileValues\" WHERE \"Id\"="
							+ value.getId() + ")");
			if (rs.next()) {
				result = new DataFileKey(rs.getInt("Id"));
				result.setFileId(rs.getInt("DataFileId"));
				result.setName(rs.getString("Name"));
				result.setOrder(rs.getInt("Order"));
				result.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataFileValues(ArrayList<DataFileValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataFileValue value : values) {
				stmt.executeUpdate("UPDATE \"DataFileValues\" " + "SET \"RowId\"=" + value.getRowId() + ", \"KeyId\"="
						+ value.getKeyId() + ", \"Value\"='" + value.getValue().replace("'", "''")
						+ "', \"IsStorage\"=" + value.isStorage() + ", " + "\"StorageRows\"="
						+ value.getStorageIdsAsString() + " " + "WHERE \"Id\"=" + value.getId());
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public int insertDataFileRowCopy(DataFileRow row) {
		int result = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"DataFileRows\"(\"DataFileId\", \"Order\") VALUES (" + row.getFileId()
					+ ", " + row.getOrder() + ")");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFileRows\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				result = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void insertDataFileValuesEmptyWithRowId(int rowId, List<DataFileKey> keys) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataFileKey key : keys) {
				DataFileValue tempValue = new DataFileValue(0);
				tempValue.setIsStorage(false);
				tempValue.setValue("");
				tempValue.setKeyId(key.getId());
				tempValue.setRowId(rowId);
				tempValue.setStorageIds(null);
				if (key.getReferenceStorageId() != 0) {
					tempValue.setIsStorage(true);
					tempValue.setValue("0");
				}

				stmt.executeUpdate("INSERT INTO \"DataFileValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES ("
						+ tempValue.getRowId() + ", " + tempValue.getKeyId() + ", '" + tempValue.getValue() + "', "
						+ tempValue.isStorage() + ", " + tempValue.getStorageIdsAsString() + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertDataFileValuesWithRowId(int rowId, List<DataFileValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataFileValue value : values) {
				stmt.executeUpdate("INSERT INTO \"DataFileValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES (" + rowId
						+ ", " + value.getKeyId() + ", '" + value.getValue() + "', " + value.isStorage() + ", "
						+ value.getStorageIdsAsString() + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public boolean deleteDataFileRow(int rowId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"DataFileValues\" WHERE \"RowId\"=" + rowId);
			stmt.executeUpdate("DELETE FROM \"DataFileRows\" WHERE \"Id\"=" + rowId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFileRows\" WHERE \"Id\"=" + rowId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataFileKeys(List<Integer> keyIds, List<Integer> keyNumbers) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keyIds.size(); i++) {
				stmt.executeUpdate("UPDATE \"DataFileKeys\" SET \"Order\"=" + keyNumbers.get(i) + " " + "WHERE \"Id\"="
						+ keyIds.get(i));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateDataStorageKeys(List<Integer> keyIds, List<Integer> keyNumbers) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keyIds.size(); i++) {
				stmt.executeUpdate("UPDATE \"DataStorageKeys\" SET \"Order\"=" + keyNumbers.get(i) + " "
						+ "WHERE \"Id\"=" + keyIds.get(i));
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public int insertDataFileKeyCopy(DataFileKey key) {
		int result = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"DataFileKeys\"(\"DataFileId\", \"Name\", \"Order\") VALUES ("
					+ key.getFileId() + ", 'edit-me'," + key.getOrder() + ")");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFileKeys\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				result = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public int insertDataStorageKeyCopy(DataStorageKey key) {
		int result = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"DataStorageKeys\"(\"DataStorageId\", \"Name\", \"Order\") VALUES ("
					+ key.getStorageId() + ", 'edit-me'," + key.getOrder() + ")");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorageKeys\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				result = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void insertDataFileValuesEmptyWithKeyId(int keyId, List<DataFileRow> rows) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataFileRow row : rows) {
				stmt.executeUpdate("INSERT INTO \"DataFileValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES ("
						+ row.getId() + ", " + keyId + ", '', false, null)");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertDataFileValuesWithKeyId(int keyId, List<DataFileValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataFileValue value : values) {
				stmt.executeUpdate("INSERT INTO \"DataFileValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES ("
						+ value.getRowId() + ", " + keyId + ", '" + value.getValue() + "', " + value.isStorage() + ", "
						+ value.getStorageIdsAsString() + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertDataStorageValuesEmptyWithKeyId(int keyId, List<DataStorageRow> rows) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataStorageRow row : rows) {
				stmt.executeUpdate("INSERT INTO \"DataStorageValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES ("
						+ row.getId() + ", " + keyId + ", '', false, null)");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertDataStorageValuesWithKeyId(int keyId, List<DataStorageValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataStorageValue value : values) {
				stmt.executeUpdate("INSERT INTO \"DataStorageValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES ("
						+ value.getRowId() + ", " + keyId + ", '" + value.getValue() + "', " + value.isStorage() + ", "
						+ value.getStorageIdsAsString() + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public boolean deleteDataFileKey(int keyId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"DataFileValues\" WHERE \"KeyId\"=" + keyId);
			stmt.executeUpdate("DELETE FROM \"DataFileKeys\" WHERE \"Id\"=" + keyId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFileKeys\" WHERE \"Id\"=" + keyId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public Category getCategory(int id) {
		Category result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Categories\" WHERE \"Id\"=" + id);

			if (rs.next()) {
				result = new Category(rs.getInt("Id"));
				result.setName(rs.getString("Name"));
				result.setProductId(rs.getInt("ProductId"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public StorageCategory getStorageCategory(int id) {
		StorageCategory result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"StorageCategories\" WHERE \"Id\"=" + id);

			if (rs.next()) {
				result = new StorageCategory(rs.getInt("Id"));
				result.setName(rs.getString("Name"));
				result.setProductId(rs.getInt("ProductId"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public Category getCategoryByName(String name, int productId) {
		Category result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Categories\" WHERE \"Name\"='" + name
					+ "' AND \"ProductId\"=" + productId);

			if (rs.next()) {
				result = new Category(rs.getInt("Id"));
				result.setName(rs.getString("Name"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateCategory(Category category) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"Categories\" SET \"Name\"='" + category.getName() + "' WHERE \"Id\"="
					+ category.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateStorageCategory(StorageCategory category) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"StorageCategories\" SET \"Name\"='" + category.getName() + "' WHERE \"Id\"="
					+ category.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public StorageCategory getStorageCategoryByName(String name, int productId) {
		StorageCategory result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"StorageCategories\" WHERE \"Name\"='" + name
					+ "' AND \"ProductId\"=" + productId);

			if (rs.next()) {
				result = new StorageCategory(rs.getInt("Id"));
				result.setName(rs.getString("Name"));
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public boolean deleteCategory(int categoryId) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataFiles\" WHERE \"CategoryId\"=" + categoryId);

			while (rs.next()) {
				if (!deleteDataFile(rs.getInt("Id"))) {
					return false;
				}
			}
			stmt.executeUpdate("DELETE FROM \"Categories\" WHERE \"Id\"=" + categoryId);

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return true;
	}

	public int insertDataStorageRowCopy(DataStorageRow row) {
		int result = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"DataStorageRows\"(\"DataStorageId\", \"Order\") VALUES ("
					+ row.getStorageId() + ", " + row.getOrder() + ")");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorageRows\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				result = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void insertDataStorageValuesEmptyWithRowId(int rowId, List<DataStorageKey> keys) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataStorageKey key : keys) {
				DataStorageValue tempValue = new DataStorageValue(0);
				tempValue.setIsStorage(false);
				tempValue.setValue("");
				tempValue.setKeyId(key.getId());
				tempValue.setRowId(rowId);
				tempValue.setStorageIds(null);
				if (key.getReferenceStorageId() != 0) {
					tempValue.setIsStorage(true);
					tempValue.setValue("0");
				}

				stmt.executeUpdate("INSERT INTO \"DataStorageValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES ("
						+ tempValue.getRowId() + ", " + tempValue.getKeyId() + ", '" + tempValue.getValue() + "', "
						+ tempValue.isStorage() + ", " + tempValue.getStorageIdsAsString() + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void insertDataStorageValuesWithRowId(int rowId, List<DataStorageValue> values) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (DataStorageValue value : values) {
				stmt.executeUpdate("INSERT INTO \"DataStorageValues\""
						+ "(\"RowId\", \"KeyId\", \"Value\", \"IsStorage\", \"StorageRows\") " + "VALUES (" + rowId
						+ ", " + value.getKeyId() + ", '" + value.getValue() + "', " + value.isStorage() + ", "
						+ value.getStorageIdsAsString() + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updatePreconditionKeys(String[] keyIds, String[] keyValues) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keyIds.length; i++) {
				stmt.executeUpdate("UPDATE \"Preconditions\" SET \"Key\"='" + keyValues[i] + "' WHERE \"Id\"="
						+ keyIds[i]);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updatePostconditionKeys(String[] keyIds, String[] keyValues) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < keyIds.length; i++) {
				stmt.executeUpdate("UPDATE \"Postconditions\" SET \"Key\"='" + keyValues[i] + "' WHERE \"Id\"="
						+ keyIds[i]);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public ArrayList<PreconditionValue> getPreconditionValues(int[] ids) {
		ArrayList<PreconditionValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < ids.length; i++) {
				ResultSet rs = stmt.executeQuery("SELECT * FROM \"Preconditions\" WHERE \"Id\"=" + ids[i]);
				if (rs.next()) {
					PreconditionValue value = new PreconditionValue(rs.getInt("Id"));
					value.setDataFileid(rs.getInt("DataFileId"));
					value.setKey(rs.getString("Key"));
					value.setValue(rs.getString("Value"));
					value.setIsStorage(rs.getBoolean("IsStorage"));
					if (rs.getArray("StorageRows") == null) {
						value.setStorageIds(null);
					} else {
						value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
					}
					value.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
					result.add(value);
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public ArrayList<PostconditionValue> getPostconditionValues(int[] ids) {
		ArrayList<PostconditionValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < ids.length; i++) {
				ResultSet rs = stmt.executeQuery("SELECT * FROM \"Postconditions\" WHERE \"Id\"=" + ids[i]);
				if (rs.next()) {
					PostconditionValue value = new PostconditionValue(rs.getInt("Id"));
					value.setDataFileid(rs.getInt("DataFileId"));
					value.setKey(rs.getString("Key"));
					value.setValue(rs.getString("Value"));
					value.setIsStorage(rs.getBoolean("IsStorage"));
					if (rs.getArray("StorageRows") == null) {
						value.setStorageIds(null);
					} else {
						value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
					}
					value.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
					result.add(value);
				}
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public boolean deletePrecondition(int id) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"Preconditions\" WHERE \"Id\"=" + id);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"Preconditions\" WHERE \"Id\"=" + id);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public boolean deletePostcondition(int id) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"Postconditions\" WHERE \"Id\"=" + id);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"Postconditions\" WHERE \"Id\"=" + id);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<PreconditionValue> getPreconditionValuesByPreconditionId(String id) {
		ArrayList<PreconditionValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Preconditions\" WHERE \"DataFileId\"="
					+ "(SELECT \"DataFileId\" WHERE \"Id\"=" + id + ") ORDER BY \"Id\"");
			while (rs.next()) {
				PreconditionValue value = new PreconditionValue(rs.getInt("Id"));
				value.setDataFileid(rs.getInt("DataFileId"));
				value.setKey(rs.getString("Key"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				value.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<PostconditionValue> getPostconditionValuesByPostconditionId(String id) {
		ArrayList<PostconditionValue> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Postconditions\" WHERE \"DataFileId\"="
					+ "(SELECT \"DataFileId\" WHERE \"Id\"=" + id + ") ORDER BY \"Id\"");
			while (rs.next()) {
				PostconditionValue value = new PostconditionValue(rs.getInt("Id"));
				value.setDataFileid(rs.getInt("DataFileId"));
				value.setKey(rs.getString("Key"));
				value.setValue(rs.getString("Value"));
				value.setIsStorage(rs.getBoolean("IsStorage"));
				if (rs.getArray("StorageRows") == null) {
					value.setStorageIds(null);
				} else {
					value.setStorageIds((Integer[]) rs.getArray("StorageRows").getArray());
				}
				value.setReferenceStorageId(rs.getInt("ReferenceStorageId"));
				result.add(value);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataFile(DataFile dataFile) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataFiles\" " + "SET \"Name\"='" + dataFile.getName() + "', \"CategoryId\"="
					+ dataFile.getCategoryId() + ", \"HasPreconditions\"=" + dataFile.isHasPreconditions()
					+ ", \"HasPostconditions\"=" + dataFile.isHasPostconditions() + " WHERE \"Id\"=" + dataFile.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public boolean deleteStorageCategory(int categoryId) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorages\" WHERE \"CategoryId\"=" + categoryId);

			while (rs.next()) {
				if (!deleteStorage(rs.getInt("Id"))) {
					return false;
				}
			}
			stmt.executeUpdate("DELETE FROM \"StorageCategories\" WHERE \"Id\"=" + categoryId);

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return true;
	}

	public int insertUser(String userName, String hashPass, boolean isAdmin) {
		int id = 0;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO \"Users\"(\"UserName\", \"Password\", \"IsAdmin\") VALUES ('" + userName
					+ "', '" + hashPass + "', " + isAdmin + ")");
			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"Users\" ORDER BY \"Id\" DESC LIMIT 1");
			if (rs.next()) {
				id = rs.getInt("Id");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return id;
	}

	public List<User> getUsers() {
		List<User> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Users\"");

			while (rs.next()) {
				User user = new User(rs.getInt("Id"));
				user.setName(rs.getString("UserName"));
				user.setPassword(rs.getString("Password"));
				user.setAdmin(rs.getBoolean("IsAdmin"));
				List<UserPermission> permissions = getUserPermissions(rs.getInt("Id"));
				user.setPermissions(permissions);
				result.add(user);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	private List<UserPermission> getUserPermissions(int userId) {
		ArrayList<UserPermission> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"UserPermissions\" WHERE \"UserId\"=" + userId
					+ " ORDER BY \"Id\"");
			while (rs.next()) {
				UserPermission permission = new UserPermission(rs.getInt("Id"));
				permission.setUserId(userId);
				permission.setProducts(getProduct(rs.getInt("ProductId")));
				result.add(permission);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void insertUserPermissions(int userId, String[] productIds) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < productIds.length; i++) {
				stmt.executeUpdate("INSERT INTO \"UserPermissions\"(\"UserId\", \"ProductId\") VALUES (" + userId
						+ ", " + productIds[i] + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public User getUserByName(String userName) {
		User result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Users\" WHERE \"UserName\"='" + userName + "'");
			if (rs.next()) {
				result = new User(rs.getInt("Id"));
				result.setName(rs.getString("UserName"));
				result.setPassword(rs.getString("Password"));
				result.setAdmin(rs.getBoolean("IsAdmin"));
				List<UserPermission> permissions = getUserPermissions(rs.getInt("Id"));
				result.setPermissions(permissions);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataFile> getDataFilesUsingRow(int rowId) {
		List<DataFile> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataFiles\" WHERE " + "(\"Id\" IN "
					+ "(SELECT \"DataFileId\" FROM \"DataFileRows\" WHERE \"Id\" IN "
					+ "(SELECT \"RowId\" FROM \"DataFileValues\" WHERE " + rowId + " = ANY (\"StorageRows\")))) "
					+ "OR " + "(\"Id\" IN " + "(SELECT \"DataFileId\" FROM \"Preconditions\" WHERE " + rowId
					+ " = ANY (\"StorageRows\"))) " + "OR " + "(\"Id\" IN "
					+ "(SELECT \"DataFileId\" FROM \"Postconditions\" WHERE " + rowId + " = ANY (\"StorageRows\")))");

			while (rs.next()) {
				DataFile dataFIle = new DataFile(rs.getInt("Id"));
				dataFIle.setName(rs.getString("Name"));
				result.add(dataFIle);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public List<DataStorage> getDataStoragesUsingRow(int rowId) {
		List<DataStorage> result = new ArrayList<>();
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"DataStorages\" WHERE " + "\"Id\" IN "
					+ "(SELECT \"DataStorageId\" FROM \"DataStorageRows\" WHERE \"Id\" IN "
					+ "(SELECT \"RowId\" FROM \"DataStorageValues\" WHERE " + rowId + " = ANY (\"StorageRows\")))");

			while (rs.next()) {
				DataStorage dataStorage = new DataStorage(rs.getInt("Id"));
				dataStorage.setName(rs.getString("Name"));
				dataStorage.setCategoryId(rs.getInt("CategoryId"));
				dataStorage.setClassName(rs.getString("ClassName"));
				dataStorage.setShowUsage(rs.getBoolean("ShowUsage"));
				result.add(dataStorage);
			}

			conn.close();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataStorage(DataStorage storage) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"DataStorages\" " + "SET \"Name\"='" + storage.getName() + "', \"ClassName\"='"
					+ storage.getClassName() + "', \"CategoryId\"=" + storage.getCategoryId() + ", \"ShowUsage\"="
					+ storage.isShowUsage() + " WHERE \"Id\"=" + storage.getId());
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public boolean deleteDataStorageRow(int rowId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"DataStorageValues\" WHERE \"RowId\"=" + rowId);
			stmt.executeUpdate("DELETE FROM \"DataStorageRows\" WHERE \"Id\"=" + rowId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorageRows\" WHERE \"Id\"=" + rowId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public boolean deleteDataStorageKey(int keyId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"DataStorageValues\" WHERE \"KeyId\"=" + keyId);
			stmt.executeUpdate("DELETE FROM \"DataStorageKeys\" WHERE \"Id\"=" + keyId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"DataStorageKeys\" WHERE \"Id\"=" + keyId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateDataStorageKeysOrder(int[] modifiedKeyIds, int[] modifiedKeyNumbers) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			for (int i = 0; i < modifiedKeyIds.length; i++) {
				stmt.executeUpdate("UPDATE \"DataStorageKeys\" SET \"Order\"=" + modifiedKeyNumbers[i] + " "
						+ "WHERE \"Id\"=" + modifiedKeyIds[i]);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public boolean deleteUser(String userId) {
		boolean result = false;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"UserPermissions\" WHERE \"UserId\"=" + userId);
			stmt.executeUpdate("DELETE FROM \"Users\" WHERE \"Id\"=" + userId);

			ResultSet rs = stmt.executeQuery("SELECT \"Id\" FROM \"Users\" WHERE \"Id\"=" + userId);
			if (!rs.next()) {
				result = true;
			}

			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public User getUserById(int id) {
		User result = null;
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM \"Users\" WHERE \"Id\"=" + id);
			if (rs.next()) {
				result = new User(rs.getInt("Id"));
				result.setName(rs.getString("UserName"));
				result.setPassword(rs.getString("Password"));
				result.setAdmin(rs.getBoolean("IsAdmin"));
				List<UserPermission> permissions = getUserPermissions(rs.getInt("Id"));
				result.setPermissions(permissions);
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
		return result;
	}

	public void updateUserPermissions(int userId, String[] productIds) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM \"UserPermissions\" WHERE \"UserId\"=" + userId);
			for (int i = 0; i < productIds.length; i++) {
				stmt.executeUpdate("INSERT INTO \"UserPermissions\"(\"UserId\", \"ProductId\") VALUES (" + userId
						+ ", " + productIds[i] + ")");
			}
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateUserName(int id, String userName) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"Users\" " + "SET \"UserName\"='" + userName + "' WHERE \"Id\"=" + id);
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateUserPassword(int id, String hashPass) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"Users\" " + "SET \"Password\"='" + hashPass + "' WHERE \"Id\"=" + id);
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

	public void updateUserIsAdmin(int id, boolean isAdmin) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE \"Users\" " + "SET \"IsAdmin\"=" + isAdmin + " WHERE \"Id\"=" + id);
			conn.close();
			stmt.close();
		} catch (SQLException e) {
			printExeptionInformation(e);
		}
	}

}
