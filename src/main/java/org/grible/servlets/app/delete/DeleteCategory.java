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
package org.grible.servlets.app.delete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.PostgresDao;
import org.grible.helpers.StringHelper;
import org.grible.model.Category;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteCategory")
public class DeleteCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteCategory() {
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
			if (isJson()) {
				Integer productId = Integer.parseInt(request.getParameter("product"));
				TableType tableType = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
				if (tableType == TableType.PRECONDITION || tableType == TableType.POSTCONDITION) {
					tableType = TableType.TABLE;
				}
				String path = StringHelper.getFolderPathWithoutLastSeparator(request.getParameter("path"));
				category = new Category(path, tableType, productId);
			} else {
				int categoryId = Integer.parseInt(request.getParameter("id"));
				category = new PostgresDao().getCategory(categoryId);
			}

			List<Table> tables = DataManager.getInstance().getDao().getTablesByCategory(category);

			StringBuilder error = new StringBuilder();
			for (Table table : tables) {
				String result = ServletHelper.deleteTable(table.getId(), Integer.parseInt(request.getParameter("product")));
				if (!result.equals("success")) {
					error.append(result).append("<br>");
				}
			}

			if (error.length() > 0) {
				out.print(error.toString());
			} else {
				boolean deleted = DataManager.getInstance().getDao().deleteCategory(category);
				if (deleted) {
					out.print("success");
				} else {
					out.print("ERROR: Category was not deleted. See server logs for details.");
				}
			}

		} catch (Exception e) {
			out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}
}
