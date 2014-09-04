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
package org.grible.servlets.app.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;
import org.grible.settings.Lang;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetRowUsage")
public class GetRowUsage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;
	private int productId;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetRowUsage() {
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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}

			int tableId = Integer.parseInt(request.getParameter("tableid"));
			int rowOrder = Integer.parseInt(request.getParameter("row"));
			productId = Integer.parseInt(request.getParameter("product"));
			Table table = null;
			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				table = jDao.getTable(tableId, productId);
			} else {
				pDao = new PostgresDao();
				table = pDao.getTable(tableId);
			}
			List<Table> allTablesUsingRow = DataManager.getInstance().getDao()
					.getTablesUsingRow(productId, table, rowOrder + 1);
			String usageInTables = getTestTableOccurences(allTablesUsingRow);
			String usageInStorages = getDataStorageOccurences(allTablesUsingRow);

			String result = "";
			if (!usageInTables.isEmpty() || !usageInStorages.isEmpty()) {
				result = Lang.get("error") + ": " + Lang.get("cannotdeleterow") + ":";
				if (!usageInTables.isEmpty()) {
					result += "<br> - " + Lang.get("tables") + ": " + usageInTables + ";";
				}
				if (!usageInStorages.isEmpty()) {
					result += "<br> - " + Lang.get("storages") + ": " + usageInStorages + ";";
				}
			}

			out.print(result);

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private String getTestTableOccurences(List<Table> tables) throws Exception {
		List<String> tableNames = new ArrayList<String>();
		for (int i = 0; i < tables.size(); i++) {
			if (TableType.TABLE == tables.get(i).getType()) {
				if (!tableNames.contains(tables.get(i).getName())) {
					tableNames.add(tables.get(i).getName());
				}
			} else if (TableType.PRECONDITION == tables.get(i).getType()
					|| TableType.POSTCONDITION == tables.get(i).getType()) {
				String tableName = ServletHelper.isJson() ? jDao.getTable(
						jDao.getParentTableId(tables.get(i).getId(), productId, tables.get(i).getType()), productId)
						.getName() : pDao.getTable(tables.get(i).getParentId()).getName();
				if (!tableNames.contains(tableName)) {
					tableNames.add(tableName);
				}
			}
		}
		return StringUtils.join(tableNames, ", ");
	}

	private String getDataStorageOccurences(List<Table> tables) throws SQLException {
		List<String> tableNames = new ArrayList<String>();
		for (int i = 0; i < tables.size(); i++) {
			if (TableType.STORAGE == tables.get(i).getType()) {
				tableNames.add(tables.get(i).getName());
			}
		}
		return StringUtils.join(tableNames, ", ");
	}
}
