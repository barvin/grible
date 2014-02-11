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
package org.grible.servlets.app.create;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.helpers.StringHelper;
import org.grible.model.Category;
import org.grible.model.Key;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.KeyJson;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddTable")
public class AddTable extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddTable() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}

			Category category = null;
			Integer productId = 0;
			Integer parentId = null;
			
			if (isJson()) {
				jDao = new JsonDao();
			} else {
				pDao = new PostgresDao();
			}
			
			if (isJson()) {
				productId = Integer.parseInt(request.getParameter("product"));
				TableType tableType = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
				if (request.getParameter("categorypath") != null) {
					String path = request.getParameter("categorypath");
					category = new Category(path, tableType, productId);
				} else {
					TableType currType = TableType.valueOf(request.getParameter("currTabletype").toUpperCase());
					if (currType == TableType.TABLE) {
						parentId = Integer.parseInt(request.getParameter("parentid"));
					} else {
						parentId = jDao.getParentTableId(Integer.parseInt(request.getParameter("parentid")), productId,
								currType);
					}
					String categoryPathFromTable = StringHelper.getCategoryPathFromTable(
							jDao.getTable(parentId, productId), productId, tableType);
					category = new Category(categoryPathFromTable, tableType, productId);
				}
			} else if (request.getParameter("categoryid") != null) {
				int categoryId = Integer.parseInt(request.getParameter("categoryid"));
				category = pDao.getCategory(categoryId);
			}

			if ((!isJson()) && (request.getParameter("parentid") != null)) {
				TableType currType = TableType.valueOf(request.getParameter("currTabletype").toUpperCase());
				if (currType == TableType.TABLE) {
					parentId = Integer.parseInt(request.getParameter("parentid"));
				} else {
					Table sibling = new PostgresDao().getTable(Integer.parseInt(request.getParameter("parentid")));
					parentId = sibling.getParentId();
				}
			}

			String name = request.getParameter("name");
			if (("").equals(name)) {
				throw new Exception("ERROR: Name cannot be empty.");
			}
			TableType type = TableType.valueOf(request.getParameter("tabletype").toUpperCase());

			if (DataManager.getInstance().getDao().isTableInProductExist(name, type, category)) {
				throw new Exception("ERROR: " + type.toString().toLowerCase() + " with name '" + name
						+ "' already exists.");
			}
			String className = request.getParameter("classname");

			int tableId = DataManager.getInstance().getDao().insertTable(name, type, category, parentId, className);
			boolean isCopy = Boolean.parseBoolean(request.getParameter("iscopy"));
			if (isCopy) {
				int copyTableId = Integer.parseInt(request.getParameter("copytableid"));
				boolean isOnlyColumns = Boolean.parseBoolean(request.getParameter("isonlycolumns"));
				if (isJson()) {
					Table table = jDao.getTable(tableId, productId);
					Table tableToCopy = jDao.getTable(copyTableId, productId);
					table.getTableJson().setKeys(tableToCopy.getTableJson().getKeys());
					if (isOnlyColumns) {
						int keysCount = tableToCopy.getTableJson().getKeys().length;
						String[][] values = new String[1][keysCount];
						for (int i = 0; i < keysCount; i++) {
							values[0][i] = "";
						}
						table.getTableJson().setValues(values);
					} else {
						table.getTableJson().setValues(tableToCopy.getTableJson().getValues());
					}
					table.save();
				} else {
					List<Key> keys = null;
					if (type == TableType.ENUMERATION) {
						List<String> keyName = new ArrayList<String>();
						keyName.add(name);
						pDao.insertKeys(tableId, keyName);
						keys = pDao.getKeys(tableId);
					} else {
						keys = pDao.insertKeysFromOneTableToAnother(copyTableId, tableId);
					}
					if (isOnlyColumns) {
						int rowId = pDao.insertRow(tableId, 1);
						pDao.insertValuesEmptyWithRowId(rowId, keys);
					} else {
						List<Row> oldRows = pDao.getRows(copyTableId);
						pDao.insertValues(tableId, copyTableId, oldRows, keys);
					}
				}
			} else {
				if (isJson()) {
					Table table = new JsonDao().getTable(tableId, productId);
					String keyName = "editme";
					if (type == TableType.ENUMERATION) {
						keyName = name;
					}
					table.getTableJson().setKeys(new KeyJson[] { new KeyJson(keyName, KeyType.TEXT, 0) });
					table.getTableJson().setValues(new String[][] { { "" } });
					table.save();
				} else {
					List<String> keys = new ArrayList<String>();
					if (type == TableType.ENUMERATION) {
						keys.add(name);
					} else {
						keys.add("editme");
					}
					int keyId = pDao.insertKeys(tableId, keys).get(0);
					pDao.insertRow(tableId, 1);
					List<Row> rows = pDao.getRows(tableId);
					pDao.insertValuesEmptyWithKeyId(keyId, rows);
				}
			}
			out.print(tableId);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			if ((e.getLocalizedMessage() != null) && (!e.getLocalizedMessage().startsWith("ERROR"))) {
				e.printStackTrace();
			}
		}
		out.flush();
		out.close();
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}
}
