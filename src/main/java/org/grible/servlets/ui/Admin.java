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
package org.grible.servlets.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.Dao;
import org.grible.model.Product;
import org.grible.model.User;
import org.grible.servlets.ServletHelper;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/admin/")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
			if (!GlobalSettings.getInstance().init(getServletContext().getRealPath(""))) {
				response.sendRedirect("/firstlaunch");
				return;
			}

			if (request.getSession(false).getAttribute("userName") == null) {
				response.sendRedirect("/");
			} else {
				StringBuilder responseHtml = new StringBuilder();
				responseHtml.append("<!DOCTYPE html>");
				responseHtml.append("<html>");
				responseHtml.append("<head>");
				responseHtml.append("<title>Admin - Grible</title>");
				responseHtml.append("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
				responseHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-1.9.1.min.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/admin.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/jquery.noty.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/top.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/defaultVars.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/default.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\">");

				responseHtml.append("function getProductsCheckboxes() {");
				StringBuilder builder = new StringBuilder("return '");
				List<Product> products = Dao.getProducts();
				for (Product product : products) {
					builder.append("<input id=\"" + product.getId()
							+ "\" class=\"access-product\" type=\"checkbox\" > " + product.getName() + "<br>");
				}
				builder.append("';}");
				responseHtml.append(builder);

				responseHtml.append("</script>");
				responseHtml.append("</head>");
				responseHtml.append("<body>");

				String userName = (String) request.getSession(false).getAttribute("userName");
				User currentUser = Dao.getUserByName(userName);
				responseHtml.append(ServletHelper.getUserPanel(currentUser));
				responseHtml.append("<div id=\"breadcrumb\" class=\"header-text\">");
				responseHtml.append("<span id=\"home-image\"><img src=\"../img/grible_logo_mini.png\"></span>");
				responseHtml.append("<a href=\"/\"><span id=\"home\" class=\"link-infront\">Home</span></a>");
				responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
				responseHtml.append("<a href=\"/admin/\"><span id=\"product-name\">Admin</span></a></div>");

				if (!currentUser.isAdmin()) {
					responseHtml.append("<span class=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
					responseHtml.append("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
				} else {
					responseHtml.append("<br /><br />");
					responseHtml.append("<div id=\"admin-page\" class=\"table\">");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div id=\"admin-users\" class=\"table-cell border-right\">");
					responseHtml.append("<span class=\"medium-header\">Users</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div class=\"table users-list\">");
					responseHtml.append("<div class=\"table-row users-header-row\">");
					responseHtml.append("<div class=\"table-cell users-header-cell\">UserName</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">Is Admin</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">Pruducts</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">Manage</div>");
					responseHtml.append("</div>");

					List<User> users = Dao.getUsers();
					for (User user : users) {
						responseHtml.append("<div class=\"table-row users-row\">");
						responseHtml.append("<div class=\"table-cell users-cell\" userid=\"" + user.getId() + "\">"
								+ user.getName() + "</div>");
						responseHtml.append("<div class=\"table-cell users-cell\">" + user.isAdmin() + "</div>");
						responseHtml.append("<div class=\"table-cell users-cell\">" + user.getProductsString() + "</div>");
						responseHtml.append("<div class=\"table-cell users-cell\">" + "<button userid=\"" + user.getId()
								+ "\" class=\"ui-button btn-edit-user\">Edit</button> " + "<button userid=\""
								+ user.getId() + "\" class=\"ui-button btn-delete-user\">Delete</button></div>");
						responseHtml.append("</div>");
					}
					responseHtml.append("</div>");

					responseHtml.append("<br /><br />");
					responseHtml.append("<span class=\"medium-header\">Add user</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div class=\"table add-user-table\">");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">User Name:</div>");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\"><input class=\"username\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">Password:</div>");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\"><input class=\"pass\" type=\"password\" ></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">Retype it:</div>");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\"><input class=\"retype-pass\" type=\"password\" ></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">Is Admin:</div>");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\"><input class=\"isadmin\" type=\"checkbox\" ></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">Products:</div>");
					responseHtml.append("<div class=\"table-cell add-user-table-cell\">");

					for (Product product : products) {
						responseHtml.append("<input id=\"" + product.getId() + "\" class=\"access-product\" type=\"checkbox\" > "
								+ product.getName() + "<br>");
					}

					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("<br><button id=\"add-user\" class=\"ui-button\">Add</button>");

					responseHtml.append("</div>"); // cell
					responseHtml.append("<div id=\"admin-database\" class=\"table-cell border-right border-left\">");
					responseHtml.append("<span class=\"medium-header\">Database</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div class=\"table users-list\">");
					responseHtml.append("<div class=\"table-row users-header-row\">");
					responseHtml.append("<div class=\"table-cell users-header-cell\">Property</div>");
					responseHtml.append("<div class=\"table-cell users-header-cell\">Value</div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">Database host:</div>");
					responseHtml.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbhost\" value=\""
							+ GlobalSettings.getInstance().getDbHost() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">Database port:</div>");
					responseHtml.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbport\" value=\""
							+ GlobalSettings.getInstance().getDbPort() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">Database name:</div>");
					responseHtml.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbname\" value=\""
							+ GlobalSettings.getInstance().getDbName() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">Database login:</div>");
					responseHtml.append("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dblogin\" value=\""
							+ GlobalSettings.getInstance().getDbLogin() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table-row users-row\">");
					responseHtml.append("<div class=\"table-cell users-cell\">Database password:</div>");
					responseHtml.append("<div class=\"table-cell users-cell\"><input type=\"password\" class=\"dialog-edit\" name=\"dbpswd\" value=\""
							+ GlobalSettings.getInstance().getDbPswd() + "\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("<br><button id=\"savedbsettings\" class=\"ui-button\">Save</button>");
					responseHtml.append("</div>"); // cell

					responseHtml.append("<div id=\"admin-grible-version\" class=\"table-cell border-left\">");
					responseHtml.append("<span class=\"medium-header\">Grible version</span>");
					responseHtml.append("<br /><br />");
					responseHtml.append("Current Grible version: "
							+ ServletHelper.getVersion(getServletContext().getRealPath("")));
					responseHtml.append("<br /><br />");
					responseHtml.append("<button id=\"check-for-updates\" class=\"ui-button\">Check for updates</button>");
					responseHtml.append("<br /><br />");
					responseHtml.append("<div id=\"update-result\"></div>");
					responseHtml.append("</div>"); // cell
					responseHtml.append("</div>"); // row
					responseHtml.append("</div>"); // page

				}
				responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath("")));
				responseHtml.append("</body>");
				responseHtml.append("</html>");
				out.print(responseHtml.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
