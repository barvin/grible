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
package org.grible.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Key;
import org.grible.model.Table;
import org.grible.model.Value;
import org.grible.model.json.KeyJson;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetEnumValues")
public class GetEnumValues extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

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

			if (ServletHelper.isJson()) {
				int keyOrder = Integer.parseInt(request.getParameter("keyid"));
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				int productId = Integer.parseInt(request.getParameter("product"));
				String content = request.getParameter("content");

				jDao = new JsonDao();
				Table table = jDao.getTable(tableId, productId);
				KeyJson key = table.getTableJson().getKeys()[keyOrder - 1];

				Table refTable = jDao.getTable(key.getRefid(), productId);
				List<String> enumValues = jDao.getValuesByKeyOrder(refTable, 0);

				out.println("<select class=\"enum-values\">");
				for (String enumeValue : enumValues) {
					String selected = "";
					if (enumeValue.equals(content)) {
						selected = "selected=\"selected\" ";
					}
					out.println("<option " + selected + ">" + enumeValue + "</option>");
				}
				out.println("</select>");
			} else {
				Key key = pDao.getKey(Integer.parseInt(request.getParameter("keyid")));
				String content = request.getParameter("content");

				out.println("<select class=\"enum-values\">");
				pDao = new PostgresDao();
				Key enumKey = pDao
						.getKeys(new PostgresDao().getTable(key.getReferenceTableId()).getId()).get(0);
				List<Value> enumValues = pDao.getValues(enumKey);
				for (Value enumeValue : enumValues) {
					String selected = "";
					if (enumeValue.getValue().equals(content)) {
						selected = "selected=\"selected\" ";
					}
					out.println("<option " + selected + ">" + enumeValue.getValue() + "</option>");
				}
				out.println("</select>");
			}

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

}
