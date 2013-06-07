package org.pine.servlets.updates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.servlets.ServletHelper;

/**
 * Servlet implementation class CheckForUpdates
 */
@WebServlet("/CheckForUpdates")
public class CheckForUpdates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String host = "http://www.pine-project.org";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			String currentVersion = ServletHelper.getBuildNumber(getServletContext().getRealPath(""));
			String latestVersion = getLatestVersion();
			if (currentVersion.equals(latestVersion)) {
				out.print("Current Pine version is up to date.");
			} else {
				out.print("New version (" + latestVersion + ") is available to download at " + "<a href=\"" + host
						+ "/download.php\" target=\"_blank\">" + host + "</a>.");
			}
		} catch (UnknownHostException e) {
			out.print("ERROR: Cannot connect to host " + host + ". Please, check your internet connection.");
			e.printStackTrace();
		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	private String getLatestVersion() throws Exception {
		String url = host + "/updates/latestversion.php";
		String charset = "UTF-8";
		String result = "";

		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		OutputStream output = null;
		try {
			output = connection.getOutputStream();
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		InputStream response = connection.getInputStream();
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(response, charset));
		result = reader.readLine();

		return result;
	}
}
