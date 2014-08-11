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
package org.grible.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.helpers.StringHelper;
import org.grible.model.Table;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/EditTable")
public class EditTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditTable() {
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
			String newName = request.getParameter("name");
			String className = request.getParameter("classname");

			if ("".equals(newName)) {
				out.print(Lang.get("error") + ": " + Lang.get("nameempty"));
			} else if ("".equals(className)) {
				out.print(Lang.get("error") + ": " + Lang.get("classnameempty"));
			} else {
				int id = Integer.parseInt(request.getParameter("id"));
				Integer productId = null;

				Table table = null;
				if (isJson()) {
					productId = Integer.parseInt(request.getParameter("product"));
					JsonDao dao = new JsonDao();
					table = dao.getTable(id, productId);
					table.setClassName(className);
					dao.updateTable(table);

					String newCategoryPath = request.getParameter("categorypath");
					String categoryPath = StringHelper.getCategoryPathFromTable(table, productId, table.getType());
					String name = table.getName();

					if (!categoryPath.equals(newCategoryPath) || !name.equals(newName)) {
						dao.moveTableFile(table, productId, newCategoryPath, newName);
					}

				} else {
					PostgresDao dao = new PostgresDao();
					table = dao.getTable(id);
					int categoryId = Integer.parseInt(request.getParameter("categoryid"));
					table.setCategoryId(categoryId);
					table.setName(newName);
					table.setClassName(className);
					dao.updateTable(table);
				}

				out.print("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		} finally {
			out.flush();
			out.close();
		}
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}
}
