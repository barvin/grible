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
package org.grible.servlets.app.delete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteRow")
public class DeleteRow extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteRow() {
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
			String result = null;

			if (ServletHelper.isJson()) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				int productId = Integer.parseInt(request.getParameter("product"));
				int rowOrder = Integer.parseInt(request.getParameter("roworder")) - 1;

				JsonDao dao = new JsonDao();
				Table table = dao.getTable(tableId, productId);

				boolean isUsedByTables = false;
				String error = "";
				if (table.getType() == TableType.STORAGE) {
					List<Table> tablesUsingRow = dao.getTablesUsingRow(productId, table, rowOrder + 1);
					if (!tablesUsingRow.isEmpty()) {
						isUsedByTables = true;
						error = "ERROR: This row is used by:";
						String tableName = "";
						for (Table t : tablesUsingRow) {
							if (t.getName() != null) {
								tableName = t.getName();
							} else {
								tableName = "-";
							}
							error += "<br>- " + tableName + " (" + t.getType().toString().toLowerCase() + ");";
						}
					}
				}

				if (isUsedByTables) {
					result = error;
				} else {
					String[][] values = table.getTableJson().getValues();
					String[][] newValues = new String[values.length - 1][values[0].length];
					for (int i = newValues.length - 1; i >= 0; i--) {
						if (i >= rowOrder) {
							for (int j = 0; j < newValues[0].length; j++) {
								newValues[i][j] = values[i + 1][j];
							}
						} else {
							for (int j = 0; j < newValues[0].length; j++) {
								newValues[i][j] = values[i][j];
							}
						}
					}
					table.getTableJson().setValues(newValues);
					table.save();
					result = "success";
				}
			} else {
				PostgresDao dao = new PostgresDao();
				int rowId = Integer.parseInt(request.getParameter("rowid"));
				Row row = dao.getRow(rowId);
				int tableId = row.getTableId();
				Table currentTable = dao.getTable(tableId);

				boolean isUsedByTables = false;
				String error = "";
				if (currentTable.getType() == TableType.STORAGE) {
					List<Table> tablesUsingRow = dao.getTablesUsingRow(rowId);
					if (!tablesUsingRow.isEmpty()) {
						isUsedByTables = true;
						error = "ERROR: This row is used by:";
						String tableName = "";
						for (Table table : tablesUsingRow) {
							if (table.getName() != null) {
								tableName = table.getName();
							} else {
								tableName = "-";
							}
							error += "<br>- " + tableName + " (" + table.getType().toString().toLowerCase() + ");";
						}
					}
				}
				if (isUsedByTables) {
					result = error;
				} else {
					if (dao.deleteRow(rowId)) {
						List<Integer> rowIds = new ArrayList<Integer>();
						List<Integer> oldRowNumbers = new ArrayList<Integer>();
						List<Integer> rowNumbers = new ArrayList<Integer>();
						List<Row> rows = dao.getRows(tableId);
						for (int i = 0; i < rows.size(); i++) {
							rowIds.add(rows.get(i).getId());
							oldRowNumbers.add(rows.get(i).getOrder());
							rowNumbers.add(i + 1);
						}
						dao.updateRows(rowIds, oldRowNumbers, rowNumbers);
						result = "success";
					} else {
						result = "Could not delete the row. See server log for details.";
					}
				}
			}
			out.print(result);
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}
}
