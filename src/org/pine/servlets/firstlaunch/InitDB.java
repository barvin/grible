package org.pine.servlets.firstlaunch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.settings.GlobalSettings;

/**
 * Servlet implementation class DBConnect
 */
@WebServlet("/InitDB")
public class InitDB extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InitDB() {
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
			boolean createNewDb = Boolean.parseBoolean(request.getParameter("createnew"));
			Dao dao = new Dao();
			if (createNewDb) {
				FileReader fr = new FileReader(new File(getServletContext().getRealPath("")
						+ "/WEB-INF/sql/pine_init.sql"));
				BufferedReader br = new BufferedReader(fr);
				StringBuilder content = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					content.append(line.replace("postgres", GlobalSettings.getInstance().getDbLogin()));
					content.append("\n");
					line = br.readLine();
				}
				br.close();
				dao.execute(content.toString());
			} else {
				// TODO: validate existing database.
			}
			out.print("Done.");
		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.flush();
			out.close();
		}
	}
}
