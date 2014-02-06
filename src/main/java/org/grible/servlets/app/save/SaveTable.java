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

import org.grible.dao.JsonDao;
import org.grible.model.Table;
import org.grible.model.json.KeyJson;
import org.grible.security.Security;

import com.google.gson.Gson;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/SaveTable")
public class SaveTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveTable() {
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
			Table table = new JsonDao().getTable(tableId, productId);

			String[] keyName = request.getParameterValues("keys[]");
			KeyJson[] keys = table.getTableJson().getKeys();
			for (int i = 0; i < keys.length; i++) {
				keys[i].setName(keyName[i]);
			}
			table.getTableJson().setKeys(keys);

			String[] valueRows = request.getParameterValues("values[]");
			String[][] values = new String[valueRows.length][keys.length];

			Gson gson = new Gson();
			for (int i = 0; i < values.length; i++) {
				String[] row = gson.fromJson(valueRows[i], String[].class);
				for (int j = 0; j < row.length; j++) {
					String value = row[j];
					
					values[i][j] = value;
				}
			}
			
			table.getTableJson().setValues(values);

			table.save();
			out.print("success");

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
