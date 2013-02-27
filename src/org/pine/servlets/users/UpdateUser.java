package org.pine.servlets.users;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.users.User;
import org.pine.sql.SQLHelper;


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
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		SQLHelper sqlHelper = new SQLHelper();

		String userId = request.getParameter("userid");
		String userName = request.getParameter("username");
		String strPass = request.getParameter("pass");
		boolean isAdmin = Boolean.parseBoolean(request.getParameter("isadmin"));

		User user = sqlHelper.getUserById(Integer.parseInt(userId));

		if (!user.getName().equals(userName)) {
			sqlHelper.updateUserName(user.getId(), userName);
		}

		if (!strPass.equals("")) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
				md.update(strPass.getBytes());
				String hashPass = new String(md.digest());
				sqlHelper.updateUserPassword(user.getId(), hashPass);
			} catch (NoSuchAlgorithmException e) {
				out.print("ERROR: " + e.getMessage());
				e.printStackTrace();
			}
		}

		if (user.isAdmin() != isAdmin) {
			sqlHelper.updateUserIsAdmin(user.getId(), isAdmin);
		}

		if ((request.getParameterValues("productIds[]") != null) && (!isAdmin)) {
			String[] productIds = request.getParameterValues("productIds[]");
			sqlHelper.updateUserPermissions(user.getId(), productIds);
		}
		out.print("success");
	}
}
