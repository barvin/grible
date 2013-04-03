/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.pine.servlets.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.excel.TempVars;
import org.pine.model.Category;
import org.pine.model.TableType;
import org.pine.model.User;
import org.pine.servlets.ServletHelper;
import org.pine.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/import/")
public class Import extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Import() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			if (!GlobalSettings.getInstance().init(getServletContext().getRealPath(""))) {
				response.sendRedirect("/pine/firstlaunch");
				return;
			}

			if (request.getSession(false) == null) {
				response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if (request.getSession(false).getAttribute("userName") == null) {
				response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if (request.getParameter("product") == null) {
				response.sendRedirect("/pine");
			} else {
				out.print("<!DOCTYPE html>");
				out.print("<html>");
				out.print("<head>");
				out.print("<title>Import - Pine</title>");
				out.print("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
				out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
				out.print("<script type=\"text/javascript\" src=\"../js/footer.js\"></script>");
				out.print("</head>");
				out.print("<body>");

				String userName = (String) request.getSession(false).getAttribute("userName");
				User user = null;
				user = Dao.getUserByName(userName);
				int productId = Integer.parseInt(request.getParameter("product"));

				if (!user.hasAccessToProduct(productId)) {
					out.println("<a href=\".\"><span id=\"home\" class=\"header-text\">Home</span></a>");
					out.println("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
				} else {
					out.print(ServletHelper.getUserPanel(user));
					out.print(ServletHelper.getBreadCrump("import", Dao.getProduct(productId)));

					out.print("<br />");
					out.print("<span class=\"medium-header\">Data Table Import</span>");
					out.print("<br />");
					out.print("<br />Only .XLS or .XLSX files are acceptable.");
					out.print("<br />First sheet will be processed as the General data sheet.");
					out.print("<br />If \"Preconditions\" sheet is present, it will be processed as Preconditions (1st row - the row of keys, 2nd - the row of values).");
					out.print("<br />If \"Postconditions\" sheet is present, it will be processed as Postconditions (1st row - the row of keys, 2nd - the row of values).");
					out.print("<br />Make sure \"Index\" column or any other help data is absent. Data file name will be taken from the Excel file name.");
					out.print("<br /><br />");

					out.print("<form action=\"../TableImport?product=" + productId + "\" method=\"post\" ");
					out.print("enctype=\"multipart/form-data\">");
					out.print("Select category: &nbsp;<select name=\"category\">");

					List<Category> categories = Dao.getCategories(productId, TableType.TABLE);
					for (Category category : categories) {
						out.print("<option value=\"" + category.getId() + "\">" + category.getName() + "</option>");
					}

					out.print("</select> <br /> <br /> <input type=\"file\" name=\"file\" size=\"50\" /> <input type=\"submit\" value=\"Import\" />");
					out.print("</form>");
					out.print("<br />");

					String dataFileImportResult = TempVars.getTableImportResult();
					if (!"".equals(dataFileImportResult)) {
						out.print("<div id=\"datafile-import-response\">" + dataFileImportResult + "</div>");
					}

					out.print("<br /><br /><br /><br />");
					out.print("<span class=\"medium-header\">Data Storage Import</span>");
					out.print("<br />");
					out.print("<br />Only .XLS or .XLSX files are acceptable. Only first sheet will be processed.");
					out.print("<br />Make sure \"Index\" column or any other help data is absent. File name would be storage name.");
					out.print("<br /><br />");
					out.print("<form action=\"../StorageImport?product=" + productId + "\" method=\"post\" "
							+ "enctype=\"multipart/form-data\">");
					out.print("Select category: &nbsp;<select name=\"category\">");

					List<Category> storageCategories = Dao.getCategories(productId, TableType.STORAGE);
					for (Category category : storageCategories) {
						out.print("<option value=\"" + category.getId() + "\">" + category.getName() + "</option>");
					}

					out.print("</select> <br /> <br /> Class name: &nbsp;<input name=\"class\" size=\"36\" />"
							+ "<br /> <br /> <input type=\"file\" name=\"file\"	size=\"50\" /> <input type=\"submit\" value=\"Import\" />");
					out.print("</form>");
					out.print("<br />");

					String dataStorageImportResult = TempVars.getStorageImportResult();
					if (!"".equals(dataStorageImportResult)) {
						out.print("<div id=\"datastorage-import-response\">" + dataStorageImportResult + "</div>");
					}
				}
				out.print(ServletHelper.getFooter(getServletContext().getRealPath(""), "../img"));
				out.print("</body>");
				out.print("</html>");
			}

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
