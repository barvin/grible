package org.grible.servlets.firstlaunch;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.PostgresDao;
import org.grible.dbmigrate.Migration;
import org.grible.dbmigrate.MigrationActions;
import org.grible.dbmigrate.Migrations;
import org.grible.settings.GlobalSettings;
import org.postgresql.util.PSQLException;

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
			PostgresDao dao = new PostgresDao();
			if (createNewDb) {
				migrateCleanly(request, dao, "grible_init.sql");
			} else {
				String currentVersion = dao.getCurrentDbVersion();
				List<Migration> migrations = Migrations.getMigrationsSinceVersion(currentVersion);
				for (Migration migration : migrations) {
					String fileName = migration.getFileName();
					migrateCleanly(request, dao, fileName);
					if (migration.getVersion().equals("0.9.0")) {
						MigrationActions.moveDataToKeysAndValuesColumns();
						dao.executeUpdate("DROP TABLE \"values\" CASCADE");
						dao.executeUpdate("DROP TABLE keys CASCADE");
						dao.executeUpdate("DROP TABLE rows CASCADE");
					}
				}
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

	private void migrateCleanly(HttpServletRequest request, PostgresDao dao, String fileName) throws Exception, SQLException,
			PSQLException {
		String query = Migrations.getSQLQuery(request, fileName);
		try {
			dao.executeUpdate(query);
		} catch (PSQLException e) {
			if (!e.getMessage().equals("A result was returned when none was expected.")) {
				throw e;
			}
		}
	}

}
