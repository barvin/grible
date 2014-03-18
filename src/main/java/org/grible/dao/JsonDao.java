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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.grible.helpers.IOHelper;
import org.grible.helpers.StringHelper;
import org.grible.model.Category;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.Key;
import org.grible.settings.GlobalSettings;
import org.grible.uimodel.Section;
import org.grible.uimodel.Sections;

/**
 * @author Maksym Barvinskyi
 */
public class JsonDao implements Dao {

	@Override
	public List<Product> getProducts() throws Exception {
		List<Product> productList = GlobalSettings.getInstance().getConfigJson().read().getProducts();
		return productList;
	}

	@Override
	public Product getProduct(int id) throws Exception {
		List<Product> productList = getProducts();
		for (Product product : productList) {
			if (product.getId() == id) {
				return product;
			}
		}
		return null;
	}

	@Override
	public List<Category> getAllCategories(int productId, TableType type) throws Exception {
		Product product = getProduct(productId);
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName());
		return getSubCategoriesRecursively(dir, "", type, productId);
	}

	private List<Category> getSubCategoriesRecursively(File dir, String initPath, TableType type, int productId) {
		List<Category> categories = new ArrayList<>();
		List<File> subdirs = Arrays.asList(dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}
		}));
		subdirs = new ArrayList<File>(subdirs);
		for (File subdir : subdirs) {
			categories.add(new Category(initPath + subdir.getName(), type, productId));
		}

		List<Category> deepSubcategories = new ArrayList<Category>();
		for (File subdir : subdirs) {
			deepSubcategories.addAll(getSubCategoriesRecursively(subdir, initPath + subdir.getName() + File.separator,
					type, productId));
		}
		categories.addAll(deepSubcategories);
		return categories;
	}

	@Override
	public List<Category> getTopLevelCategories(int productId, TableType type) throws Exception {
		List<Category> result = new ArrayList<>();
		Product product = getProduct(productId);
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName());
		File[] subdirs = dir.listFiles();
		if (subdirs != null) {
			for (File subdir : subdirs) {
				if (subdir.isDirectory()) {
					result.add(new Category(subdir.getName(), type, productId));
				}
			}
		}
		return result;
	}

	@Override
	public List<Category> getChildCategories(Category category) throws Exception {
		List<Category> result = new ArrayList<>();
		Product product = getProduct(category.getProductId());
		File dir = new File(product.getPath() + File.separator + category.getType().getSection().getDirName()
				+ File.separator + category.getPath());
		File[] subdirs = dir.listFiles();
		for (File subdir : subdirs) {
			if (subdir.isDirectory()) {
				result.add(new Category(category.getPath() + File.separator + subdir.getName(), category.getType(),
						category.getProductId()));
			}
		}
		return result;
	}

	@Override
	public int insertCategory(TableType type, int productId, String name, Integer parentId, String parentPath)
			throws Exception {
		Product product = getProduct(productId);
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName() + File.separator
				+ parentPath + name);
		dir.mkdir();
		return 0;
	}

	@Override
	public int insertTable(String name, TableType type, Category category, Integer parentId, String className,
			Key[] keys, String[][] values) throws Exception {
		Product product = getProduct(category.getProductId());
		if (parentId != null) {
			name = getTable(parentId, category.getProductId()).getName() + "_" + type.toString();
		}
		File file = new File(product.getPath() + File.separator + category.getType().getSection().getDirName()
				+ File.separator + category.getPath() + File.separator + name + ".json");
		Table table = new Table(file);
		table.getTableJson().setType(type);
		table.getTableJson().setClassName(className);
		table.getTableJson().setShowWarning(true);
		table.getTableJson().setKeys(keys);
		table.getTableJson().setValues(values);		
		table.save();
		table.setTableJson();

		int id = product
				.getGribleJson()
				.read()
				.addPath(
						category.getType().getSection().getDirName() + File.separator + category.getPath()
								+ File.separator + name + ".json");
		product.getGribleJson().save();
		return id;
	}

	public Table getTable(int id, int productId) throws Exception {
		Product product = getProduct(productId);
		String path = product.getGribleJson().read().getPathById(id);
		if (path == null) {
			throw new Exception("Entry with id = " + id + " not found in '" + product.getGribleJson().getFilePath()
					+ "'.");
		}
		File file = new File(product.getPath() + File.separator + path);
		Table table = new Table(file);
		table.setTableJson();
		table.setId(id);
		return table;
	}

	@Override
	public List<Table> getTablesByCategory(Category category) throws Exception {
		List<Table> result = new ArrayList<>();
		Product product = getProduct(category.getProductId());
		File dir = new File(product.getPath() + File.separator + category.getType().getSection().getDirName()
				+ File.separator + category.getPath());
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".json")
						&& !file.getName().contains("_PRECONDITION.json") && !file.getName().contains(
						"_POSTCONDITION.json"));
			}
		});
		if (files != null) {
			for (File file : files) {
				Table table = new Table(file);
				table.setTableJson();
				table.setId(product
						.getGribleJson()
						.read()
						.getIdByPath(
								category.getType().getSection().getDirName() + File.separator + category.getPath()
										+ File.separator + file.getName()));
				result.add(table);
			}
		}
		return result;
	}

	public Integer getChildTableId(int tableId, int productId, TableType childType) throws Exception {
		Product product = getProduct(productId);
		String parentPath = product.getGribleJson().read().getPathById(tableId);
		String childPath = StringUtils.replace(parentPath, ".json", "_" + childType.toString() + ".json");
		int result = product.getGribleJson().getIdByPath(childPath);
		return (result == 0) ? null : result;
	}

	public Integer getParentTableId(int tableId, int productId, TableType childType) throws Exception {
		Product product = getProduct(productId);
		String childPath = product.getGribleJson().read().getPathById(tableId);
		String parentPath = StringUtils.replace(childPath, "_" + childType.toString() + ".json", ".json");
		return product.getGribleJson().getIdByPath(parentPath);
	}

	@Override
	public Integer getCategoryId(String name, int productId, TableType type, Integer parentId, String parentPath)
			throws Exception {
		Product product = getProduct(productId);
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName() + File.separator
				+ parentPath + File.separator + name);
		if (dir.exists()) {
			return 1;
		}
		return null;
	}

	@Override
	public boolean deleteCategory(Category category) throws Exception {
		Product product = getProduct(category.getProductId());
		File dir = new File(product.getPath() + File.separator + category.getType().getSection().getDirName()
				+ File.separator + category.getPath());
		dir.delete();
		return !dir.exists();
	}

	@Override
	public List<Table> getTablesUsingStorage(Table table, int productId) throws Exception {
		List<Table> result = new ArrayList<Table>();
		List<Table> tables = getTablesOfProduct(productId, TableType.TABLE);
		addTablesUsingTable(result, tables, table);
		List<Table> preconditions = getTablesOfProduct(productId, TableType.PRECONDITION);
		addTablesUsingTable(result, preconditions, table);
		List<Table> postconditions = getTablesOfProduct(productId, TableType.POSTCONDITION);
		addTablesUsingTable(result, postconditions, table);
		List<Table> storages = getTablesOfProduct(productId, TableType.STORAGE);
		addTablesUsingTable(result, storages, table);
		return result;
	}

	private void addTablesUsingTable(List<Table> result, List<Table> tables, Table table) {
		for (Table foundTable : tables) {
			Key[] keys = foundTable.getTableJson().getKeys();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].getRefid() == table.getId()) {
					result.add(foundTable);
					break;
				}
			}
		}
	}

	@Override
	public boolean deleteTable(Table table, int productId) throws Exception {
		if (table.getType() == TableType.TABLE) {
			Integer preId = getChildTableId(table.getId(), productId, TableType.PRECONDITION);
			if (preId != null) {
				deleteTable(getTable(preId, productId), productId);
			}
			Integer ppstId = getChildTableId(table.getId(), productId, TableType.POSTCONDITION);
			if (ppstId != null) {
				deleteTable(getTable(ppstId, productId), productId);
			}
		}
		Product product = getProduct(productId);
		product.getGribleJson().read().deleteId(table.getId());
		product.getGribleJson().save();
		File file = table.getFile();
		file.delete();
		return !file.exists();
	}

	public void updateTable(Table table) throws Exception {
		table.getTableJson().setClassName(table.getClassName());
		table.getTableJson().setShowWarning(table.isShowWarning());
		table.save();
	}

	@Override
	public void updateCategory(Category category) throws Exception {
		Product product = getProduct(category.getProductId());
		File dir = new File(product.getPath() + File.separator + category.getType().getSection().getDirName()
				+ File.separator + category.getPath());
		String newFullPath = StringUtils.substringBeforeLast(category.getPath(), File.separator) + File.separator
				+ category.getName();
		dir.renameTo(new File(product.getPath() + File.separator + category.getType().getSection().getDirName()
				+ File.separator + newFullPath));
	}

	@Override
	public Product getProduct(String name) throws Exception {
		List<Product> productList = getProducts();
		for (Product product : productList) {
			if (product.getName().equals(name)) {
				return product;
			}
		}
		return null;
	}

	public boolean isProductWithPathExists(String path) throws Exception {
		List<Product> productList = getProducts();
		for (Product product : productList) {
			if (product.getPath().equals(path)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int insertProduct(String name, String path) throws Exception {
		List<Product> productList = getProducts();
		int id = 1;
		if (!productList.isEmpty()) {
			id = productList.get(productList.size() - 1).getId() + 1;
		}
		Product product = new Product(id);
		product.setName(name);
		product.setPath(path);
		productList.add(product);

		GlobalSettings.getInstance().getConfigJson().setProducts(productList);
		GlobalSettings.getInstance().getConfigJson().save();

		File gribleJsonFile = new File(product.getGribleJson().getFilePath());
		if (gribleJsonFile.exists()) {
			for (Section section : Sections.getSections()) {
				File dir = new File(path + File.separator + section.getDirName());
				if (!dir.exists()) {
					dir.mkdir();
				}
			}
		} else {
			product.getGribleJson().save();
			for (Section section : Sections.getSections()) {
				File dir = new File(path + File.separator + section.getDirName());
				if (dir.exists()) {
					dir.delete();
				}
				dir.mkdir();
			}
		}
		return id;
	}

	@Override
	public boolean deleteProduct(int productId) throws Exception {
		List<Product> productList = getProducts();
		int prNumberToDelete = -1;
		for (int i = 0; i < productList.size(); i++) {
			if (productList.get(i).getId() == productId) {
				prNumberToDelete = i;
				break;
			}
		}
		productList.remove(prNumberToDelete);
		GlobalSettings.getInstance().getConfigJson().setProducts(productList);
		GlobalSettings.getInstance().getConfigJson().save();
		return true;
	}

	@Override
	public void updateProduct(Product product) throws Exception {
		List<Product> productList = getProducts();
		for (Product pr : productList) {
			if (pr.getId() == product.getId()) {
				pr.setName(product.getName());
				pr.setPath(product.getPath());
				break;
			}
		}
		GlobalSettings.getInstance().getConfigJson().setProducts(productList);
		GlobalSettings.getInstance().getConfigJson().save();
	}

	@Override
	public List<Table> getTablesOfProduct(int productId, TableType type) throws Exception {
		List<Table> result = new ArrayList<Table>();
		Product product = getProduct(productId);
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName());
		List<File> files = new ArrayList<>();
		IOHelper.searchAllFiles(files, dir, ".json");
		for (File file : files) {
			Table table = new Table(file);
			table.setTableJson();
			if (table.getType() == type) {
				table.setId(product
						.getGribleJson()
						.read()
						.getIdByPath(
								type.getSection().getDirName() + File.separator
										+ StringHelper.getCategoryPathFromTable(table, productId, type)
										+ File.separator + file.getName()));
				result.add(table);
			}
		}
		return result;
	}

	@Override
	public boolean isTableInProductExist(String name, TableType type, Category category) throws Exception {
		if (category == null) {
			return false;
		}
		Product product = getProduct(category.getProductId());
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName());
		File file = IOHelper.searchFile(dir, name + ".json");
		return file != null;
	}

	public void moveTableFile(Table table, Integer productId, String newCategoryPath, String newName) throws Exception {
		if (table.getType() == TableType.TABLE) {
			Integer preId = getChildTableId(table.getId(), productId, TableType.PRECONDITION);
			if (preId != null) {
				moveTableFile(getTable(preId, productId), productId, newCategoryPath, newName);
			}
			Integer ppstId = getChildTableId(table.getId(), productId, TableType.POSTCONDITION);
			if (ppstId != null) {
				moveTableFile(getTable(ppstId, productId), productId, newCategoryPath, newName);
			}
		} else if (table.getType() == TableType.PRECONDITION || table.getType() == TableType.POSTCONDITION) {
			newName = newName + "_" + table.getType();
		}
		Product product = getProduct(productId);
		String pathAfterProduct = table.getType().getSection().getDirName() + File.separator + newCategoryPath
				+ File.separator + newName + ".json";
		product.getGribleJson().read().updatePath(table.getId(), pathAfterProduct);
		product.getGribleJson().save();
		File file = table.getFile();
		file.renameTo(new File(product.getPath() + File.separator + pathAfterProduct));
	}

	/**
	 * @param table
	 * @param rowOrder
	 *            - one-based order of the row in the table.
	 * @return
	 */
	public List<Table> getTablesUsingRow(int productId, Table table, int rowOrder) throws Exception {
		List<Table> result = new ArrayList<Table>();
		List<Table> tables = getTablesOfProduct(productId, TableType.TABLE);
		addTablesUsingRow(result, tables, table, rowOrder);
		List<Table> preconditions = getTablesOfProduct(productId, TableType.PRECONDITION);
		addTablesUsingRow(result, preconditions, table, rowOrder);
		List<Table> postconditions = getTablesOfProduct(productId, TableType.POSTCONDITION);
		addTablesUsingRow(result, postconditions, table, rowOrder);
		List<Table> storages = getTablesOfProduct(productId, TableType.STORAGE);
		addTablesUsingRow(result, storages, table, rowOrder);
		return result;
	}

	private void addTablesUsingRow(List<Table> result, List<Table> tables, Table table, int rowOrder) {
		for (Table foundTable : tables) {
			Key[] keys = foundTable.getTableJson().getKeys();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].getRefid() == table.getId()) {
					String[][] values = foundTable.getTableJson().getValues();
					for (int j = 0; j < values.length; j++) {
						String[] indexes = values[j][i].split(";");
						for (String index : indexes) {
							if (index.equals(String.valueOf(rowOrder))) {
								result.add(foundTable);
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param table
	 * @param keyOrder
	 *            - zero-based key order.
	 * @return
	 */
	public List<String> getValuesByKeyOrder(Table table, int keyOrder) {
		List<String> values = new ArrayList<>();
		for (String[] row : table.getTableJson().getValues()) {
			values.add(row[keyOrder]);
		}
		return values;
	}

	public Table getTable(String name, TableType type, Category category) throws Exception {
		Product product = getProduct(category.getProductId());
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName());
		File file = IOHelper.searchFile(dir, name + ".json");
		Table table = new Table(file);
		table.setTableJson();
		String path = type.getSection().getDirName() + File.separator;
		path += StringHelper.getCategoryPathFromTable(table, category.getProductId(), type);
		path += File.separator + name + ".json";
		table.setId(product.getGribleJson().read().getIdByPath(path));
		return table;
	}

}