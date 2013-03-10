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
package org.pine.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Category;
import org.pine.model.Table;
import org.pine.model.TableType;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetEditTableDialog")
public class GetEditTableDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetEditTableDialog() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Dao dao = new Dao();
			int id = Integer.parseInt(request.getParameter("id"));
			
			Table table = dao.getTable(id);
			if ((table.getType() == TableType.TABLE) || (table.getType() == TableType.STORAGE)) {
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				String name = table.getName();
				int categoryId = table.getCategoryId();

				out.println("<div id=\"edit-table-dialog\" class=\"ui-dialog\">");
				out.println("<div class=\"ui-dialog-title\">Edit data " + table.getType().toString().toLowerCase()
						+ "</div>");
				out.println("<div class=\"ui-dialog-content\">");
				out.println("<div class=\"table\">");
				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell\">Name:</div>");
				out.println("<div class=\"table-cell dialog-cell\"><input class=\"data-item-name dialog-edit\" value=\""
						+ name + "\"></div>");
				out.println("</div>");

				if (table.getType() == TableType.STORAGE) {
					out.println("<div class=\"table-row\">");
					out.println("<div class=\"table-cell dialog-cell\">Class name:</div>");
					out.println("<div class=\"table-cell dialog-cell\"><input class=\"data-storage-class-name dialog-edit\" value=\""
							+ table.getClassName() + "\"></div>");
					out.println("</div>");
				}

				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell\">Category:</div>");
				out.println("<div class=\"table-cell dialog-cell\">");
				out.println("<select class=\"categories\" \">");

				List<Category> categories = dao.getCategories(dao.getCategory(categoryId).getProductId(),
						table.getType());
				for (Category category : categories) {
					String selected = "";
					if (categoryId == category.getId()) {
						selected = "selected=\"selected\" ";
					}
					out.print("<option value=\"" + category.getId() + "\" " + selected + ">" + category.getName()
							+ "</option>");
				}

				out.print("</select></div>");
				out.println("</div>");

				if (table.getType() == TableType.STORAGE) {
					out.println("<div class=\"table-row\">");
					out.println("<div class=\"table-cell dialog-cell\">Show rows usage:</div>");
					String usage = "";
					if (table.isShowUsage()) {
						usage = "checked=\"checked\"";
					}
					out.println("<div class=\"table-cell dialog-cell\"><input class=\"usage\" type=\"checkbox\" "
							+ usage + "></div>");
					out.println("</div>");
				}
				out.println("</div>");
				out.println("<div class=\"dialog-buttons right\">");
				out.println("<button id=\"dialog-btn-edit-data-item\" class=\"ui-button\">Save</button> ");
				out.println("<button class=\"ui-button btn-cancel\">Cancel</button> ");
				out.println("</div></div></div>");

				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
