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
package org.grible.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.Dao;
import org.grible.model.Table;

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
			int id = Integer.parseInt(request.getParameter("id"));
			Table table = Dao.getTable(id);
			int categoryId = Integer.parseInt(request.getParameter("categoryid"));
			String name = request.getParameter("name");
			String className = request.getParameter("classname");

			if ("".equals(name)) {
				out.print("ERROR: Name cannot be empty.");
			} else {
				table.setCategoryId(categoryId);
				table.setName(name);
				table.setClassName(className);
				Dao.updateTable(table);
				out.print("success");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}
}
