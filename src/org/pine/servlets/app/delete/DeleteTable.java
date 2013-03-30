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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Table;
import org.pine.model.TableType;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteTable")
public class DeleteTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteTable() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			int tableId = Integer.parseInt(request.getParameter("id"));
			Table currentTable = Dao.getTable(tableId);

			boolean isUsedByTables = false;
			String error = "";
			if (currentTable.getType() == TableType.STORAGE) {
				List<Table> tablesUsingThisStorage = Dao.getTablesUsingStorage(tableId);

				if (!tablesUsingThisStorage.isEmpty()) {
					isUsedByTables = true;
				} else {
					error = "ERROR: This data storage is used by:";
					for (Table table : tablesUsingThisStorage) {
						error += "\n- " + table.getName() + " (" + table.getType().toString().toLowerCase() + ");";
					}
				}

			}
			if (isUsedByTables) {
				out.print(error);
			} else {
				boolean deleted = Dao.deleteTable(tableId);
				if (deleted) {
					switch (currentTable.getType()) {
					case TABLE:
					case STORAGE:
						out.print("success");
						break;

					case PRECONDITION:
					case POSTCONDITION:
						out.print(currentTable.getParentId());
						break;

					default:
						out.print("success");
						break;
					}
				} else {
					out.print("ERROR: " + currentTable.getType().toString().toLowerCase()
							+ " was not deleted. See server logs for details.");
				}
			}

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
