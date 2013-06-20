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
package org.pine.servlets.users;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddUser")
public class AddUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddUser() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String userName = request.getParameter("username");
		String strPass = request.getParameter("pass");
		boolean isAdmin = Boolean.parseBoolean(request.getParameter("isadmin"));

		if (!strPass.equals("password")) {
			MessageDigest md;
			try {

				md = MessageDigest.getInstance("MD5");
				md.update(strPass.getBytes());
				String hashPass = new String(md.digest());
				int userId = Dao.insertUser(userName, hashPass, isAdmin, false);
				if (userId > 0) {
					if ((request.getParameterValues("productIds[]") != null) && (!isAdmin)) {
						String[] productIds = request.getParameterValues("productIds[]");
						Dao.insertUserPermissions(userId, productIds);
					}
					out.print("success");
				} else {
					out.print("ERROR: User was not added. See server logs for details.");
				}
			} catch (Exception e) {
				out.print(e.getLocalizedMessage());
				e.printStackTrace();
			}
		} else {
			out.print("ERROR: Password 'password' is not permitted. It is too obvious.");
		}
		out.flush();
		out.close();
	}
}
