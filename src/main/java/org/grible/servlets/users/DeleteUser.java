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
package org.grible.servlets.users;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.PostgresDao;
import org.grible.model.User;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteUser")
public class DeleteUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteUser() {
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

			String userId = request.getParameter("userid");
			PostgresDao dao = new PostgresDao();
			User currUser = dao.getUserById(Integer.parseInt(userId));

			boolean deletingSelf = false;
			if (request.getSession(false).getAttribute("userName").equals(currUser.getName())) {
				deletingSelf = true;
			}
			
			boolean isLastAdmin = dao.getAdminsCount() == 1;
			if (deletingSelf && isLastAdmin) {
				out.print("ERROR: You cannot delete yourself, because you are the last administator.");
			} else {
				boolean deleted = dao.deleteUser(userId);
				if (deleted) {
					if (deletingSelf) {
						out.print("gohome");	
					} else {
						out.print("success");
					}
				} else {
					out.print("ERROR: User was not deleted. See server logs for details.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		} finally {
			out.flush();
			out.close();
		}
	}
}
