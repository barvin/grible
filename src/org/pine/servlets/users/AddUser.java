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

import org.pine.sql.SQLHelper;


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
		SQLHelper sqlHelper = new SQLHelper();

		String userName = request.getParameter("username");
		String strPass = request.getParameter("pass");
		boolean isAdmin = Boolean.parseBoolean(request.getParameter("isadmin"));

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(strPass.getBytes());
			String hashPass = new String(md.digest());
			int userId = sqlHelper.insertUser(userName, hashPass, isAdmin);
			if (userId > 0) {
				if ((request.getParameterValues("productIds[]") != null) && (!isAdmin)) {
					String[] productIds = request.getParameterValues("productIds[]");
					sqlHelper.insertUserPermissions(userId, productIds);
				}
				out.print("success");
			} else {
				out.print("ERROR: User was not added. See server logs for details.");
			}
		} catch (NoSuchAlgorithmException e) {
			out.print("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
