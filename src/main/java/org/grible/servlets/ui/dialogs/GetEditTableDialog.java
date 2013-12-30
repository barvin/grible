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
package org.grible.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.grible.data.Dao;
import org.grible.model.Category;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;

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
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			int id = Integer.parseInt(request.getParameter("id"));

			Table table = Dao.getTable(id);
			if ((table.getType() == TableType.TABLE) || (table.getType() == TableType.STORAGE)
					|| (table.getType() == TableType.ENUMERATION)) {
				String name = table.getName();
				int categoryId = table.getCategoryId();

				out.println("<div id=\"edit-table-dialog\" class=\"ui-dialog\">");
				out.println("<div class=\"ui-dialog-title\">Edit " + table.getType().toString().toLowerCase()
						+ "</div>");
				out.println("<div class=\"ui-dialog-content\">");
				out.println("<div class=\"table\">");
				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell dialog-label\">Name:</div>");
				out.println("<div class=\"table-cell dialog-cell dialog-edit\"><input class=\"data-item-name dialog-edit\" value=\""
						+ name + "\"></div>");
				out.println("</div>");

				if (table.getType() == TableType.STORAGE) {
					out.println("<div class=\"table-row\">");
					out.println("<div class=\"table-cell dialog-cell dialog-label\">Class name:</div>");
					out.println("<div class=\"table-cell dialog-cell dialog-edit\"><input class=\"data-storage-class-name dialog-edit\" value=\""
							+ table.getClassName() + "\"></div>");
					out.println("</div>");
				}

				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell dialog-label\">Category:</div>");
				out.println("<div class=\"table-cell dialog-cell\">");
				out.println("<select class=\"categories dialog-edit\" \">");

				List<Category> categories = Dao.getAllCategories(Dao.getCategory(categoryId).getProductId(),
						table.getType());
				for (Category category : categories) {
					category.setName(addParents(category));
				}
				Collections.sort(categories);
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

				out.println("</div>");
				out.println("<div class=\"dialog-buttons right\">");
				out.println("<button id=\"dialog-btn-edit-data-item\" class=\"ui-button\">Save</button> ");
				out.println("<button class=\"ui-button btn-cancel\">Cancel</button> ");
				out.println("</div></div></div>");
			}
		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private String addParents(Category category) throws Exception {
		Category currCategory = category;
		String fullPath = category.getName();
		while (currCategory.getParentId() != 0) {
			currCategory = Dao.getCategory(currCategory.getParentId());
			fullPath = StringUtils.join(new Object[] { currCategory.getName(), "/", fullPath });
		}
		return fullPath;
	}
}
