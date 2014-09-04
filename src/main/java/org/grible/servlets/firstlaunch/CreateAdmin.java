package org.grible.servlets.firstlaunch;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.PostgresDao;
import org.grible.model.User;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

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

			PostgresDao dao = new PostgresDao();
			User user = dao.getUserByName(gribleLogin);
			if (user == null) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(griblePswd.getBytes());
				String hashPass = new String(md.digest());
				int userId = dao.insertUser(gribleLogin, hashPass, true, false);
				if (userId > 0) {
					out.print("success");
				} else {
					out.print(Lang.get("error") + ": " + Lang.get("cannotfindadmin"));
				}
			} else {
				if (!griblePswd.equals("")) {
					MessageDigest md = MessageDigest.getInstance("MD5");
					md.update(griblePswd.getBytes());
					String hashPass = new String(md.digest());
					dao.updateUserPassword(user.getId(), hashPass);
					dao.updateUserIsAdmin(user.getId(), true);
					out.print("success");
				} else {
					out.print(Lang.get("error") + ": " + Lang.get("pswdempty"));
				}
			}
		} catch (Exception e) {
			try {
				GlobalSettings.getInstance().eraseDbSettings();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			out.print(Lang.get("error") + ": " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}
}