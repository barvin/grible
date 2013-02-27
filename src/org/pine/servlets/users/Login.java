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

import org.pine.model.users.User;
import org.pine.sql.SQLHelper;


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
		SQLHelper sqlHelper = new SQLHelper();

		String userName = request.getParameter("username");
		String actualPass = request.getParameter("pass");

		User user = sqlHelper.getUserByName(userName);
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
