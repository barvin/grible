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
package org.grible.servlets.app.create;

import java.io.IOException;
import java.io.PrintWriter;

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
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.Key;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

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
				throw new Exception(Lang.get("error") + ": " + Lang.get("nameempty"));
			}
			TableType type = TableType.valueOf(request.getParameter("tabletype").toUpperCase());

			if (DataManager.getInstance().getDao().isTableInProductExist(name, type, category)) {
				String errorMessage = "";
				switch (type) {
				case TABLE:
					errorMessage = Lang.get("tablewithname");
					break;
				case STORAGE:
					errorMessage = Lang.get("storagewithname");
					break;
				case ENUMERATION:
					errorMessage = Lang.get("enumwithname");
					break;

				default:
					break;
				}
				throw new Exception(Lang.get("error") + ": " + errorMessage + " '" + name + "' " + Lang.get("alreadyexists"));
			}
			String className = request.getParameter("classname");

			Key[] keys = null;
			String[][] values = null;

			boolean isCopy = Boolean.parseBoolean(request.getParameter("iscopy"));
			if (isCopy) {
				int copyTableId = Integer.parseInt(request.getParameter("copytableid"));
				boolean isOnlyColumns = Boolean.parseBoolean(request.getParameter("isonlycolumns"));
				if (isJson()) {
					Table tableToCopy = jDao.getTable(copyTableId, productId);
					keys = tableToCopy.getTableJson().getKeys();
					if (isOnlyColumns) {
						int keysCount = tableToCopy.getTableJson().getKeys().length;
						values = new String[1][keysCount];
						for (int i = 0; i < keysCount; i++) {
							values[0][i] = "";
						}
					} else {
						values = tableToCopy.getTableJson().getValues();
					}
				} else {
					Table tableToCopy = pDao.getTable(copyTableId);
					keys = tableToCopy.getKeys();
					if (isOnlyColumns) {
						int keysCount = tableToCopy.getKeys().length;
						values = new String[1][keysCount];
						for (int i = 0; i < keysCount; i++) {
							values[0][i] = "";
						}
					} else {
						values = tableToCopy.getValues();
					}
				}
			} else {
				String keyName = "editme";
				if (type == TableType.ENUMERATION) {
					keyName = name;
				}
				keys = new Key[] { new Key(keyName, KeyType.TEXT, 0) };
				values = new String[][] { { "" } };
			}

			int tableId = DataManager.getInstance().getDao()
					.insertTable(name, type, category, parentId, className, keys, values);
			out.print(tableId);
		} catch (Exception e) {
			out.print(e.getMessage());
			if ((e.getMessage() != null) && (!e.getMessage().startsWith(Lang.get("error")))) {
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
