package org.grible.servlets.updates;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CheckForUpdates
 */
@WebServlet("/ApplyUpdates")
public class ApplyUpdates extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String host = "http://www.grible.org";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		FileOutputStream fos = null;
		try {
			fos = downloadWarFile();
			out.print("success");
			runRestartBatchFile();
		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
			fos.close();
		}
	}

	private void runRestartBatchFile() throws IOException {
		Runtime rt = Runtime.getRuntime();
		rt.exec("restart.bat");
	}

	private FileOutputStream downloadWarFile() throws MalformedURLException, IOException, FileNotFoundException {
		FileOutputStream fos;
		URL website = new URL(host + "/binaries/latest/grible.war");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		fos = new FileOutputStream(getServletContext().getRealPath("") + File.separator + ".." + File.separator
				+ "grible_new.war");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		return fos;
	}
}
