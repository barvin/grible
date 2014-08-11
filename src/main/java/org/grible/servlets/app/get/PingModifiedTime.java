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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;
import org.grible.settings.Lang;

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
			if (!ServletHelper.isJson()
					&& (request.getSession(false) == null || request.getSession(false).getAttribute("userName") == null)) {
				out.print("logged-out");
				return;
			}
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			int id = Integer.parseInt(request.getParameter("id"));
			int productId = Integer.parseInt(request.getParameter("product"));
			Date userStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(request.getParameter("time"));

			Table table = null;
			if (ServletHelper.isJson()) {
				table = new JsonDao().getTable(id, productId);
			} else {
				table = new PostgresDao().getTable(id);
			}

			String message = "";

			if (table.getModifiedTime().after(userStartTime)) {
				message = Lang.get("tablewasmodified");
			}
			out.print(message);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
