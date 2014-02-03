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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.PostgresDao;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.Value;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/PingModifiedTime")
public class PingModifiedTime extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PingModifiedTime() {
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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			int id = Integer.parseInt(request.getParameter("id"));
			Table table = new PostgresDao().getTable(id);

			String message = "";

			if (table.getType() == TableType.TABLE || table.getType() == TableType.STORAGE) {
				if (table.isShowWarning()) {
					List<String> strValues = new ArrayList<String>();
					List<Row> rows = DataManager.getInstance().getDao().getRows(id);
					for (Row row : rows) {
						strValues.add(getCombinedValues(row));
					}
					for (int i = 0; i < rows.size(); i++) {
						String currValue = strValues.remove(0);
						if (strValues.contains(currValue)) {
							int first = rows.get(i).getOrder();
							int second = first + 1 + strValues.indexOf(currValue);
							message += "|Duplicated rows detected: " + first + " and " + second + ".";
						}
					}
				}
			}
			if (!message.equals("")) {
				message = "true" + message;
			}
			out.print(message);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private String getCombinedValues(Row row) throws Exception {
		StringBuilder builder = new StringBuilder();
		List<Value> values = DataManager.getInstance().getDao().getValues(row);
		for (Value value : values) {
			builder.append(value.getValue());
		}
		return builder.toString();
	}
}
