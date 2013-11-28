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
package org.grible.servlets.users;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.Dao;
import org.grible.model.User;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/UpdateUser")
public class UpdateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateUser() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			String userId = request.getParameter("userid");
			String userName = request.getParameter("username");
			String strPass = request.getParameter("pass");
			boolean isAdmin = Boolean.parseBoolean(request.getParameter("isadmin"));

			boolean noErrors = true;

			User user;
			user = Dao.getUserById(Integer.parseInt(userId));

			if (!user.getName().equals(userName)) {
				Dao.updateUserName(user.getId(), userName);
			}

			if (!strPass.equals("")) {
				if (!strPass.equals("password")) {
					MessageDigest md;
					try {
						md = MessageDigest.getInstance("MD5");
						md.update(strPass.getBytes());
						String hashPass = new String(md.digest());
						Dao.updateUserPassword(user.getId(), hashPass);
					} catch (NoSuchAlgorithmException e) {
						out.print("ERROR: " + e.getMessage());
						noErrors = false;
						e.printStackTrace();
					}
				} else {
					out.print("ERROR: Password 'password' is not permitted. It is too obvious.");
					noErrors = false;
				}
			}

			if (user.isAdmin() != isAdmin) {
				Dao.updateUserIsAdmin(user.getId(), isAdmin);
			}

			if ((request.getParameterValues("productIds[]") != null) && (!isAdmin)) {
				String[] productIds = request.getParameterValues("productIds[]");
				Dao.updateUserPermissions(user.getId(), productIds);
			}
			if (noErrors) {
				out.print("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
