/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.pine.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pine.model.Category;
import org.pine.model.Key;
import org.pine.model.Product;
import org.pine.model.Row;
import org.pine.model.Table;
import org.pine.model.TableType;
import org.pine.model.User;
import org.pine.model.UserPermission;
import org.pine.model.Value;
import org.pine.settings.GlobalSettings;

/**
 * @author Maksym Barvinskyi
 */
public class Dao {

	private static Connection connection;

	private static Connection getConnection() throws SQLException {
		if (connection == null) {
			initializeSQLDriver();
			String sqlserver = null;
			String sqlport = null;
			String sqldatabase = null;
			String sqllogin = null;
			String sqlpassword = null;
			try {
				sqlserver = GlobalSettings.getInstance().getDbHost();
				sqlport = GlobalSettings.getInstance().getDbPort();
				sqldatabase = GlobalSettings.getInstance().getDbName();
				sqllogin = GlobalSettings.getInstance().getDbLogin();
				sqlpassword = GlobalSettings.getInstance().getDbPswd();
			} catch (Exception e) {
				e.printStackTrace();
			}
			connection = DriverManager.getConnection("jdbc:postgresql://" + sqlserver + ":" + sqlport + "/"
					+ sqldatabase, sqllogin, sqlpassword);
		}
		return connection;
	}

	private static void initializeSQLDriver() {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Unable to load driver.");
			e.printStackTrace();
		}
	}

	private static Product initProduct(ResultSet rs) throws SQLException {
		Product product = new Product(rs.getInt("id"));
		product.setName(rs.getString("name"));
		return product;
	}

	private static Category initCategory(ResultSet rs) throws SQLException {
		Category category = new Category(rs.getInt("id"));
		category.setName(rs.getString("name"));
		category.setProductId(rs.getInt("productid"));
		category.setParentId(rs.getInt("parentid"));
		category.setType(TableType.valueOf(rs.getString("type").toUpperCase()));
		return category;
	}

	private static Table initTable(ResultSet rs) throws SQLException {
		Table result = new Table(rs.getInt("id"));
		result.setType(TableType.valueOf(rs.getString("type").toUpperCase()));
		result.setName(rs.getString("name"));
		result.setCategoryId(rs.getInt("categoryid"));
		if (rs.getInt("parentid") == 0) {
			result.setParentId(null);
		} else {
			result.setParentId(rs.getInt("parentid"));
		}
		result.setClassName(rs.getString("className"));
		result.setShowUsage(rs.getBoolean("showusage"));
		result.setShowWarning(rs.getBoolean("showwarning"));
		result.setModifiedTime(rs.getTimestamp("modifiedtime"));
		return result;
	}

	private static User initUser(ResultSet rs) throws SQLException {
		User user = new User(rs.getInt("id"));
		user.setName(rs.getString("login"));
		user.setPassword(rs.getString("password"));
		user.setAdmin(rs.getBoolean("isadmin"));
		List<UserPermission> permissions = getUserPermissions(rs.getInt("id"));
		user.setPermissions(permissions);
		return user;
	}

	private static List<UserPermission> getUserPermissions(int userId) throws SQLException {
		ArrayList<UserPermission> result = new ArrayList<UserPermission>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM userpermissions WHERE userid=" + userId + " ORDER BY id");
		while (rs.next()) {
			UserPermission permission = new UserPermission(rs.getInt("id"));
			permission.setUserId(userId);
			permission.setProducts(getProduct(rs.getInt("productid")));
			permission.setHasWriteAccess(rs.getBoolean("writeaccess"));
			result.add(permission);
		}

		stmt.close();
		return result;
	}

	private static Key initKey(ResultSet rs) throws SQLException {
		Key result = new Key(rs.getInt("id"));
		result.setName(rs.getString("name"));
		result.setTableId(rs.getInt("tableid"));
		result.setOrder(rs.getInt("order"));
		result.setReferenceTableId(rs.getInt("reftable"));
		return result;
	}

	private static Row initRow(ResultSet rs) throws SQLException {
		Row result = new Row(rs.getInt("id"));
		result.setTableId(rs.getInt("tableid"));
		result.setOrder(rs.getInt("order"));
		return result;
	}

	private static Value initValue(ResultSet rs) throws SQLException {
		Value result = new Value(rs.getInt("id"));
		result.setKeyId(rs.getInt("keyid"));
		result.setRowId(rs.getInt("rowid"));
		result.setValue(rs.getString("value"));
		result.setIsStorage(rs.getBoolean("isstorage"));
		if (rs.getArray("storagerows") == null) {
			result.setStorageIds(null);
		} else {
			result.setStorageIds((Integer[]) rs.getArray("storagerows").getArray());
		}
		return result;
	}

	public static List<Product> getProducts() throws SQLException {
		List<Product> result = new ArrayList<Product>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY name");
		while (rs.next()) {
			result.add(initProduct(rs));
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static Product getProduct(int id) throws SQLException {
		Product result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE id=" + id);
		while (rs.next()) {
			result = initProduct(rs);
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static List<Category> getAllCategories(int productId, TableType type) throws SQLException {
		List<Category> result = new ArrayList<Category>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT c.id, c.name, c.productid, c.parentid, tt.name as type "
				+ " FROM categories as c JOIN tabletypes as tt ON c.type=tt.id AND c.productid=" + productId
				+ " AND c.type=" + type.getId() + " ORDER BY c.name");

		while (rs.next()) {
			result.add(initCategory(rs));
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static List<Category> getTopLevelCategories(int productId, TableType type) throws SQLException {
		List<Category> result = new ArrayList<Category>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT c.id, c.name, c.productid, c.parentid, tt.name as type "
				+ " FROM categories as c JOIN tabletypes as tt ON c.type=tt.id AND c.productid=" + productId
				+ " AND c.type=" + type.getId() + " AND c.parentid is null ORDER BY c.name");

		while (rs.next()) {
			result.add(initCategory(rs));
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static List<Category> getChildCategories(int categoryId) throws SQLException {
		List<Category> result = new ArrayList<Category>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT c.id, c.name, c.productid, c.parentid, tt.name as type "
				+ " FROM categories as c JOIN tabletypes as tt ON c.type=tt.id AND c.parentid=" + categoryId
				+ " ORDER BY c.name");

		while (rs.next()) {
			result.add(initCategory(rs));
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static int insertCategory(TableType type, int productId, String name, Integer parentId) throws SQLException {
		int id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO categories(type, productid, name, parentid) VALUES (" + type.getId() + ", "
				+ productId + ", '" + name + "', " + parentId + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM categories ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();
		return id;
	}

	public static int insertTable(String name, TableType type, Integer categoryId, Integer parentId, String className)
			throws SQLException {
		int id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalClassName = "null";
		if (className != null) {
			finalClassName = "'" + className + "'";
		}
		stmt.executeUpdate("INSERT INTO tables(name, type, categoryid, parentid, classname) VALUES ('" + name + "', "
				+ type.getId() + ", " + categoryId + ", " + parentId + ", " + finalClassName + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM tables ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();
		return id;
	}

	public static int insertRow(int tableId, int order) throws SQLException {
		int id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO rows(tableid, \"order\") VALUES (" + tableId + ", " + order + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM rows ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();
		return id;
	}

	public static int insertKey(int tableId, String name, int order, int referenceStorageId) throws SQLException {
		int id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalRefStorageId = (referenceStorageId == 0) ? "NULL" : String.valueOf(referenceStorageId);
		stmt.executeUpdate("INSERT INTO keys(tableid, name, \"order\", reftable) VALUES (" + tableId + ", '" + name
				+ "', " + order + ", " + finalRefStorageId + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM keys ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();
		return id;
	}

	public static void insertValue(int rowId, int keyId, String value, boolean isStorage, String storageIdsAsString)
			throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		value = value.replace("'", "''");
		stmt.executeUpdate("INSERT INTO values(rowid, keyid, value, isstorage, storagerows) VALUES (" + rowId + ", "
				+ keyId + ", '" + value + "', " + isStorage + ", " + storageIdsAsString + ")");

		stmt.close();
	}

	public static boolean deleteUser(String userId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM userpermissions WHERE userid=" + userId);
		stmt.executeUpdate("DELETE FROM users WHERE id=" + userId);

		ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE id=" + userId);
		if (!rs.next()) {
			result = true;
		}

		stmt.close();
		return result;
	}

	public static User getUserById(int id) throws SQLException {
		User result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id=" + id);
		if (rs.next()) {
			result = initUser(rs);
		}

		stmt.close();
		return result;
	}

	public static void updateUserPermissions(int userId, String[] productIds) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM userpermissions WHERE userid=" + userId);
		for (int i = 0; i < productIds.length; i++) {
			stmt.executeUpdate("INSERT INTO userpermissions(userid, productid) VALUES (" + userId + ", "
					+ productIds[i] + ")");
		}

		stmt.close();
	}

	public static void updateUserName(int id, String userName) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users " + "SET login='" + userName + "' WHERE id=" + id);

		stmt.close();
	}

	public static void updateUserPassword(int id, String hashPass) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users " + "SET password='" + hashPass + "' WHERE id=" + id);

		stmt.close();
	}

	public static void updateUserIsAdmin(int id, boolean isAdmin) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users " + "SET isadmin=" + isAdmin + " WHERE id=" + id);

		stmt.close();
	}

	public static int insertUser(String userName, String hashPass, boolean isAdmin) throws SQLException {
		int id = 0;

		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO users(login, password, isadmin) VALUES ('" + userName + "', '"
				+ hashPass.replace("'", "''") + "', " + isAdmin + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM users ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();

		return id;
	}

	public static List<User> getUsers() throws SQLException {
		List<User> result = new ArrayList<User>();

		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users");

		while (rs.next()) {
			result.add(initUser(rs));
		}

		rs.close();
		stmt.close();

		return result;
	}

	public static void insertUserPermissions(int userId, String[] productIds) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < productIds.length; i++) {
			stmt.executeUpdate("INSERT INTO userpermissions(userid, productid) VALUES (" + userId + ", "
					+ productIds[i] + ")");
		}

		stmt.close();

	}

	public static User getUserByName(String userName) throws SQLException {
		User result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE login='" + userName + "'");
		if (rs.next()) {
			result = initUser(rs);
		}

		stmt.close();
		return result;
	}

	public static Table getTable(int id) throws SQLException {
		Table result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
				+ "t.classname, t.showusage, t.showwarning, t.modifiedtime, tt.name as type "
				+ "FROM tables as t JOIN tabletypes as tt ON t.type=tt.id AND t.id=" + id);

		if (rs.next()) {
			result = initTable(rs);
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static Table getTable(String name, Integer categoryId) throws SQLException {
		Table result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, t.classname, "
						+ "t.showusage, t.showwarning, t.modifiedtime, tt.name as type "
						+ "FROM tables as t JOIN tabletypes as tt ON t.type=tt.id AND t.name='"
						+ name
						+ "' AND t.categoryid IN (SELECT id FROM categories WHERE productid=(SELECT productid FROM categories WHERE id="
						+ categoryId + "))");
		if (rs.next()) {
			result = initTable(rs);
		}

		stmt.close();
		return result;
	}

	/**
	 * Method for 'tables' or 'storages'.
	 */
	public static int getProductIdByPrimaryTableId(int tableId) throws SQLException {
		int productId = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT productid FROM categories WHERE id="
				+ "(SELECT categoryid FROM tables WHERE id=" + tableId + ")");
		if (rs.next()) {
			productId = rs.getInt("productid");
		}

		stmt.close();
		return productId;
	}

	/**
	 * Method for 'preconditions' or 'postconditions'.
	 */
	public static int getProductIdBySecondaryTableId(int tableId) throws SQLException {
		int productId = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT productid FROM categories WHERE id="
				+ "(SELECT categoryid FROM tables WHERE id=(SELECT parentid FROM tables WHERE id=" + tableId + "))");
		if (rs.next()) {
			productId = rs.getInt("productid");
		}

		stmt.close();
		return productId;
	}

	public static List<Table> getTablesByCategoryId(int categoryId) throws SQLException {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showusage, t.showwarning, t.modifiedtime, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id AND t.categoryid=" + categoryId + " ORDER BY t.name");
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	public static List<Key> getKeys(int tableId) throws SQLException {
		ArrayList<Key> result = new ArrayList<Key>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM keys WHERE tableid=" + tableId + " ORDER BY \"order\"");
		while (rs.next()) {
			result.add(initKey(rs));
		}

		stmt.close();
		return result;
	}

	public static List<Row> getRows(int tableId) throws SQLException {
		ArrayList<Row> result = new ArrayList<Row>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rows WHERE tableid=" + tableId + " ORDER BY \"order\"");
		while (rs.next()) {
			result.add(initRow(rs));
		}

		stmt.close();
		return result;
	}

	public static ArrayList<Value> getValues(Row row) throws SQLException {
		ArrayList<Value> result = new ArrayList<Value>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT v.id, v.keyid, v.rowid, v.value, v.isstorage, v.storagerows, "
				+ "k.\"order\" FROM values as v JOIN keys as k ON v.keyid=k.id AND v.rowid=" + row.getId()
				+ " ORDER BY k.\"order\"");
		while (rs.next()) {
			result.add(initValue(rs));
		}

		stmt.close();
		return result;
	}

	public static List<Table> getTablesUsingRow(int rowId) throws SQLException {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showusage, t.showwarning, t.modifiedtime, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id AND t.id IN (SELECT tableid FROM rows WHERE id IN "
						+ "(SELECT rowid FROM values WHERE " + rowId + " = ANY (storagerows))) ORDER BY t.id");
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	public static Integer getChildtable(int tableId, TableType childType) throws SQLException {
		Integer result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM tables WHERE parentid=" + tableId
				+ " AND type=(SELECT id FROM tabletypes WHERE name='" + childType.toString().toLowerCase() + "')");
		if (rs.next()) {
			result = rs.getInt("id");
		}

		stmt.close();
		return result;
	}

	public static Category getCategory(int id) throws SQLException {
		Category result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT c.id, c.name, c.productid, c.parentid, tt.name as type "
				+ " FROM categories as c JOIN tabletypes as tt ON c.type=tt.id AND c.id=" + id);
		if (rs.next()) {
			result = initCategory(rs);
		}

		stmt.close();
		return result;
	}

	public static List<Value> getValues(Key key) throws SQLException {
		List<Value> result = new ArrayList<Value>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT v.id, v.keyid, v.rowid, v.value, v.isstorage, v.storagerows, "
				+ "r.\"order\" FROM values as v JOIN rows as r ON v.rowid=r.id AND v.keyid=" + key.getId()
				+ " ORDER BY r.\"order\"");
		while (rs.next()) {
			result.add(initValue(rs));
		}

		stmt.close();
		return result;
	}

	public static Row getRow(int id) throws SQLException {
		Row result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rows WHERE id=" + id);
		if (rs.next()) {
			result = initRow(rs);
		}

		stmt.close();
		return result;
	}

	public static Value getValue(int id) throws SQLException {
		Value result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM values WHERE id=" + id);
		if (rs.next()) {
			result = initValue(rs);
		}

		stmt.close();
		return result;
	}

	public static Key getKey(int id) throws SQLException {
		Key result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM keys WHERE id=" + id);
		if (rs.next()) {
			result = initKey(rs);
		}

		stmt.close();
		return result;
	}

	public static List<Table> getRefTablesOfProductByKeyId(int keyId, TableType type) throws SQLException {
		List<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		Integer categoryId = 0;
		ResultSet rs = stmt.executeQuery("SELECT categoryid FROM tables WHERE id="
				+ "(SELECT tableid FROM keys WHERE id=" + keyId + ")");
		if (rs.next()) {
			categoryId = rs.getInt("categoryid");
		}
		if (categoryId.equals(0)) {
			ResultSet rs2 = stmt.executeQuery("SELECT categoryid FROM tables WHERE id="
					+ "(SELECT parentid FROM tables WHERE id=" + "(SELECT tableid FROM keys WHERE id=" + keyId + "))");
			if (rs2.next()) {
				categoryId = rs2.getInt("categoryid");
			}
		}
		ResultSet rs3 = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showusage, t.showwarning, t.modifiedtime, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id AND tt.name='" + type.toString().toLowerCase()
						+ "' AND t.categoryid IN (SELECT id FROM categories WHERE productid="
						+ "(SELECT productid FROM categories WHERE id=" + categoryId + ")) ORDER BY t.name");
		while (rs3.next()) {
			result.add(initTable(rs3));
		}

		stmt.close();
		return result;

	}

	public static Integer getCategoryId(String name, int productId, TableType type, Integer parentId)
			throws SQLException {
		Integer result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String parent = " is null";
		if (parentId != null) {
			parent = "=" + parentId;
		}
		ResultSet rs = stmt.executeQuery("SELECT id FROM categories WHERE productid=" + productId + " AND name='"
				+ name + "' AND type=(SELECT id FROM tabletypes WHERE name='" + type.toString().toLowerCase()
				+ "') AND parentid" + parent);
		if (rs.next()) {
			result = rs.getInt("id");
		}

		stmt.close();
		return result;
	}

	public static List<Integer> insertKeys(int tableId, List<String> keyNames) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < keyNames.size(); i++) {
			stmt.executeUpdate("INSERT INTO keys" + "(tableid, name, \"order\", reftable) VALUES (" + tableId + ", '"
					+ keyNames.get(i) + "', " + (i + 1) + ", NULL)");
			ResultSet rs = stmt.executeQuery("SELECT id FROM keys ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	public static List<Integer> insertValuesEmptyWithKeyId(int keyId, List<Row> rows) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Row row : rows) {
			stmt.executeUpdate("INSERT INTO values(rowid, keyid, value, isstorage, storagerows) VALUES (" + row.getId()
					+ ", " + keyId + ", '', false, null)");
			ResultSet rs = stmt.executeQuery("SELECT id FROM values ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	public static void updateKeys(List<Integer> keyIds, List<Integer> keyNumbers) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < keyIds.size(); i++) {
			stmt.executeUpdate("UPDATE keys SET \"order\"=" + keyNumbers.get(i) + " " + "WHERE id=" + keyIds.get(i));
		}

		stmt.close();
	}

	public static int insertKeyCopy(Key currentKey) throws SQLException {
		int result = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalRefStorageId = (currentKey.getReferenceTableId() == 0) ? "NULL" : String.valueOf(currentKey
				.getReferenceTableId());
		stmt.executeUpdate("INSERT INTO keys(tableid, name, \"order\", reftable) VALUES (" + currentKey.getTableId()
				+ ", '" + currentKey.getName() + "'," + currentKey.getOrder() + ", " + finalRefStorageId + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM keys ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			result = rs.getInt("id");
		}

		stmt.close();
		return result;
	}

	public static List<Integer> insertValuesWithKeyId(int newKeyId, List<Value> values) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Value value : values) {
			stmt.executeUpdate("INSERT INTO values" + "(rowid, keyid, value, isstorage, storagerows) " + "VALUES ("
					+ value.getRowId() + ", " + newKeyId + ", '" + escape(value.getValue()) + "', " + value.isStorage()
					+ ", " + value.getStorageIdsAsString() + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM values ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	/**
	 * Adds escaping symbols to the value, so that it could be properly inserted to the database.
	 * 
	 * @param value
	 * @return value that is ready for DB inserting.
	 */
	private static String escape(String value) {
		return value.replace("'", "''");
	}

	public static void updateRows(List<Integer> rowIds, List<Integer> oldRowNumbers, List<Integer> modifiedRowNumbers)
			throws SQLException {
		List<Value> values = new ArrayList<Value>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < rowIds.size(); i++) {
			stmt.executeUpdate("UPDATE rows SET \"order\"=" + modifiedRowNumbers.get(i) + " " + "WHERE id="
					+ rowIds.get(i));

			ResultSet rs = stmt.executeQuery("SELECT * FROM values WHERE " + rowIds.get(i) + " = ANY (storagerows)");
			while (rs.next()) {
				values.add(initValue(rs));
			}
			rs.close();
		}

		for (Value value : values) {
			String oldValueRows[] = value.getValue().split(";");
			String newValueRows[] = oldValueRows.clone();
			for (int i = 0; i < oldValueRows.length; i++) {
				for (int j = 0; j < oldRowNumbers.size(); j++) {
					if (oldValueRows[i].equals(String.valueOf(oldRowNumbers.get(j)))) {
						newValueRows[i] = String.valueOf(modifiedRowNumbers.get(j));
					}
				}
			}
			String newValue = StringUtils.join(newValueRows, ";");
			value.setValue(newValue);
			updateValue(value);
		}

		stmt.close();
	}

	public static void updateRows(List<Integer> rowIds, List<Integer> rowNumbers) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < rowIds.size(); i++) {
			stmt.executeUpdate("UPDATE rows SET \"order\"=" + rowNumbers.get(i) + " " + "WHERE id=" + rowIds.get(i));
		}

		stmt.close();
	}

	public static int insertRowCopy(Row currentRow) throws SQLException {
		int result = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO rows(tableid, \"order\") VALUES (" + currentRow.getTableId() + ", "
				+ currentRow.getOrder() + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM rows ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			result = rs.getInt("id");
		}

		stmt.close();
		return result;
	}

	public static List<Integer> insertValuesWithRowId(int newRowId, List<Value> values) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Value value : values) {
			stmt.executeUpdate("INSERT INTO values" + "(rowid, keyid, value, isstorage, storagerows) " + "VALUES ("
					+ newRowId + ", " + value.getKeyId() + ", '" + escape(value.getValue()) + "', " + value.isStorage()
					+ ", " + value.getStorageIdsAsString() + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM values ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	public static List<Integer> insertValuesEmptyWithRowId(int rowId, List<Key> keys) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Key key : keys) {
			Value value = new Value(0);
			value.setIsStorage(false);
			value.setValue("");
			value.setKeyId(key.getId());
			value.setRowId(rowId);
			value.setStorageIds(null);
			if (key.getReferenceTableId() != 0) {
				Table refTable = getTable(key.getReferenceTableId());
				if (refTable.getType() == TableType.STORAGE) {
					value.setIsStorage(true);
					value.setValue("0");
				} else if (refTable.getType() == TableType.ENUMERATION) {
					String firstValue = getValues(getKeys(refTable.getId()).get(0)).get(0).getValue();
					value.setValue(firstValue);
				}
			}

			stmt.executeUpdate("INSERT INTO values(rowid, keyid, value, isstorage, storagerows) " + "VALUES ("
					+ value.getRowId() + ", " + value.getKeyId() + ", '" + escape(value.getValue()) + "', "
					+ value.isStorage() + ", " + value.getStorageIdsAsString() + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM values ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	public static boolean deleteCategory(int categoryId) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM categories WHERE id=" + categoryId);
		boolean result = false;
		ResultSet rs = stmt.executeQuery("SELECT id FROM categories WHERE id=" + categoryId);
		if (!rs.next()) {
			result = true;
		}
		rs.close();
		rs.close();
		stmt.close();
		return result;
	}

	public static List<Table> getTablesUsingStorage(int storageId) throws SQLException {
		List<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showusage, t.showwarning, t.modifiedtime, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id AND t.id IN (SELECT tableid FROM keys WHERE reftable=" + storageId
						+ ") ORDER BY t.id");
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	public static boolean deleteTable(int tableId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT id FROM tables WHERE parentid=" + tableId);
		while (rs.next()) {
			deleteTable(rs.getInt("id"));
		}
		rs.close();

		stmt.executeUpdate("DELETE FROM values WHERE rowid IN (SELECT id FROM rows WHERE tableid=" + tableId + ")");
		stmt.executeUpdate("DELETE FROM keys WHERE tableid=" + tableId);
		stmt.executeUpdate("DELETE FROM rows WHERE tableid=" + tableId);
		stmt.executeUpdate("DELETE FROM tables WHERE id=" + tableId);

		ResultSet rs2 = stmt.executeQuery("SELECT id FROM tables WHERE id=" + tableId);
		if (!rs2.next()) {
			result = true;
		}
		rs2.close();

		stmt.close();
		return result;
	}

	public static boolean deleteKey(int keyId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM values WHERE keyid=" + keyId);
		stmt.executeUpdate("DELETE FROM keys WHERE id=" + keyId);

		ResultSet rs = stmt.executeQuery("SELECT id FROM keys WHERE id=" + keyId);
		if (!rs.next()) {
			result = true;
		}

		stmt.close();
		return result;
	}

	public static boolean deleteRow(int rowId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM values WHERE rowid=" + rowId);
		stmt.executeUpdate("DELETE FROM rows WHERE id=" + rowId);

		ResultSet rs = stmt.executeQuery("SELECT id FROM rows WHERE id=" + rowId);
		if (!rs.next()) {
			result = true;
		}

		stmt.close();
		return result;
	}

	public static List<Integer> insertRows(int tableId, int rowsNumber) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < rowsNumber; i++) {
			stmt.executeUpdate("INSERT INTO rows(tableid, \"order\") VALUES (" + tableId + ", " + (i + 1) + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM rows ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	public static List<Integer> addRows(int tableId, int currRowsNumber, int rowsNumberToAdd) throws SQLException {
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = currRowsNumber; i < currRowsNumber + rowsNumberToAdd; i++) {
			stmt.executeUpdate("INSERT INTO rows(tableid, \"order\") VALUES (" + tableId + ", " + (i + 1) + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM rows ORDER BY id DESC LIMIT 1");
			if (rs.next()) {
				result.add(rs.getInt("id"));
			}
		}

		stmt.close();
		return result;
	}

	public static void insertValues(List<Integer> rowIds, List<Integer> keyIds, ArrayList<ArrayList<String>> values)
			throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < rowIds.size(); i++) {
			int rowId = rowIds.get(i);
			ArrayList<String> rowValues = values.get(i);
			for (int j = 0; j < keyIds.size(); j++) {
				int keyId = keyIds.get(j);
				String value = rowValues.get(j).replace("'", "''");
				stmt.executeUpdate("INSERT INTO values" + "(rowid, keyid, value, isstorage, storagerows) " + "VALUES ("
						+ rowId + ", " + keyId + ", '" + value + "', false, NULL)");
			}
		}

		stmt.close();
	}

	public static void updateTable(Table table) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalName = table.getName();
		if (finalName != null) {
			finalName = "'" + finalName + "'";
		}
		stmt.executeUpdate("UPDATE tables SET name=" + finalName + ", type=" + table.getType().getId()
				+ ", classname='" + table.getClassName() + "', categoryid=" + table.getCategoryId() + ", parentid="
				+ table.getParentId() + ", showusage=" + table.isShowUsage() + ", showwarning=" + table.isShowWarning()
				+ ", modifiedtime='" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(table.getModifiedTime())
				+ "' WHERE id=" + table.getId());

		stmt.close();
	}

	public static void updateCategory(Category category) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE categories SET name='" + category.getName() + "' WHERE id=" + category.getId());

		stmt.close();
	}

	public static void updateKeys(String[] keyIds, String[] keyValues) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < keyIds.length; i++) {
			stmt.executeUpdate("UPDATE keys SET name='" + keyValues[i] + "' " + "WHERE id=" + keyIds[i]);
		}

		stmt.close();
	}

	public static void updateKeyValue(String id, String value) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE keys SET name='" + value.replace("'", "''") + "' " + "WHERE id=" + id);

		stmt.close();
	}

	public static int getRefStorageId(int keyId) throws SQLException {
		int result = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT reftable FROM keys WHERE id=" + keyId);
		if (rs.next()) {
			result = rs.getInt("reftable");
		}

		stmt.close();
		return result;
	}

	public static Row getRow(int refStorageId, int order) throws SQLException {
		Row result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT * FROM rows WHERE tableid=" + refStorageId + " AND \"order\"=" + order);
		if (rs.next()) {
			result = initRow(rs);
		}

		stmt.close();
		return result;
	}

	public static void updateValues(ArrayList<Value> values) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Value value : values) {
			stmt.executeUpdate("UPDATE values SET rowid=" + value.getRowId() + ", keyid=" + value.getKeyId()
					+ ", value='" + escape(value.getValue()) + "', isstorage=" + value.isStorage() + ", storagerows="
					+ value.getStorageIdsAsString() + " " + "WHERE id=" + value.getId());
		}

		stmt.close();
	}

	public static void updateKey(Key key) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalRefTableId = "null";
		if (key.getReferenceTableId() != 0) {
			finalRefTableId = String.valueOf(key.getReferenceTableId());
		}
		stmt.executeUpdate("UPDATE keys SET name='" + key.getName() + "', " + "\"order\"=" + key.getOrder() + ", "
				+ "reftable=" + finalRefTableId + " " + "WHERE id=" + key.getId());

		stmt.close();
	}

	public static void updateValuesTypes(int keyId, boolean isStorage, String storageIds) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE values SET isstorage=" + isStorage + ", " + "storagerows=" + storageIds + " "
				+ "WHERE keyid=" + keyId);

		stmt.close();
	}

	public static void updateValue(Value value) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE values " + "SET rowid=" + value.getRowId() + ", keyid=" + value.getKeyId()
				+ ", value='" + escape(value.getValue()) + "', isstorage=" + value.isStorage() + ", " + "storagerows="
				+ value.getStorageIdsAsString() + " " + "WHERE id=" + value.getId());

		stmt.close();
	}

	public static boolean isLastAdmin(String userId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT isadmin FROM users WHERE id=" + userId);
		if (rs.next()) {
			if (rs.getBoolean("isadmin")) {
				ResultSet rs2 = stmt.executeQuery("SELECT count(id) FROM users WHERE isadmin=true");
				if (rs2.next()) {
					if (rs2.getInt(1) == 1) {
						result = true;
					}
				}
			}
		}

		stmt.close();
		return result;
	}

	public static Product getProduct(String name) throws SQLException {
		Product result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE name='" + name + "'");
		while (rs.next()) {
			result = initProduct(rs);
		}

		rs.close();
		stmt.close();
		return result;
	}

	public static int insertProduct(String name) throws SQLException {
		int id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO products(name) VALUES ('" + name + "')");
		ResultSet rs = stmt.executeQuery("SELECT id FROM categories ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();
		return id;
	}

	public static int getAdminsCount() throws SQLException {
		int result = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT count(id) FROM users WHERE isadmin=true");
		if (rs.next()) {
			result = rs.getInt(1);
		}

		stmt.close();
		return result;
	}

	public static void execute(String query) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);

		stmt.close();
	}

	public static ResultSet executeSelect(String query) throws SQLException {
		ResultSet rs = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		rs = stmt.executeQuery(query);

		stmt.close();
		return rs;
	}

	public static boolean deleteProduct(int productId) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		try {
			stmt.executeUpdate("ALTER TABLE ONLY keys DROP CONSTRAINT keys_reftable_fkey");
		} catch (Exception e) {
			if (!e.getMessage().contains("constraint \"keys_reftable_fkey\" of relation \"keys\" does not exist")) {
				throw e;
			}
		}

		ResultSet rs = stmt.executeQuery("SELECT id FROM categories WHERE productid=" + productId);

		while (rs.next()) {

			if (!deleteCategory(rs.getInt("id"))) {

				rs.close();
				stmt.close();
				return false;
			}
		}
		stmt.executeUpdate("DELETE FROM userpermissions WHERE productid=" + productId);
		stmt.executeUpdate("DELETE FROM products WHERE id=" + productId);
		stmt.executeUpdate("ALTER TABLE ONLY keys ADD CONSTRAINT keys_reftable_fkey FOREIGN KEY (reftable) REFERENCES tables(id)");

		rs.close();
		stmt.close();
		return true;
	}

	public static void updateProduct(Product product) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE products SET name='" + product.getName() + "' WHERE id=" + product.getId());

		stmt.close();
	}

	public static List<Table> getTablesOfProduct(int productId, TableType type) throws SQLException {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showusage, t.showwarning, t.modifiedtime, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id AND t.categoryid IN (SELECT id FROM categories WHERE productid=" + productId
						+ " AND type=(SELECT id FROM tabletypes WHERE name='" + type.toString().toLowerCase()
						+ "')) ORDER BY t.name");
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	public static boolean isTableInProductExist(String name, TableType type, Integer categoryId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM tables WHERE categoryid IN "
				+ "(SELECT id FROM categories WHERE productid=(SELECT productid FROM categories WHERE id=" + categoryId
				+ ")) AND type=(SELECT id FROM tabletypes WHERE name='" + type.toString().toLowerCase()
				+ "') AND name='" + name + "'");
		if (rs.next()) {
			result = true;
		}

		stmt.close();
		return result;
	}

	public static List<Key> insertKeysFromOneTableToAnother(int copyTableId, int tableId) throws SQLException {
		List<Key> result = new ArrayList<Key>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO keys (tableid, name, \"order\", reftable) " + "SELECT " + tableId
				+ ", name, \"order\", reftable FROM keys WHERE tableid=" + copyTableId);

		ResultSet rs = stmt.executeQuery("SELECT * FROM keys WHERE tableid=" + tableId);
		while (rs.next()) {
			result.add(initKey(rs));
		}

		stmt.close();
		return result;
	}

	public static void insertValues(int tableId, int oldTableId, List<Row> oldRows, List<Key> keys) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (Row oldRow : oldRows) {
			stmt.executeUpdate("INSERT INTO rows (tableid, \"order\") VALUES (" + tableId + ", " + oldRow.getOrder()
					+ ")");
			for (Key key : keys) {
				stmt.executeUpdate("INSERT INTO values (rowid, keyid, value, isstorage, storagerows) "
						+ "SELECT (SELECT id FROM rows WHERE tableid=" + tableId + " AND \"order\"="
						+ oldRow.getOrder() + "), " + key.getId()
						+ ", value, isstorage, storagerows FROM values WHERE rowid=" + oldRow.getId() + " AND keyid="
						+ "(SELECT id FROM keys WHERE tableid=" + oldTableId + " AND \"order\"=" + key.getOrder() + ")");
			}
		}
		stmt.close();
	}

	public static boolean isTableTypeExist(String name) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM tabletypes WHERE name ='" + name.toLowerCase() + "'");
		if (rs.next()) {
			result = true;
		}
		stmt.close();
		return result;
	}

	public static List<Value> getValuesByEnumValue(Value enumValue, String oldValue) throws SQLException {
		int enumId = Dao.getTable(Dao.getKey(enumValue.getKeyId()).getTableId()).getId();
		List<Value> result = new ArrayList<Value>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM values WHERE value='" + oldValue
				+ "' AND keyid IN (SELECT id FROM keys WHERE reftable=" + enumId + ")");
		while (rs.next()) {
			result.add(initValue(rs));
		}
		stmt.close();
		return result;
	}

	public static boolean columnExist(String table, String column) {
		try {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeQuery("SELECT " + column + " FROM " + table + " LIMIT 1");
			stmt.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
}