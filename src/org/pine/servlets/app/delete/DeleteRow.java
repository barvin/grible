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
package org.pine.servlets.app.delete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Row;
import org.pine.model.Table;
import org.pine.model.TableType;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			Dao dao = new Dao();
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
						error += "\n- " + tableName + " (" + table.getType().toString().toLowerCase() + ");";
					}
				}
			}
			if (isUsedByTables) {
				out.print(error);
			} else {
				if (dao.deleteRow(rowId)) {
					List<Integer> rowIds = new ArrayList<>();
					List<Integer> oldRowNumbers = new ArrayList<>();
					List<Integer> rowNumbers = new ArrayList<>();
					List<Row> rows = dao.getRows(tableId);
					for (int i = 0; i < rows.size(); i++) {
						rowIds.add(rows.get(i).getId());
						oldRowNumbers.add(rows.get(i).getOrder());
						rowNumbers.add(i + 1);
					}
					dao.updateRows(rowIds, oldRowNumbers, rowNumbers);
					out.print("success");
				} else {
					out.print("Could not delete the row. See server log for details.");
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
