package org.pine.servlets.firstlaunch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
				String query = getSQLQuery("/WEB-INF/sql/pine_init.sql");
				String querySetSeqVal = getSQLQuery("/WEB-INF/sql/pine_setseqval.sql");
				dao.execute(query);
				dao.executeSelect(querySetSeqVal);
			} else {
				// TODO: validate existing database.
			}
			out.print("Done.");
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

	private String getSQLQuery(String filePath) throws Exception {
		FileReader fr = new FileReader(new File(getServletContext().getRealPath("") + filePath));
		List<String> lines = IOUtils.readLines(fr);
		StringBuilder content = new StringBuilder();

		for (String line : lines) {
			content.append(line.replace("postgres", GlobalSettings.getInstance().getDbLogin()));
			content.append("\n");
		}
		return content.toString();
	}
}
