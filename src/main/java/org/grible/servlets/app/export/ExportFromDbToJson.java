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
package org.grible.servlets.app.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.helpers.StringHelper;
import org.grible.json.IdPathPair;
import org.grible.model.Category;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.servlets.ServletHelper;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/ExportFromDbToJson")
public class ExportFromDbToJson extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PostgresDao pDao;
	private JsonDao jDao;
	private int postgresProductId;
	private int jsonProductId;
	private HashMap<Integer, Integer> crossIds;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportFromDbToJson() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			String dest = request.getParameter("dest");
			if (!dest.endsWith(File.separator)) {
				dest += File.separator;
			}

			ServletHelper.setConfigJsonFile();

			pDao = new PostgresDao();
			jDao = new JsonDao();
			crossIds = new HashMap<>();
			List<Product> products = pDao.getProducts();

			for (Product product : products) {
				String productPath = dest + product.getName();
				File productDir = new File(productPath);
				if (productDir.exists()) {
					productDir.delete();
				}
				productDir.mkdirs();
				postgresProductId = product.getId();
				jsonProductId = jDao.insertProduct(product.getName(), productPath);
				List<Category> allCategories = pDao.getTopLevelCategories(postgresProductId, TableType.TABLE);
				allCategories.addAll(pDao.getTopLevelCategories(postgresProductId, TableType.STORAGE));
				allCategories.addAll(pDao.getTopLevelCategories(postgresProductId, TableType.ENUMERATION));
				addCategories(allCategories, "");

				updateTableIds();
				crossIds.clear();
			}

			GlobalSettings.getInstance().getConfigJson().setProducts(new ArrayList<Product>());
			GlobalSettings.getInstance().getConfigJson().save();

			out.print("Done.");
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void addCategories(List<Category> categories, String parentPath) throws Exception {
		for (Category category : categories) {
			category.setName(StringHelper.removeForbiddenCharactersForFolder(category.getName()));
			jDao.insertCategory(category.getType(), jsonProductId, category.getName(), null, parentPath
					+ File.separator);
			String categoryPath = category.getName();
			if (!parentPath.isEmpty()) {
				categoryPath = parentPath + File.separator + category.getName();
			}
			List<Table> tables = pDao.getTablesByCategory(category);
			category.setPath(categoryPath);
			category.setProductId(jsonProductId);
			for (Table table : tables) {
				table.setName(StringHelper.removeForbiddenCharactersForFolder(table.getName()));
				int tableId = jDao.insertTable(table.getName(), table.getType(), category, table.getParentId(),
						table.getClassName(), table.getKeys(), table.getValues());
				crossIds.put(tableId, table.getId());
				if (table.getType() == TableType.TABLE) {
					Integer preIdPostgres = pDao.getChildTableId(table.getId(), TableType.PRECONDITION);
					if (preIdPostgres != null) {
						Table precondition = pDao.getTable(preIdPostgres);
						int preIdJson = jDao.insertTable(table.getName(), TableType.PRECONDITION, category, tableId,
								"", precondition.getKeys(), precondition.getValues());
						crossIds.put(preIdJson, preIdPostgres);
					}
					Integer postIdPostgres = pDao.getChildTableId(table.getId(), TableType.POSTCONDITION);
					if (postIdPostgres != null) {
						Table postcondition = pDao.getTable(postIdPostgres);
						int postIdJson = jDao.insertTable(table.getName(), TableType.POSTCONDITION, category, tableId,
								"", postcondition.getKeys(), postcondition.getValues());
						crossIds.put(postIdJson, postIdPostgres);
					}
				}
			}
			List<Category> childCategories = pDao.getChildCategories(category);
			addCategories(childCategories, categoryPath);
		}
	}

	private void updateTableIds() throws Exception {
		Product product = jDao.getProduct(jsonProductId);
		List<IdPathPair> pairs = product.getGribleJson().read(product.getGribleJsonPath()).getIdPathPairs();
		for (IdPathPair pair : pairs) {
			int id = pair.getId();
			pair.setId(crossIds.get(id));
		}
		product.getGribleJson().setIdPathPairs(pairs);
		product.getGribleJson().save(product.getGribleJsonPath());
	}
}