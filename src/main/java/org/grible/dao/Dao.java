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

import java.util.ArrayList;
import java.util.List;

import org.grible.model.Category;
import org.grible.model.Key;
import org.grible.model.Product;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.Value;

/**
 * @author Maksym Barvinskyi
 */
public interface Dao {

	public List<Product> getProducts() throws Exception;

	public Product getProduct(int id) throws Exception;

	public List<Category> getAllCategories(int productId, TableType type) throws Exception;

	public List<Category> getTopLevelCategories(int productId, TableType type) throws Exception;

	public List<Category> getChildCategories(Category category) throws Exception;

	public int insertCategory(TableType type, int productId, String name, Integer parentId, String parentPath) throws Exception;

	public int insertTable(String name, TableType type, Category category, Integer parentId, String className) throws Exception;

	public int insertRow(int tableId, int order) throws Exception;

	public int insertKey(int tableId, String name, int order, int referenceStorageId) throws Exception;

	public void insertValue(int rowId, int keyId, String value, boolean isStorage, String storageIdsAsString)
			throws Exception;

	public Table getTable(String name, Integer categoryId) throws Exception;

	/**
	 * Method for 'tables' or 'storages'.
	 */
	public int getProductIdByPrimaryTableId(int tableId) throws Exception;

	/**
	 * Method for 'preconditions' or 'postconditions'.
	 */
	public int getProductIdBySecondaryTableId(int tableId) throws Exception;

	public List<Table> getTablesByCategory(Category category) throws Exception;

	public List<Key> getKeys(int tableId) throws Exception;

	public List<Row> getRows(int tableId) throws Exception;

	public ArrayList<Value> getValues(Row row) throws Exception;

	public List<Table> getTablesUsingRow(int rowId) throws Exception;

	public Category getCategory(int id) throws Exception;

	public List<Value> getValues(Key key) throws Exception;

	public Row getRow(int id) throws Exception;

	public Value getValue(int id) throws Exception;

	public Key getKey(int id) throws Exception;

	public Integer getCategoryId(String name, int productId, TableType type, Integer parentId, String parentPath)
			throws Exception;

	public List<Integer> insertKeys(int tableId, List<String> keyNames) throws Exception;

	public List<Integer> insertValuesEmptyWithKeyId(int keyId, List<Row> rows) throws Exception;

	public void updateKeys(List<Integer> keyIds, List<Integer> keyNumbers) throws Exception;

	public int insertKeyCopy(Key currentKey) throws Exception;

	public List<Integer> insertValuesWithKeyId(int newKeyId, List<Value> values) throws Exception;

	public void updateRows(List<Integer> rowIds, List<Integer> oldRowNumbers, List<Integer> modifiedRowNumbers)
			throws Exception;

	public void updateRows(List<Integer> rowIds, List<Integer> rowNumbers) throws Exception;

	public int insertRowCopy(Row currentRow) throws Exception;

	public List<Integer> insertValuesWithRowId(int newRowId, List<Value> values) throws Exception;

	public List<Integer> insertValuesEmptyByRowIdFromExistingRow(int rowId, List<Value> values)
			throws Exception;

	public List<Integer> insertValuesEmptyWithRowId(int rowId, List<Key> keys) throws Exception;

	public boolean deleteCategory(Category category) throws Exception;

	public List<Table> getTablesUsingStorage(Table storage) throws Exception;

	public boolean deleteTable(Table table, int productId) throws Exception;

	public boolean deleteKey(int keyId) throws Exception;

	public boolean deleteRow(int rowId) throws Exception;

	public List<Integer> insertRows(int tableId, int rowsNumber) throws Exception;

	public List<Integer> addRows(int tableId, int currRowsNumber, int rowsNumberToAdd) throws Exception;

	public void insertValues(List<Integer> rowIds, List<Integer> keyIds, ArrayList<ArrayList<String>> values)
			throws Exception;

	public void updateCategory(Category category) throws Exception;

	public void updateKeys(String[] keyIds, String[] keyValues) throws Exception;

	public void updateKeyValue(String id, String value) throws Exception;

	public int getRefStorageId(int keyId) throws Exception;

	public Row getRow(int refStorageId, int order) throws Exception;

	public void updateValues(ArrayList<Value> values) throws Exception;

	public void updateKey(Key key) throws Exception;

	public void updateValuesTypes(int keyId, boolean isStorage, String storageIds) throws Exception;

	public void updateValue(Value value) throws Exception;
	
	public Product getProduct(String name) throws Exception;

	public int insertProduct(String name, String path) throws Exception;

	public boolean deleteProduct(int productId) throws Exception;

	public void updateProduct(Product product) throws Exception;

	public List<Table> getTablesOfProduct(int productId, TableType type) throws Exception;

	public boolean isTableInProductExist(String name, TableType type, Category category) throws Exception;

	public List<Key> insertKeysFromOneTableToAnother(int copyTableId, int tableId) throws Exception;

	public void insertValues(int tableId, int oldTableId, List<Row> oldRows, List<Key> keys) throws Exception;

	public boolean isTableTypeExist(String name) throws Exception;

	public List<Value> getValuesByEnumValue(Value enumValue, String oldValue) throws Exception;

	public void updateTable(Table table) throws Exception;
}