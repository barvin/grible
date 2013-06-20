package org.pine.servlets.firstlaunch;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.User;
import org.pine.settings.GlobalSettings;

/**
 * Servlet implementation class CreateAdmin
 */
@WebServlet("/CreateAdmin")
public class CreateAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateAdmin() {
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
			String pineLogin = request.getParameter("pinelogin");
			String pinePswd = request.getParameter("pinepswd");

			

			User user = Dao.getUserByName(pineLogin);
			if (user == null) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(pinePswd.getBytes());
				String hashPass = new String(md.digest());
				int userId = Dao.insertUser(pineLogin, hashPass, true, false);
				if (userId > 0) {
					out.print("Done.");
				} else {
					out.print("ERROR: It seemed like Pine admin was inserted, but now we cannot find him.");
				}
			} else {
				if (!pinePswd.equals("")) {
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(pinePswd.getBytes());
					String hashPass = new String(md.digest());
					Dao.updateUserPassword(user.getId(), hashPass);
					Dao.updateUserIsAdmin(user.getId(), true);
					out.print("Done.");
				} else {
					out.print("ERROR: Password for Pine administrator cannot be empty.");
				}
			}
		} catch (Exception e) {
			try {
				GlobalSettings.getInstance().eraseDbSettings();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
}