package org.grible.servlets.firstlaunch;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.Dao;
import org.grible.model.User;
import org.grible.settings.GlobalSettings;

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
			String gribleLogin = request.getParameter("griblelogin");
			String griblePswd = request.getParameter("griblepswd");

			

			User user = Dao.getUserByName(gribleLogin);
			if (user == null) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(griblePswd.getBytes());
				String hashPass = new String(md.digest());
				int userId = Dao.insertUser(gribleLogin, hashPass, true, false);
				if (userId > 0) {
					out.print("Done.");
				} else {
					out.print("ERROR: It seemed like Grible admin was inserted, but now we cannot find him.");
				}
			} else {
				if (!griblePswd.equals("")) {
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(griblePswd.getBytes());
					String hashPass = new String(md.digest());
					Dao.updateUserPassword(user.getId(), hashPass);
					Dao.updateUserIsAdmin(user.getId(), true);
					out.print("Done.");
				} else {
					out.print("ERROR: Password for Grible administrator cannot be empty.");
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