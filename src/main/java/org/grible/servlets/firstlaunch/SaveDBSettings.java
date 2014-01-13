package org.grible.servlets.firstlaunch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.settings.GlobalSettings;

import nu.xom.Document;
import nu.xom.Element;

/**
 * Servlet implementation class SaveDBSettings
 */
@WebServlet("/SaveDBSettings")
public class SaveDBSettings extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveDBSettings() {
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
			Element root = new Element("configuration");
			Element apptype = new Element("apptype");
			apptype.appendChild(GlobalSettings.getInstance().getAppType().toString());
			root.appendChild(apptype);
			
			Element database = new Element("database");

			Element host = new Element("dbhost");
			host.appendChild(GlobalSettings.getInstance().getDbHost());

			Element port = new Element("dbport");
			port.appendChild(GlobalSettings.getInstance().getDbPort());

			Element dbname = new Element("dbname");
			dbname.appendChild(GlobalSettings.getInstance().getDbName());

			Element login = new Element("dblogin");
			login.appendChild(GlobalSettings.getInstance().getDbLogin());

			Element password = new Element("dbpswd");
			password.appendChild(GlobalSettings.getInstance().getDbPswd());

			database.appendChild(host);
			database.appendChild(port);
			database.appendChild(dbname);
			database.appendChild(login);
			database.appendChild(password);
			root.appendChild(database);

			Document doc = new Document(root);
			String result = doc.toXML();

			File dir = new File(getServletContext().getRealPath("") + File.separator + ".." + File.separator + "config");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(getServletContext().getRealPath("") + File.separator + ".." + File.separator + "config" + File.separator + "config.xml");
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(result);
			bw.close();

			out.print("Done.");

		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

}
