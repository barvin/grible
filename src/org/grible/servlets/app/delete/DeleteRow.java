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

import org.grible.dao.Dao;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;

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
			int rowId = Integer.parseInt(request.getParameter("rowid"));
			Row row = Dao.getRow(rowId);
			int tableId = row.getTableId();
			Table currentTable = Dao.getTable(tableId);

			boolean isUsedByTables = false;
			String error = "";
			if (currentTable.getType() == TableType.STORAGE) {
				List<Table> tablesUsingRow = Dao.getTablesUsingRow(rowId);
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
				out.print(error);
			} else {
				if (Dao.deleteRow(rowId)) {
					List<Integer> rowIds = new ArrayList<Integer>();
					List<Integer> oldRowNumbers = new ArrayList<Integer>();
					List<Integer> rowNumbers = new ArrayList<Integer>();
					List<Row> rows = Dao.getRows(tableId);
					for (int i = 0; i < rows.size(); i++) {
						rowIds.add(rows.get(i).getId());
						oldRowNumbers.add(rows.get(i).getOrder());
						rowNumbers.add(i + 1);
					}
					Dao.updateRows(rowIds, oldRowNumbers, rowNumbers);
					out.print("success");
				} else {
					out.print("Could not delete the row. See server log for details.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}
}
