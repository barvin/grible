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

import org.grible.dao.Dao;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/UpdateKeysOrder")
public class UpdateKeysOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateKeysOrder() {
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
			if (request.getParameterValues("modkeyids[]") != null) {
				String[] strKeyIds = request.getParameterValues("modkeyids[]");
				String[] strKeyNumbers = request.getParameterValues("modkeynumbers[]");
				List<Integer> keyIds = new ArrayList<Integer>();
				List<Integer> keyNumbers = new ArrayList<Integer>();
				for (int i = 0; i < strKeyIds.length; i++) {
					keyIds.add(Integer.parseInt(strKeyIds[i]));
					keyNumbers.add(Integer.parseInt(strKeyNumbers[i]));
				}
				Dao.updateKeys(keyIds, keyNumbers);
			}
			out.print("success");

		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
