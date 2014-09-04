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
package org.grible.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetFilterDialog")
public class GetFilterDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetFilterDialog() {
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

			int storageId = Integer.parseInt(request.getParameter("tableid"));
			int productId = Integer.parseInt(request.getParameter("product"));

			StringBuilder responseHtml = new StringBuilder();
			responseHtml.append("<div id=\"filter-dialog\" class=\"ui-dialog\">");
			responseHtml.append("<div class=\"ui-dialog-title\">Filter storage</div>");
			responseHtml.append("<div class=\"ui-dialog-content\">");
			responseHtml.append("Filter storage by:<br><br>");
			responseHtml.append("<div class=\"table\">");

			responseHtml.append("<div class=\"table-row\">");
			responseHtml.append("<div class=\"table-cell dialog-cell dialog-label\">");
			responseHtml.append("<input name=\"table-type\" type=\"radio\" value=\"table\"> table:</div>");
			responseHtml.append("<div class=\"table-cell dialog-cell\">");
			responseHtml.append("<select class=\"tables-list dialog-edit\"\">");

			List<Table> tables = DataManager.getInstance().getDao().getTablesUsingStorage(storageId, productId, TableType.TABLE);
			for (Table table : tables) {
				responseHtml.append("<option value=\"");
				responseHtml.append(table.getId());
				responseHtml.append("\">");
				responseHtml.append(table.getName());
				responseHtml.append("</option>");
			}
			responseHtml.append("</select></div>");
			responseHtml.append("</div>");

			responseHtml.append("<div class=\"table-row\">");
			responseHtml.append("<div class=\"table-cell dialog-cell dialog-label\">");
			responseHtml.append("<input name=\"table-type\" type=\"radio\" value=\"storage\"> storage:</div>");
			responseHtml.append("<div class=\"table-cell dialog-cell\">");
			responseHtml.append("<select class=\"storage-list dialog-edit\"\">");

			tables = DataManager.getInstance().getDao().getTablesUsingStorage(storageId, productId, TableType.STORAGE);
			for (Table table : tables) {
				responseHtml.append("<option value=\"");
				responseHtml.append(table.getId());
				responseHtml.append("\">");
				responseHtml.append(table.getName());
				responseHtml.append("</option>");
			}
			responseHtml.append("</select></div>");
			responseHtml.append("</div>");

			responseHtml.append("</div>");
			responseHtml.append("<div class=\"dialog-buttons right\">");
			responseHtml.append("<button id=\"dialog-btn-filter\" class=\"ui-button\">Filter</button> "
					+ "<button class=\"ui-button btn-cancel\">Cancel</button>");
			responseHtml.append("</div></div></div>");

			out.print(responseHtml.toString());
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		} finally {
			out.flush();
			out.close();
		}
	}
}
