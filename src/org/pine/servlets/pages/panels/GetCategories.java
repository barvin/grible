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
package org.pine.servlets.pages.panels;

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
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			int productId = Integer.parseInt(request.getParameter("productId"));
			int tableId = Integer.parseInt(request.getParameter("tableId"));
			String strTableType = request.getParameter("tableType");
			Dao dao = new Dao();

			TableType tableType = TableType.valueOf(strTableType.toUpperCase());
			if (tableType == TableType.PRECONDITION || tableType == TableType.POSTCONDITION) {
				tableType = TableType.TABLE;
				tableId = dao.getTable(tableId).getParentId();
			}
			List<Category> categories = dao.getCategories(productId, tableType);
			for (Category category : categories) {
				List<Table> tables = dao.getTablesByCategoryId(category.getId());
				boolean categorySelected = false;
				for (Table table : tables) {
					if (table.getId() == tableId) {
						categorySelected = true;
						break;
					}
				}
				String categorySelectedClass = "";
				if (categorySelected) {
					categorySelectedClass = " category-item-selected";
				}
				out.print("<h3 id=\"" + category.getId() + "\" class=\"category-item" + categorySelectedClass + "\">"
						+ category.getName() + "</h3><div>");
				for (Table table : tables) {
					String selected = (table.getId() == tableId) ? " data-item-selected" : "";
					out.print("<div id=\"" + table.getId() + "\" class=\"data-item" + selected + "\">"
							+ table.getName() + "</div>");
				}
				out.print("</div>");
			}

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
