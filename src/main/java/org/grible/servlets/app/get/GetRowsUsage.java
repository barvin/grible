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
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.json.ui.UiRowsUsage;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetRowsUsage")
public class GetRowsUsage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;
	private int productId;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetRowsUsage() {
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

			UiRowsUsage uiRowsUsage = new UiRowsUsage();
			if (ServletHelper.isJson()) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				productId = Integer.parseInt(request.getParameter("product"));

				jDao = new JsonDao();
				Table table = jDao.getTable(tableId, productId);
				int rowsCount = table.getTableJson().getValues().length;
				String[] usageInTables = new String[rowsCount];
				String[] usageInStorages = new String[rowsCount];
				for (int i = 0; i < rowsCount; i++) {
					List<Table> allTablesUsingRow = jDao.getTablesUsingRow(productId, table, i + 1);
					usageInTables[i] = getTestTableOccurences(allTablesUsingRow);
					usageInStorages[i] = getDataStorageOccurences(allTablesUsingRow);
				}
				uiRowsUsage.setTables(usageInTables);
				uiRowsUsage.setStorages(usageInStorages);				
			} else {
				// TODO: implement PostgreSQL part.
				pDao = new PostgresDao();
			}

			out.println(new Gson().toJson(uiRowsUsage));

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
