package org.grible.servlets.firstlaunch;

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
import org.grible.servlets.ServletHelper;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class FirstLaunch
 */
@WebServlet("/firstlaunch")
public class FirstLaunch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FirstLaunch() {
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
			if (!GlobalSettings.getInstance().init(getServletContext().getRealPath(""))) {
				FileReader fr = new FileReader(new File(getServletContext().getRealPath("")
						+ "/WEB-INF/firstlaunch.html"));
				List<String> lines = IOUtils.readLines(fr);
				StringBuilder content = new StringBuilder();
				for (String line : lines) {
					content.append(line).append("\n");
				}
				String finalContent = content.toString().replace("Version:",
						"Version: " + ServletHelper.getVersion(getServletContext().getRealPath("")));
				out.print(finalContent);
			} else {
				out.println("<!DOCTYPE html>");
				out.println("<html>");
				out.println("<head>");
				out.println("<title>Grible</title>");
				out.println("<link rel=\"shortcut icon\" href=\"img/favicon.ico\" >");
				out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
				out.println("<script type=\"text/javascript\" src=\"js/jquery-1.11.0.min.js\"></script>");
				out.println("<script type=\"text/javascript\" src=\"js/home.js\"></script>");
				out.println("</head>");
				out.println("<body>");
				out.println("<a href=\".\"><img id=\"logo-mini\" src=\"img/grible_logo_mini.png\"></a>");
				out.println("<br/><br/><div class=\"error-message\">You cannot access this page!"
						+ "<br>Your database information is stored in config.xml file and seems to be valid."
						+ "<br>If you really need to reset your database information and/or Grible administrator credentials"
						+ ", delete config.xml file on the server.</div>");
				out.println(ServletHelper.getFooter(getServletContext().getRealPath("")));
				out.println("</body>");
				out.println("</html>");
			}
		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
