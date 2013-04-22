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
package org.pine.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Value;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			Key key = Dao.getKey(Integer.parseInt(request.getParameter("keyid")));
			String content = request.getParameter("content");

			out.println("<select class=\"enum-values\">");
			Key enumKey = Dao.getKeys(Dao.getTable(key.getReferenceTableId()).getId()).get(0);
			List<Value> enumValues = Dao.getValues(enumKey);
			for (Value enumeValue : enumValues) {
				String selected = "";
				if (enumeValue.getValue().equals(content)) {
					selected = "selected=\"selected\" ";
				}
				out.println("<option " + selected + ">" + enumeValue.getValue()
						+ "</option>");
			}
			out.println("</select>");
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

}
