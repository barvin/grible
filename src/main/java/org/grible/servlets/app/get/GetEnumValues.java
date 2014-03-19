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
package org.grible.servlets.app.get;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetEnumValues")
public class GetEnumValues extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetEnumValues() {
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

			int tableId = Integer.parseInt(request.getParameter("tableid"));
			int productId = Integer.parseInt(request.getParameter("product"));
			Table table = null;
			if (ServletHelper.isJson()) {
				JsonDao jDao = new JsonDao();
				table = jDao.getTable(tableId, productId);
			} else {
				PostgresDao pDao = new PostgresDao();
				table = pDao.getTable(tableId);
			}
			String[] options = DataManager.getInstance().getDao().getValuesByKeyOrder(table, 0).toArray(new String[0]);
			out.println(new Gson().toJson(options, String[].class));

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

}
