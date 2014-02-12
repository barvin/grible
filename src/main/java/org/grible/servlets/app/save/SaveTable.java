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
package org.grible.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.JsonDao;
import org.grible.model.Table;
import org.grible.model.json.KeyJson;
import org.grible.model.json.KeyType;
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
			JsonDao dao = new JsonDao();
			Table table = dao.getTable(tableId, productId);

			String[] keyName = request.getParameterValues("keys[]");
			KeyJson[] keys = table.getTableJson().getKeys();
			String[] valueRows = request.getParameterValues("values[]");
			String[][] values = new String[valueRows.length][keys.length];

			Gson gson = new Gson();
			for (int i = 0; i < values.length; i++) {
				String[] row = gson.fromJson(valueRows[i], String[].class);
				for (int j = 0; j < row.length; j++) {
					String value = row[j];
					if (keys[j].getType() == KeyType.STORAGE) {
						String[] strIndexes = value.split(";");
						for (String index : strIndexes) {
							if (!StringUtils.isNumeric(index)) {
								throw new Exception("ERROR: One of indexes in the row " + (i + 1) + " is not numeric.");
							}
							if (!"0".equals(index)) {
								Table refTable = dao.getTable(keys[j].getRefid(), productId);
								String[][] refRows = refTable.getTableJson().getValues();
								if (refRows.length < Integer.parseInt(index)) {
									throw new Exception("ERROR: Data storage '" + refTable.getName()
											+ "' does not contain row number " + index
											+ ".<br>You specified it in row: " + (i + 1)
											+ ".<br>You must first create this row in the specified data storage.");
								}
							}
						}

					}
					values[i][j] = value;
				}
			}

			for (int i = 0; i < keys.length; i++) {
				keys[i].setName(keyName[i]);
			}

			table.getTableJson().setKeys(keys);
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
