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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pine.dao.Dao;
import org.pine.model.User;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		String userName = request.getParameter("username");
		String actualPass = request.getParameter("pass");

		User user = null;
		try {
			
			user = Dao.getUserByName(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user != null) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
				md.update(actualPass.getBytes());
				String actualEncodedPass = new String(md.digest());
				if (user.getPassword().equals(actualEncodedPass)) {
					HttpSession session = request.getSession(true);
					session.setAttribute("userName", user.getName());
					session.setAttribute("loginFailed", null);
				} else {
					HttpSession session = request.getSession(false);
					session.setAttribute("loginFailed", "Incorrect password for user " + userName + ".");
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else {
			HttpSession session = request.getSession(false);
			session.setAttribute("loginFailed", "No user found with name '" + userName + "'.");
		}

		String path = "";
		if (request.getParameter("url") != null) {
			path = request.getParameter("url");
		}

		response.sendRedirect(path);
	}
}
