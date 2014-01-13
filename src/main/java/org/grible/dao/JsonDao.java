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
package org.grible.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.grible.model.Category;
import org.grible.model.Key;
import org.grible.model.Product;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.Value;
import org.grible.settings.GlobalSettings;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getTopLevelCategories(int productId, TableType type) throws Exception {
		List<Category> result = new ArrayList<>();
		Product product = getProduct(productId);
		File dir = new File(product.getPath() + File.separator + type.getSection().getDirName());
		File[] subdirs = dir.listFiles();
		for (File subdir : subdirs) {
			if (subdir.isDirectory()){
				result.add(new Category(subdir.getName(), type, productId));
			}
		}
		return result;
	}

	@Override
	public List<Category> getChildCategories(int categoryId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int insertCategory(TableType type, int productId, String name, Integer parentId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertTable(String name, TableType type, Integer categoryId, Integer parentId, String className)
			throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertRow(int tableId, int order) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertKey(int tableId, String name, int order, int referenceStorageId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insertValue(int rowId, int keyId, String value, boolean isStorage, String storageIdsAsString)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Table getTable(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table getTable(String name, Integer categoryId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getProductIdByPrimaryTableId(int tableId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProductIdBySecondaryTableId(int tableId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Table> getTablesByCategoryId(int categoryId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Key> getKeys(int tableId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Row> getRows(int tableId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Value> getValues(Row row) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Table> getTablesUsingRow(int rowId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChildtable(int tableId, TableType childType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category getCategory(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Value> getValues(Key key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Row getRow(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value getValue(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Key getKey(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Table> getRefTablesOfProductByKeyId(int keyId, TableType type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getCategoryId(String name, int productId, TableType type, Integer parentId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> insertKeys(int tableId, List<String> keyNames) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> insertValuesEmptyWithKeyId(int keyId, List<Row> rows) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateKeys(List<Integer> keyIds, List<Integer> keyNumbers) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int insertKeyCopy(Key currentKey) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> insertValuesWithKeyId(int newKeyId, List<Value> values) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateRows(List<Integer> rowIds, List<Integer> oldRowNumbers, List<Integer> modifiedRowNumbers)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRows(List<Integer> rowIds, List<Integer> rowNumbers) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int insertRowCopy(Row currentRow) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> insertValuesWithRowId(int newRowId, List<Value> values) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> insertValuesEmptyByRowIdFromExistingRow(int rowId, List<Value> values) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> insertValuesEmptyWithRowId(int rowId, List<Key> keys) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteCategory(int categoryId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Table> getTablesUsingStorage(int storageId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteTable(int tableId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteKey(int keyId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRow(int rowId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Integer> insertRows(int tableId, int rowsNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> addRows(int tableId, int currRowsNumber, int rowsNumberToAdd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertValues(List<Integer> rowIds, List<Integer> keyIds, ArrayList<ArrayList<String>> values)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTable(Table table) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateCategory(Category category) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateKeys(String[] keyIds, String[] keyValues) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateKeyValue(String id, String value) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRefStorageId(int keyId) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Row getRow(int refStorageId, int order) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateValues(ArrayList<Value> values) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateKey(Key key) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateValuesTypes(int keyId, boolean isStorage, String storageIds) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateValue(Value value) throws Exception {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTableInProductExist(String name, TableType type, Integer categoryId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Key> insertKeysFromOneTableToAnother(int copyTableId, int tableId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertValues(int tableId, int oldTableId, List<Row> oldRows, List<Key> keys) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTableTypeExist(String name) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Value> getValuesByEnumValue(Value enumValue, String oldValue) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}