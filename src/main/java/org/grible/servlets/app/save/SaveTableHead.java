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

import org.grible.model.Table;
import org.grible.model.json.Key;
import org.grible.model.json.KeyType;
import org.grible.security.Security;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/SaveTableHead")
public class SaveTableHead extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveTableHead() {
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
			Table table = new Table(0);

			String[] keyNames = request.getParameterValues("keys[]");
			String[] keyTypes = request.getParameterValues("keyTypes[]");
			String[] keyRefids = request.getParameterValues("keyRefids[]");
			String[] keyWidth = request.getParameterValues("keyWidth[]");
			Key[] keys = new Key[keyNames.length];

			for (int i = 0; i < keys.length; i++) {
				keys[i] = new Key(keyNames[i], KeyType.valueOf(keyTypes[i].toUpperCase()),
						Integer.parseInt(keyRefids[i]), Integer.parseInt(keyWidth[i]));
			}

			table.setKeys(keys);
			
			request.getSession().setAttribute("SaveTable", table);

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
