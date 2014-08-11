/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.servlets.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.PostgresDao;
import org.grible.model.Product;
import org.grible.model.User;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/admin/")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String host = "http://www.grible.org";
	private static boolean isNewVersionExist = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			StringBuilder responseHtml = new StringBuilder();
			responseHtml.append("<!DOCTYPE html>");
			responseHtml.append("<html>");
			responseHtml.append("<head>");
			responseHtml.append("<title>Admin - Grible</title>");
			responseHtml.append("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
			responseHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
			responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-1.11.0.min.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"../lang/current.json\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"../js/admin.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/jquery.noty.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/top.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/defaultVars.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/default.js\"></script>");
			List<Product> products = null;
			if (isMultipleUsers()) {
				responseHtml.append("<script type=\"text/javascript\">");

				responseHtml.append("function getProductsCheckboxes() {");
				StringBuilder builder = new StringBuilder("return '");
				products = DataManager.getInstance().getDao().getProducts();
				for (Product product : products) {
					builder.append("<input id=\"" + product.getId()
							+ "\" class=\"access-product\" type=\"checkbox\" > " + product.getName() + "<br>");
				}
				builder.append("';}");
				responseHtml.append(builder);

				responseHtml.append("</script>");
			}
			responseHtml.append("</head>");
			responseHtml.append("<body>");

			User currentUser = null;
			if (isMultipleUsers()) {
				String userName = (String) request.getSession(false).getAttribute("userName");
				currentUser = new PostgresDao().getUserByName(userName);
				responseHtml.append(ServletHelper.getUserPanel(currentUser));
			} else {
				responseHtml.append(ServletHelper.getUserPanel());
			}
			responseHtml.append("<div id=\"breadcrumb\" class=\"header-text\">");
			responseHtml.append("<span id=\"home-image\"><img src=\"../img/grible_logo_mini.png\"></span>");
			responseHtml.append("<a href=\"/\"><span id=\"home\" class=\"link-infront\">" + Lang.get("home")
					+ "</span></a>");
			responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
			responseHtml.append("<a href=\"/admin/\"><span id=\"product-name\">" + Lang.get("admin")
					+ "</span></a></div>");

			if (isMultipleUsers() && (!currentUser.isAdmin())) {
				responseHtml
						.append("<span class=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
				responseHtml.append("<br/><br/><div class=\"error-message\">" + Lang.get("nopermissions") + "</div>");
			} else {
				responseHtml.append("<br /><br />");
				responseHtml.append("<div id=\"admin-page\" class=\"table\">");
				responseHtml.append("<div class=\"table-row\">");

				if (isMultipleUsers()) {
					responseHtml.append("<div id=\"admin-users\" class=\"table-cell border-right\">");
					responseHtml.append("<span class=\"medium-header\">" + Lang.get("users") + "</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div class=\"table users-list\">");
					responseHtml.append("<div class=\"table-row users-header-row\">");
					responseHtml.append("<div class=\"table-cell users-header-cell\">" + Lang.get("username")
							+ "</div>");
					responseHtml
							.append("<div class=\"table-cell users-header-cell\">" + Lang.get("isadmin") + "</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">" + Lang.get("products")
							+ "</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">" + Lang.get("manage") + "</div>");
					responseHtml.append("</div>");

					List<User> users = new PostgresDao().getUsers();
					for (User user : users) {
						responseHtml.append("<div class=\"table-row users-row\">");
						responseHtml.append("<div class=\"table-cell users-cell\" userid=\"" + user.getId() + "\">"
								+ user.getName() + "</div>");
						responseHtml.append("<div class=\"table-cell users-cell\">" + user.isAdmin() + "</div>");
						responseHtml.append("<div class=\"table-cell users-cell\">" + user.getProductsString()
								+ "</div>");
						responseHtml.append("<div class=\"table-cell users-cell\">" + "<button userid=\""
								+ user.getId() + "\" class=\"ui-button btn-edit-user\">" + Lang.get("edit")
								+ "</button> " + "<button userid=\"" + user.getId()
								+ "\" class=\"ui-button btn-delete-user\">" + Lang.get("del") + "</button></div>");
						responseHtml.append("</div>");
					}
					responseHtml.append("</div>");

					responseHtml.append("<br /><br />");
					responseHtml.append("<span class=\"medium-header\">" + Lang.get("adduser") + "</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div class=\"table add-user-table\">");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">" + Lang.get("username")
							+ ":</div>");
					responseHtml
							.append("<div class=\"table-cell add-user-table-cell\"><input class=\"username\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">" + Lang.get("password")
							+ ":</div>");
					responseHtml
							.append("<div class=\"table-cell add-user-table-cell\"><input class=\"pass\" type=\"password\" ></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">" + Lang.get("retypeit")
							+ ":</div>");
					responseHtml
							.append("<div class=\"table-cell add-user-table-cell\"><input class=\"retype-pass\" type=\"password\" ></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">" + Lang.get("isadmin")
							+ ":</div>");
					responseHtml
							.append("<div class=\"table-cell add-user-table-cell\"><input class=\"isadmin\" type=\"checkbox\" ></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">" + Lang.get("products")
							+ ":</div>");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">");

					for (Product product : products) {
						responseHtml.append("<input id=\"" + product.getId()
								+ "\" class=\"access-product\" type=\"checkbox\" > " + product.getName() + "<br>");
					}

					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("<br><button id=\"add-user\" class=\"ui-button\">" + Lang.get("adduser")
							+ "</button>");

					responseHtml.append("</div>"); // cell
					responseHtml.append("<div id=\"admin-database\" class=\"table-cell border-right border-left\">");
					responseHtml.append("<span class=\"medium-header\">" + Lang.get("database") + "</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div class=\"table users-list\">");
					responseHtml.append("<div class=\"table-row users-header-row\">");
					responseHtml.append("<div class=\"table-cell users-header-cell\">" + Lang.get("property")
							+ "</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">" + Lang.get("value") + "</div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">" + Lang.get("dbhost") + ":</div>");
					responseHtml
							.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbhost\" value=\""
									+ GlobalSettings.getInstance().getDbHost() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">" + Lang.get("dbport") + ":</div>");
					responseHtml
							.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbport\" value=\""
									+ GlobalSettings.getInstance().getDbPort() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">" + Lang.get("dbname") + ":</div>");
					responseHtml
							.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbname\" value=\""
									+ GlobalSettings.getInstance().getDbName() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">" + Lang.get("dblogin") + ":</div>");
					responseHtml
							.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dblogin\" value=\""
									+ GlobalSettings.getInstance().getDbLogin() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">" + Lang.get("dbpswd") + ":</div>");
					responseHtml
							.append("<div class=\"table-cell users-cell\"><input type=\"password\" class=\"dialog-edit\" name=\"dbpswd\" value=\""
									+ GlobalSettings.getInstance().getDbPswd() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("<br><button id=\"savedbsettings\" class=\"ui-button\">" + Lang.get("save")
							+ "</button>");
					responseHtml.append("</div>"); // cell
					responseHtml.append("<div id=\"admin-grible-version\" class=\"table-cell border-left\">");
				} else {
					responseHtml.append("<div id=\"admin-grible-version\" class=\"table-cell border-right\">");
				}
				responseHtml.append("<span class=\"medium-header\">" + Lang.get("gribleversion") + "</span>");
				responseHtml.append("<br /><br />");
				responseHtml.append(Lang.get("currentgribleversion") + ": "
						+ ServletHelper.getVersion(getServletContext().getRealPath("")));

				if (ServletHelper.isWindows()) {
					responseHtml.append("<br /><br />");
					responseHtml.append("<div id=\"checking-for-update-result\">");
					try {
						String currentVersion = ServletHelper.getVersion(getServletContext().getRealPath(""));
						String latestVersion = getLatestVersionForWindows();
						if (currentVersion.equals(latestVersion)) {
							responseHtml.append(Lang.get("uptodate"));
						} else {
							responseHtml.append(Lang.get("newversionavailable1") + latestVersion
									+ Lang.get("newversionavailable2"));
							isNewVersionExist = true;
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
						responseHtml.append(Lang.get("error") + ": " + Lang.get("hostunavailable") + " " + host + Lang.get("checkconnection"));
					} catch (Exception e) {
						e.printStackTrace();
						responseHtml.append(Lang.get("error") + ": " + e.getLocalizedMessage());
					}

					responseHtml.append("</div>");
					if (isNewVersionExist) {
						responseHtml.append("<br />");
						responseHtml.append("<button id=\"btn-apply-updates\" class=\"ui-button\">"
								+ Lang.get("applyupdates") + "</button>");
						responseHtml.append("<br /><br />");
						responseHtml.append("<div id=\"update-result\"></div>");
					}
				}

				responseHtml.append("</div>"); // cell
				responseHtml.append("</div>"); // row
				responseHtml.append("</div>"); // page

			}
			responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath("")));
			responseHtml.append("</body>");
			responseHtml.append("</html>");
			out.print(responseHtml.toString());
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}

	private String getLatestVersionForWindows() throws Exception {
		String url = host + "/updates/latestversion.php?platform=Windows";
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

	private boolean isMultipleUsers() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.POSTGRESQL;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
