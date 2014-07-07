/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.grible.dbmigrate.oldmodel.OldKey;
import org.grible.dbmigrate.oldmodel.OldRow;
import org.grible.dbmigrate.oldmodel.OldValue;
import org.grible.model.Category;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.User;
import org.grible.model.UserPermission;
import org.grible.model.json.Key;
import org.grible.settings.GlobalSettings;

import com.google.gson.Gson;

/**
 * @author Maksym Barvinskyi
 */
public class PostgresDao implements Dao {

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

	private Product initProduct(ResultSet rs) throws SQLException {
		Product product = new Product(rs.getInt("id"));
		product.setName(rs.getString("name"));
		return product;
	}

	private Category initCategory(ResultSet rs) throws SQLException {
		Category category = new Category(rs.getInt("id"));
		category.setName(rs.getString("name"));
		category.setProductId(rs.getInt("productid"));
		category.setParentId(rs.getInt("parentid"));
		category.setType(TableType.valueOf(rs.getString("type").toUpperCase()));
		return category;
	}

	private Table initTable(ResultSet rs) throws Exception {
		Table result = new Table(rs.getInt("id"));
		result.setType(TableType.valueOf(rs.getString("type").toUpperCase()));
		result.setName(rs.getString("name"));
		if (rs.getInt("categoryid") == 0) {
			result.setCategoryId(null);
		} else {
			result.setCategoryId(rs.getInt("categoryid"));
		}
		if (rs.getInt("parentid") == 0) {
			result.setParentId(null);
		} else {
			result.setParentId(rs.getInt("parentid"));
		}
		result.setClassName(rs.getString("className"));
		result.setShowWarning(rs.getBoolean("showwarning"));
		result.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("modifiedtime")));
		Gson gson = new Gson();
		result.setKeys(gson.fromJson(rs.getString("keys"), Key[].class));
		result.setValues(gson.fromJson(rs.getString("values"), String[][].class));
		return result;
	}

	private User initUser(ResultSet rs) throws SQLException {
		User user = new User(rs.getInt("id"));
		user.setName(rs.getString("login"));
		user.setPassword(rs.getString("password"));
		user.setAdmin(rs.getBoolean("isadmin"));
		List<UserPermission> permissions = getUserPermissions(rs.getInt("id"));
		user.setPermissions(permissions);
		user.setTooltipOnClick(rs.getBoolean("tooltiponclick"));
		return user;
	}

	private List<UserPermission> getUserPermissions(int userId) throws SQLException {
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

	private OldKey initKey(ResultSet rs) throws SQLException {
		OldKey result = new OldKey(rs.getInt("id"));
		result.setName(rs.getString("name"));
		result.setTableId(rs.getInt("tableid"));
		result.setOrder(rs.getInt("order"));
		result.setReferenceTableId(rs.getInt("reftable"));
		return result;
	}

	private OldRow initRow(ResultSet rs) throws SQLException {
		OldRow result = new OldRow(rs.getInt("id"));
		result.setTableId(rs.getInt("tableid"));
		result.setOrder(rs.getInt("order"));
		return result;
	}

	private OldValue initValue(ResultSet rs) throws SQLException {
		OldValue result = new OldValue(rs.getInt("id"));
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

	public List<Product> getProducts() throws SQLException {
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

	public Product getProduct(int id) throws SQLException {
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

	public String getCurrentDbVersion() throws SQLException {
		String result = "";
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT tablename FROM pg_tables WHERE schemaname='public'");
		boolean isVersionTableExist = false;
		while (rs.next()) {
			if (rs.getString("tablename").equals("version")) {
				isVersionTableExist = true;
				break;
			}
		}
		rs.close();
		if (!isVersionTableExist) {
			result = "0.8.x";
		} else {
			ResultSet rs2 = stmt.executeQuery("SELECT dbversion FROM version");
			if (rs2.next()) {
				result = rs2.getString("dbversion");
			}
			rs2.close();
		}
		stmt.close();
		return result;
	}

	public List<Category> getAllCategories(int productId, TableType type) throws SQLException {
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

	public List<Category> getTopLevelCategories(int productId, TableType type) throws SQLException {
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

	public List<Category> getChildCategories(Category category) throws SQLException {
		List<Category> result = new ArrayList<Category>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT c.id, c.name, c.productid, c.parentid, tt.name as type "
				+ " FROM categories as c JOIN tabletypes as tt ON c.type=tt.id AND c.parentid=" + category.getId()
				+ " ORDER BY c.name");

		while (rs.next()) {
			result.add(initCategory(rs));
		}

		rs.close();
		stmt.close();
		return result;
	}

	public int insertCategory(TableType type, int productId, String name, Integer parentId, String parentPath)
			throws SQLException {
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

	public int insertTable(String name, TableType type, Category category, Integer parentId, String className,
			Key[] keys, String[][] values) throws SQLException {
		int id = 0;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalClassName = "null";
		if (className != null) {
			finalClassName = "'" + className + "'";
		}
		String categoryId = "null";
		if (category != null) {
			categoryId = String.valueOf(category.getId());
		}
		Gson gson = new Gson();
		stmt.executeUpdate("INSERT INTO tables(name, type, categoryid, parentid, classname, keys, values) VALUES ('"
				+ name + "', " + type.getId() + ", " + categoryId + ", " + parentId + ", " + finalClassName + ", '"
				+ gson.toJson(keys) + "', '" + gson.toJson(escape(values)) + "')");
		ResultSet rs = stmt.executeQuery("SELECT id FROM tables ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();
		return id;
	}

	public boolean deleteUser(String userId) throws SQLException {
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

	public User getUserById(int id) throws SQLException {
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

	public void updateUserPermissions(int userId, String[] productIds) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM userpermissions WHERE userid=" + userId);
		for (int i = 0; i < productIds.length; i++) {
			stmt.executeUpdate("INSERT INTO userpermissions(userid, productid) VALUES (" + userId + ", "
					+ productIds[i] + ")");
		}

		stmt.close();
	}

	public void updateUserName(int id, String userName) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET login='" + userName + "' WHERE id=" + id);

		stmt.close();
	}

	public void updateUserPassword(int id, String hashPass) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET password='" + hashPass + "' WHERE id=" + id);

		stmt.close();
	}

	public void updateUserIsAdmin(int id, boolean isAdmin) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET isadmin=" + isAdmin + " WHERE id=" + id);

		stmt.close();
	}

	public void updateUserIsTooltipOnClick(int id, boolean isTooltipOnClick) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET tooltiponclick=" + isTooltipOnClick + " WHERE id=" + id);

		stmt.close();
	}

	public int insertUser(String userName, String hashPass, boolean isAdmin, boolean isTooltipOnClick)
			throws SQLException {
		int id = 0;

		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO users(login, password, isadmin, tooltiponclick) VALUES ('" + userName + "', '"
				+ hashPass.replace("'", "''") + "', " + isAdmin + ", " + isTooltipOnClick + ")");
		ResultSet rs = stmt.executeQuery("SELECT id FROM users ORDER BY id DESC LIMIT 1");
		if (rs.next()) {
			id = rs.getInt("id");
		}

		stmt.close();

		return id;
	}

	public List<User> getUsers() throws SQLException {
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

	public void insertUserPermissions(int userId, String[] productIds) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		for (int i = 0; i < productIds.length; i++) {
			stmt.executeUpdate("INSERT INTO userpermissions(userid, productid) VALUES (" + userId + ", "
					+ productIds[i] + ")");
		}

		stmt.close();

	}

	public User getUserByName(String userName) throws SQLException {
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

	public Table getTable(int id) throws Exception {
		Table result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
				+ "t.classname, t.showwarning, t.modifiedtime, t.keys, t.values, tt.name as type "
				+ "FROM tables as t JOIN tabletypes as tt ON t.type=tt.id AND t.id=" + id);

		if (rs.next()) {
			result = initTable(rs);
		}

		rs.close();
		stmt.close();
		return result;
	}

	public Table getTable(String name, Integer categoryId) throws Exception {
		Table result = null;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, t.classname, "
						+ "t.showwarning, t.modifiedtime, t.keys, t.values, tt.name as type "
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
	public int getProductIdByPrimaryTableId(int tableId) throws SQLException {
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
	public int getProductIdBySecondaryTableId(int tableId) throws SQLException {
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

	public List<Table> getTablesByCategory(Category category) throws Exception {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showwarning, t.modifiedtime, t.keys, t.values, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id AND t.categoryid=" + category.getId() + " ORDER BY t.name");
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	public List<OldKey> getOldKeys(int tableId) throws SQLException {
		ArrayList<OldKey> result = new ArrayList<OldKey>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM keys WHERE tableid=" + tableId + " ORDER BY \"order\"");
		while (rs.next()) {
			result.add(initKey(rs));
		}

		stmt.close();
		return result;
	}

	public List<OldRow> getOldRows(int tableId) throws SQLException {
		ArrayList<OldRow> result = new ArrayList<OldRow>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM rows WHERE tableid=" + tableId + " ORDER BY \"order\"");
		while (rs.next()) {
			result.add(initRow(rs));
		}

		stmt.close();
		return result;
	}

	public ArrayList<OldValue> getOldValues(OldRow row) throws SQLException {
		ArrayList<OldValue> result = new ArrayList<OldValue>();
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

	/**
	 * @param productId
	 * @param table
	 * @param rowOrder
	 *            - one-based order of the row in the table.
	 * @return
	 * @throws Exception
	 */
	public List<Table> getTablesUsingRow(int productId, Table table, int rowOrder) throws Exception {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		List<Table> allTables = getTablesOfProduct(productId, TableType.TABLE);
		allTables.addAll(getTablesOfProduct(productId, TableType.PRECONDITION));
		allTables.addAll(getTablesOfProduct(productId, TableType.POSTCONDITION));
		allTables.addAll(getTablesOfProduct(productId, TableType.STORAGE));
		for (Table foundTable : allTables) {
			boolean found = false;
			Key[] keys = foundTable.getKeys();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].getRefid() == table.getId()) {
					String[][] values = foundTable.getValues();
					for (int j = 0; j < values.length; j++) {
						String[] indexes = values[j][i].split(";");
						for (String index : indexes) {
							if (index.equals(String.valueOf(rowOrder))) {
								result.add(foundTable);
								found = true;
								break;
							}
						}
						if (found) {
							break;
						}
					}
				}
				if (found) {
					break;
				}
			}
		}

		stmt.close();
		return result;
	}

	public Integer getChildTableId(Integer tableId, TableType childType) throws SQLException {
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

	public Category getCategory(int id) throws SQLException {
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

	public Integer getCategoryId(String name, int productId, TableType type, Integer parentId, String parentPath)
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

	/**
	 * Adds escaping symbols to the value, so that it could be properly inserted
	 * to the database.
	 * 
	 * @param value
	 * @return value that is ready for DB inserting.
	 */
	private String[][] escape(String[][] values) {
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				values[i][j] = values[i][j].replace("'", "''");
			}
		}
		return values;
	}

	public boolean deleteCategory(Category category) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DELETE FROM categories WHERE id=" + category.getId());
		boolean result = false;
		ResultSet rs = stmt.executeQuery("SELECT id FROM categories WHERE id=" + category.getId());
		if (!rs.next()) {
			result = true;
		}
		rs.close();
		rs.close();
		stmt.close();
		return result;
	}

	public List<Table> getTablesUsingStorage(Table storage, int productId) throws Exception {
		List<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		List<Table> allTables = getTablesOfProduct(productId, TableType.TABLE);
		allTables.addAll(getTablesOfProduct(productId, TableType.PRECONDITION));
		allTables.addAll(getTablesOfProduct(productId, TableType.POSTCONDITION));
		allTables.addAll(getTablesOfProduct(productId, TableType.STORAGE));
		for (Table table : allTables) {
			Key[] keys = table.getKeys();
			for (Key key : keys) {
				if (key.getRefid() == storage.getId()) {
					result.add(table);
					break;
				}
			}
		}
		stmt.close();
		return result;
	}

	public boolean deleteTable(Table table, int productId) throws SQLException {
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT id FROM tables WHERE parentid=" + table.getId());
		while (rs.next()) {
			deleteTable(new Table(rs.getInt("id")), productId);
		}
		rs.close();

		stmt.executeUpdate("DELETE FROM tables WHERE id=" + table.getId());

		ResultSet rs2 = stmt.executeQuery("SELECT id FROM tables WHERE id=" + table.getId());
		if (!rs2.next()) {
			result = true;
		}
		rs2.close();

		stmt.close();
		return result;
	}

	public String updateTable(Table table) throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String finalName = table.getName();
		if (finalName != null) {
			finalName = "'" + finalName + "'";
		}
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Gson gson = new Gson();
		stmt.executeUpdate("UPDATE tables SET name=" + finalName + ", type=" + table.getType().getId()
				+ ", classname='" + table.getClassName() + "', categoryid=" + table.getCategoryId() + ", parentid="
				+ table.getParentId() + ", showwarning=" + table.isShowWarning() + ", modifiedtime='" + time
				+ "', keys='" + gson.toJson(table.getKeys()) + "', values='" + gson.toJson(table.getValues())
				+ "' WHERE id=" + table.getId());

		stmt.close();
		return time;
	}

	public void updateCategory(Category category) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE categories SET name='" + category.getName() + "' WHERE id=" + category.getId());

		stmt.close();
	}

	public boolean isLastAdmin(String userId) throws SQLException {
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

	public Product getProduct(String name) throws SQLException {
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

	public int insertProduct(String name, String path) throws SQLException {
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

	public int getAdminsCount() throws SQLException {
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

	public void executeUpdate(String query) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);

		stmt.close();
	}

	public boolean deleteProduct(int productId) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT c.id, c.name, c.productid, c.parentid, tt.name as type "
				+ " FROM categories as c JOIN tabletypes as tt ON c.type=tt.id AND c.productid=" + productId);

		while (rs.next()) {
			Category category = initCategory(rs);
			if (!deleteCategory(category)) {
				rs.close();
				stmt.close();
				return false;
			}
		}
		stmt.executeUpdate("DELETE FROM userpermissions WHERE productid=" + productId);
		stmt.executeUpdate("DELETE FROM products WHERE id=" + productId);

		rs.close();
		stmt.close();
		return true;
	}

	public void updateProduct(Product product) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE products SET name='" + product.getName() + "' WHERE id=" + product.getId());

		stmt.close();
	}

	public List<Table> getTablesOfProduct(int productId, TableType tableType) throws Exception {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		TableType categoryType = tableType.getParentTableType();
		String query;
		if (tableType == TableType.PRECONDITION || tableType == TableType.POSTCONDITION) {
			query = "SELECT t.id, t.name, t.categoryid, t.parentid, "
					+ "t.classname, t.showwarning, t.modifiedtime, t.keys, t.values, tt.name as type FROM tables as t "
					+ "JOIN tabletypes as tt ON t.type=tt.id " + "JOIN tables as t2 ON t.parentid=t2.id "
					+ "WHERE t2.categoryid IN (SELECT id FROM categories WHERE productid=" + productId
					+ " AND type=(SELECT id FROM tabletypes WHERE name='" + categoryType.toString().toLowerCase()
					+ "')) " + "AND tt.name='" + tableType.toString().toLowerCase() + "' " + "ORDER BY t.name";
		} else {
			query = "SELECT t.id, t.name, t.categoryid, t.parentid, "
					+ "t.classname, t.showwarning, t.modifiedtime, t.keys, t.values, tt.name as type FROM tables as t JOIN tabletypes as tt "
					+ "ON t.type=tt.id WHERE t.categoryid IN (SELECT id FROM categories WHERE productid=" + productId
					+ " AND type=(SELECT id FROM tabletypes WHERE name='" + categoryType.toString().toLowerCase()
					+ "')) ORDER BY t.name";
		}
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	public boolean isTableInProductExist(String name, TableType type, Category category) throws SQLException {
		if (category == null) {
			return false;
		}
		boolean result = false;
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT id FROM tables WHERE categoryid IN "
				+ "(SELECT id FROM categories WHERE productid=(SELECT productid FROM categories WHERE id="
				+ category.getId() + ")) AND type=(SELECT id FROM tabletypes WHERE name='"
				+ type.toString().toLowerCase() + "') AND name='" + name + "'");
		if (rs.next()) {
			result = true;
		}

		stmt.close();
		return result;
	}

	public List<Table> getAllTables() throws Exception {
		ArrayList<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT t.id, t.name, t.categoryid, t.parentid, "
						+ "t.classname, t.showwarning, t.modifiedtime, t.keys, t.values, tt.name as type FROM tables as t JOIN tabletypes as tt "
						+ "ON t.type=tt.id ORDER BY t.name");
		while (rs.next()) {
			result.add(initTable(rs));
		}

		stmt.close();
		return result;
	}

	/**
	 * @param table
	 * @param keyOrder
	 *            - zero-based key order.
	 * @return
	 */
	@Override
	public List<String> getValuesByKeyOrder(Table table, int keyOrder) {
		List<String> values = new ArrayList<>();
		for (String[] row : table.getValues()) {
			values.add(row[keyOrder]);
		}
		return values;
	}

	@Override
	public List<Integer> getStorageRowsUsedByTable(int productId, int storageId, int tableId) throws Exception {
		Table table = getTable(tableId);
		Key[] keys = table.getKeys();
		String[][] values = table.getValues();

		List<Integer> rows = new ArrayList<>();
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				if (keys[j].getRefid() == storageId) {
					String[] indexes = values[i][j].split(";");
					for (String index : indexes) {
						if (!index.equals("0") && !rows.contains(Integer.parseInt(index) - 1)) {
							rows.add(Integer.parseInt(index) - 1);
						}
					}
				}
			}
		}
		return rows;
	}

	@Override
	public List<Table> getTablesUsingStorage(int storageId, int productId, TableType tableType) throws Exception {
		List<Table> result = new ArrayList<Table>();
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		List<Table> allTables = getTablesOfProduct(productId, tableType);
		for (Table table : allTables) {
			Key[] keys = table.getKeys();
			for (Key key : keys) {
				if (key.getRefid() == storageId) {
					result.add(table);
					break;
				}
			}
		}
		stmt.close();
		return result;
	}
}