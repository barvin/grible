package org.grible.servlets.firstlaunch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.grible.dao.Dao;
import org.grible.settings.GlobalSettings;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			boolean createNewDb = Boolean.parseBoolean(request.getParameter("createnew"));

			if (createNewDb) {
				String query = getSQLQuery("/WEB-INF/sql/grible_init.sql");
				String querySetSeqVal = getSQLQuery("/WEB-INF/sql/grible_setseqval.sql");
				Dao.executeUpdate(query);
				Dao.executeSelect(querySetSeqVal);
			} else {
				Dao.executeUpdateNoFail("ALTER TABLE ONLY keys ADD CONSTRAINT keys_tableid_order_key UNIQUE (tableid, \"order\");");
				Dao.executeUpdateNoFail("ALTER TABLE ONLY rows ADD CONSTRAINT rows_tableid_order_key UNIQUE (tableid, \"order\");");
				Dao.executeUpdateNoFail("ALTER TABLE ONLY \"values\" ADD CONSTRAINT values_rowid_keyid_key UNIQUE (rowid, keyid);");
				if (!Dao.isTableTypeExist("enumeration")) {
					Dao.executeUpdate("INSERT INTO tabletypes(name) VALUES ('enumeration')");
				}
				if (!Dao.columnExist("tables", "showwarning")) {
					Dao.executeUpdate("ALTER TABLE tables ADD COLUMN showwarning boolean NOT NULL DEFAULT true;");
				}
				if (!Dao.columnExist("tables", "modifiedtime")) {
					Dao.executeUpdate("ALTER TABLE tables ADD COLUMN modifiedtime timestamp without time zone NOT NULL DEFAULT '2013-01-01 00:00:00';");
				}
				if (!Dao.columnExist("users", "tooltiponclick")) {
					Dao.executeUpdate("ALTER TABLE users ADD COLUMN tooltiponclick boolean NOT NULL DEFAULT false;");
				}
				updateValuesLength();
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

	private void updateValuesLength() throws SQLException {
		int newValuesLength = 5000;
		String strLength = Dao
				.executeSelect("SELECT atttypmod FROM pg_attribute WHERE attrelid = 'values'::regclass AND attname = 'value';");
		int actualValuesLength = 0;
		if (strLength != null) {
			actualValuesLength = Integer.parseInt(strLength);
		}
		if ((actualValuesLength - 4) != newValuesLength) {
			Dao.executeUpdate("UPDATE pg_attribute SET atttypmod = " + newValuesLength
					+ " + 4 WHERE attrelid = 'values'::regclass AND attname = 'value';");
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
