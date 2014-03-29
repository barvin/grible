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

import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/PingModifiedTime")
public class PingModifiedTime extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PostgresDao pDao;

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
			pDao = new PostgresDao();

			int id = Integer.parseInt(request.getParameter("id"));
			Date userStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(request.getParameter("time"));
			Table table = pDao.getTable(id);

			String message = "";

			if (table.getModifiedTime().after(userStartTime)) {
				message = "This table was modified by another user. Please, refresh the page.";
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
