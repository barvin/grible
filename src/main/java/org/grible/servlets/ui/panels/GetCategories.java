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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Category;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetCategories")
public class GetCategories extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private int productId;

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

			productId = Integer.parseInt(request.getParameter("productId"));
			int tableId = Integer.parseInt(request.getParameter("tableId"));
			String strTableType = request.getParameter("tableType");

			TableType tableType = TableType.valueOf(strTableType.toUpperCase());
			if (tableType == TableType.PRECONDITION || tableType == TableType.POSTCONDITION) {
				tableType = TableType.TABLE;
				if (isJson()) {
					tableId = new JsonDao().getParentTableId(tableId, productId, tableType);
				} else {
					tableId = new PostgresDao().getTable(tableId).getParentId();
				}
			}
			List<Category> categories = DataManager.getInstance().getDao().getTopLevelCategories(productId, tableType);
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

	private void appendCategory(int tableId, StringBuilder responseHtml, Category category) throws Exception {
		String categorySelectedClass = "";
		if (tableId > 0) {
			if (isOneOfParentCategoriesForTable(tableId, category)) {
				categorySelectedClass = " category-item-selected";
			}
		}

		responseHtml.append("<h3 id=\"").append(category.getId()).append("\" class=\"category-item");
		responseHtml.append(categorySelectedClass).append("\">");
		responseHtml.append("<span class=\"tree-item-text\">&nbsp;");
		responseHtml.append(category.getName()).append("</span></h3>");
		responseHtml.append("<div class=\"category-content-holder\">");

		List<Category> childCategories = DataManager.getInstance().getDao().getChildCategories(category);
		if (!childCategories.isEmpty()) {
			responseHtml.append("<div class=\"categories\">");
			for (Category childCategory : childCategories) {
				appendCategory(tableId, responseHtml, childCategory);
			}
			responseHtml.append("</div>");
		}

		List<Table> tables = DataManager.getInstance().getDao().getTablesByCategory(category);
		for (Table table : tables) {
			String selected = (table.getId() == tableId) ? " data-item-selected" : "";
			responseHtml.append("<div id=\"").append(table.getId()).append("\" class=\"data-item");
			responseHtml.append(selected).append("\">");
			responseHtml.append("<span class=\"tree-item-text\">&nbsp;");
			responseHtml.append(table.getName()).append("</span></div>");
		}
		responseHtml.append("</div>");
	}

	private boolean isOneOfParentCategoriesForTable(int tableId, Category category) throws Exception {
		if (isJson()) {
			Table table = new JsonDao().getTable(tableId, productId);
			Product product = DataManager.getInstance().getDao().getProduct(productId);
			String tableFilePath = StringUtils.substringAfter(table.getFile().getAbsolutePath(), product.getPath()
					+ File.separator + category.getType().getSection().getDirName() + File.separator);
			return tableFilePath.startsWith(category.getPath());
		}
		int parentCategoryId = new PostgresDao().getTable(tableId).getCategoryId();
		if (parentCategoryId == category.getId()) {
			return true;
		} else {
			Category currentCategory = DataManager.getInstance().getDao().getCategory(parentCategoryId);
			while (currentCategory.getParentId() > 0) {
				parentCategoryId = currentCategory.getParentId();
				if (parentCategoryId == category.getId()) {
					return true;
				}
				currentCategory = DataManager.getInstance().getDao().getCategory(parentCategoryId);
			}
		}
		return false;
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}
}
