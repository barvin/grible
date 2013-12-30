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
package org.grible.servlets.ui.panels;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.data.Dao;
import org.grible.model.Category;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetCategories")
public class GetCategories extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetCategories() {
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
			
			int productId = Integer.parseInt(request.getParameter("productId"));
			int tableId = Integer.parseInt(request.getParameter("tableId"));
			String strTableType = request.getParameter("tableType");

			TableType tableType = TableType.valueOf(strTableType.toUpperCase());
			if (tableType == TableType.PRECONDITION || tableType == TableType.POSTCONDITION) {
				tableType = TableType.TABLE;
				tableId = Dao.getTable(tableId).getParentId();
			}
			List<Category> categories = Dao.getTopLevelCategories(productId, tableType);
			StringBuilder responseHtml = new StringBuilder();
			for (Category category : categories) {
				appendCategory(tableId, responseHtml, category);
			}
			out.print(responseHtml.toString());
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void appendCategory(int tableId, StringBuilder responseHtml, Category category) throws SQLException {
		String categorySelectedClass = "";
		if (tableId > 0) {
			if (isOneOfParentCategoriesForTable(tableId, category.getId())) {
				categorySelectedClass = " category-item-selected";
			}
		}

		responseHtml.append("<h3 id=\"").append(category.getId()).append("\" class=\"category-item");
		responseHtml.append(categorySelectedClass).append("\">");
		responseHtml.append("<span class=\"tree-item-text\">&nbsp;");
		responseHtml.append(category.getName()).append("</span></h3>");
		responseHtml.append("<div class=\"category-content-holder\">");

		List<Category> childCategories = Dao.getChildCategories(category.getId());
		if (!childCategories.isEmpty()) {
			responseHtml.append("<div class=\"categories\">");
			for (Category childCategory : childCategories) {
				appendCategory(tableId, responseHtml, childCategory);
			}
			responseHtml.append("</div>");
		}

		List<Table> tables = Dao.getTablesByCategoryId(category.getId());
		for (Table table : tables) {
			String selected = (table.getId() == tableId) ? " data-item-selected" : "";
			responseHtml.append("<div id=\"").append(table.getId()).append("\" class=\"data-item");
			responseHtml.append(selected).append("\">");
			responseHtml.append("<span class=\"tree-item-text\">&nbsp;");
			responseHtml.append(table.getName()).append("</span></div>");
		}
		responseHtml.append("</div>");
	}

	private boolean isOneOfParentCategoriesForTable(int tableId, int categoryId) throws SQLException {
		int parentCategoryId = Dao.getTable(tableId).getCategoryId();
		if (parentCategoryId == categoryId) {
			return true;
		} else {
			Category currentCategory = Dao.getCategory(parentCategoryId);
			while (currentCategory.getParentId() > 0) {
				parentCategoryId = currentCategory.getParentId();
				if (parentCategoryId == categoryId) {
					return true;
				}
				currentCategory = Dao.getCategory(parentCategoryId);
			}
		}
		return false;
	}
}
