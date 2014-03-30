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

import java.util.List;

import org.grible.model.Category;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.Key;

/**
 * @author Maksym Barvinskyi
 */
public interface Dao {

	public List<Product> getProducts() throws Exception;

	public Product getProduct(int id) throws Exception;

	public List<Category> getAllCategories(int productId, TableType type) throws Exception;

	public List<Category> getTopLevelCategories(int productId, TableType type) throws Exception;

	public List<Category> getChildCategories(Category category) throws Exception;

	public int insertCategory(TableType type, int productId, String name, Integer parentId, String parentPath)
			throws Exception;

	public int insertTable(String name, TableType type, Category category, Integer parentId, String className,
			Key[] keys, String[][] values) throws Exception;

	public List<Table> getTablesByCategory(Category category) throws Exception;

	public Integer getCategoryId(String name, int productId, TableType type, Integer parentId, String parentPath)
			throws Exception;

	public boolean deleteCategory(Category category) throws Exception;

	public List<Table> getTablesUsingStorage(Table storage, int productId) throws Exception;

	public List<Table> getTablesUsingStorage(int storageId, int productId, TableType tableType) throws Exception;

	public boolean deleteTable(Table table, int productId) throws Exception;

	public void updateCategory(Category category) throws Exception;

	public Product getProduct(String name) throws Exception;

	public int insertProduct(String name, String path) throws Exception;

	public boolean deleteProduct(int productId) throws Exception;

	public void updateProduct(Product product) throws Exception;

	public List<Table> getTablesOfProduct(int productId, TableType type) throws Exception;

	public boolean isTableInProductExist(String name, TableType type, Category category) throws Exception;

	public String updateTable(Table table) throws Exception;
	
	public List<String> getValuesByKeyOrder(Table table, int keyOrder);
	
	public List<Table> getTablesUsingRow(int productId, Table table, int rowOrder) throws Exception;

	public List<Integer> getStorageRowsUsedByTable(int productId, int storageId, int tableId) throws Exception;
}