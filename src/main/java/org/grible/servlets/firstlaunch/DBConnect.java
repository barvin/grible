package org.grible.servlets.firstlaunch;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class DBConnect
 */
@WebServlet("/DBConnect")
public class DBConnect extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DBConnect() {
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
			initializeSQLDriver();
			String dbhost = request.getParameter("dbhost");
			String dbport = request.getParameter("dbport");
			String dbname = request.getParameter("dbname");
			String dblogin = request.getParameter("dblogin");
			String dbpswd = request.getParameter("dbpswd");
			Connection con = getConnection(dbhost, dbport, dbname, dblogin, dbpswd);
			if (con != null) {
				out.print("Done.");
				con.close();
				GlobalSettings.getInstance().setDbHost(dbhost);
				GlobalSettings.getInstance().setDbPort(dbport);
				GlobalSettings.getInstance().setDbName(dbname);
				GlobalSettings.getInstance().setDbLogin(dblogin);
				GlobalSettings.getInstance().setDbPswd(dbpswd);
			}
		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	private Connection getConnection(String dbhost, String dbport, String dbname, String dblogin, String dbpswd)
			throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:postgresql://" + dbhost + ":" + dbport + "/" + dbname,
				dblogin, dbpswd);
		return conn;
	}

	private void initializeSQLDriver() throws Exception {
		Class.forName("org.postgresql.Driver").newInstance();
	}

}
