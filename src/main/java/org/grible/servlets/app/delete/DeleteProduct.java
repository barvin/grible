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
import org.grible.model.Category;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteProduct")
public class DeleteProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteProduct() {
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
			int productId = Integer.parseInt(request.getParameter("id"));

			if (GlobalSettings.getInstance().getAppType() == AppTypes.POSTGRESQL) {
				deleteCategories(out, productId, TableType.TABLE);
				deleteCategories(out, productId, TableType.STORAGE);
				deleteCategories(out, productId, TableType.ENUMERATION);
			}

			boolean deleted = DataManager.getInstance().getDao().deleteProduct(productId);
			if (deleted) {
				out.print("success");
			} else {
				out.print(Lang.get("error") + ": " + Lang.get("productnotdeleted"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}

	public void deleteCategories(PrintWriter out, int productId, TableType tableType) throws Exception {
		List<Category> categories = DataManager.getInstance().getDao().getAllCategories(productId, tableType);
		for (Category category : categories) {
			List<Table> tables = DataManager.getInstance().getDao().getTablesByCategory(category);

			StringBuilder error = new StringBuilder();
			for (Table table : tables) {
				boolean deleted = DataManager.getInstance().getDao().deleteTable(table, productId);
				if (!deleted) {
					out.print(Lang.get("error") + ": " + Lang.get("tablenotdeleted"));
				}
			}

			if (error.length() > 0) {
				out.print(error.toString());
			} else {
				boolean deleted = DataManager.getInstance().getDao().deleteCategory(category);
				if (!deleted) {
					out.print(Lang.get("error") + ": " + Lang.get("categorynotdeleted"));
				}
			}
		}
	}
}
