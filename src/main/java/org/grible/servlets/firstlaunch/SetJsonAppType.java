package org.grible.servlets.firstlaunch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.xom.Document;
import nu.xom.Element;

import org.grible.json.ConfigJson;
import org.grible.model.Product;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class SaveDBSettings
 */
@WebServlet("/SetJsonAppType")
public class SetJsonAppType extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SetJsonAppType() {
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
			GlobalSettings.getInstance().setAppType(AppTypes.JSON);

			Element root = new Element("configuration");
			
			Element apptype = new Element("apptype");
			apptype.appendChild(AppTypes.JSON.toString());						
			root.appendChild(apptype);

			Document doc = new Document(root);
			String result = doc.toXML();

			File dir = new File(getServletContext().getRealPath("") + File.separator + ".." + File.separator + "config");
			if (!dir.exists()) {
				dir.mkdir();
			}
			writeToFileInConfigFolder(GlobalSettings.getInstance().getConfigFilePath(), result);
			
			ConfigJson configJson = new ConfigJson();
			configJson.setProducts(new ArrayList<Product>());
			configJson.setTooltipOnClick(false);
			GlobalSettings.getInstance().setConfigJson(configJson);
			GlobalSettings.getInstance().getConfigJson().save();
			
			out.print("success");

		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	private File writeToFileInConfigFolder(String filePath, String text) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(text);
		bw.close();
		return file;
	}

}
