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
package org.pine.servlets.create;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Row;
import org.pine.model.TableType;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddTable")
public class AddTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddTable() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			Integer categoryId = null;
			if (request.getParameter("categoryid") != null) {
				categoryId = Integer.parseInt(request.getParameter("categoryid"));
			}
			Integer parentId = null;
			if (request.getParameter("parentid") != null) {
				parentId = Integer.parseInt(request.getParameter("parentid"));
			}
			String name = request.getParameter("name");
			TableType type = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
			String className = request.getParameter("classname");
			Dao dao = new Dao();

			int tableId;
			tableId = dao.insertTable(name, type, categoryId, parentId, className);
			List<String> keys = new ArrayList<>();
			keys.add("editme");
			int keyId = dao.insertKeys(tableId, keys).get(0);
			dao.insertRow(tableId, 1);
			List<Row> rows = dao.getRows(tableId);
			dao.insertValuesEmptyWithKeyId(keyId, rows);
			out.print(tableId);
			out.flush();
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
