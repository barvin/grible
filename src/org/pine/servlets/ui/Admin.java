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
import org.pine.model.Product;
import org.pine.model.User;
import org.pine.servlets.ServletHelper;
import org.pine.settings.GlobalSettings;

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
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			if (!GlobalSettings.getInstance().init(getServletContext().getRealPath(""))) {
				response.sendRedirect("/pine/firstlaunch");
				return;
			}

			if (request.getSession(false).getAttribute("userName") == null) {
				response.sendRedirect("/pine");
			} else {
				out.print("<!DOCTYPE html>");
				out.print("<html>");
				out.print("<head>");
				out.print("<title>Admin - Pine</title>");
				out.print("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
				out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
				out.print("<script type=\"text/javascript\" src=\"../js/jquery-1.9.1.min.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/admin.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/footer.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/noty/jquery.noty.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/noty/top.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/noty/defaultVars.js\"></script>");
				out.print("<script type=\"text/javascript\" src=\"../js/noty/default.js\"></script>");
				out.println("<script type=\"text/javascript\">");

				out.println("function getProductsCheckboxes() {");
				StringBuilder builder = new StringBuilder("return '");
				List<Product> products = Dao.getProducts();
				for (Product product : products) {
					builder.append("<input id=\"" + product.getId()
							+ "\" class=\"access-product\" type=\"checkbox\" > " + product.getName() + "<br>");
				}
				builder.append("';}");
				out.println(builder.toString());

				out.println("</script>");
				out.print("</head>");
				out.print("<body>");

				String userName = (String) request.getSession(false).getAttribute("userName");
				User currentUser = Dao.getUserByName(userName);
				out.print(ServletHelper.getUserPanel(currentUser));
				out.print("<div id=\"breadcrumb\" class=\"header-text\">"
						+ "<a href=\"/pine\"><span id=\"home\" class=\"link-infront\">Home</span></a>");
				out.print("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
				out.print("<a href=\"/pine/admin/\"><span id=\"product-name\">Admin</span></a></div>");

				if (!currentUser.isAdmin()) {
					out.println("<span class=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
					out.println("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
				} else {
					out.print("<br /><br />");
					out.print("<div id=\"admin-page\" class=\"table\">");
					out.print("<div class=\"table-row\">");
					out.print("<div id=\"admin-users\" class=\"table-cell border-right\">");
					out.print("<span class=\"medium-header\">Users</span>");
					out.print("<br /><br />");
					out.print("<div class=\"table users-list\">");
					out.print("<div class=\"table-row users-header-row\">");
					out.print("<div class=\"table-cell users-header-cell\">UserName</div>");
					out.print("<div class=\"table-cell users-header-cell\">Is Admin</div>");
					out.print("<div class=\"table-cell users-header-cell\">Pruducts</div>");
					out.print("<div class=\"table-cell users-header-cell\">Manage</div>");
					out.print("</div>");

					List<User> users = Dao.getUsers();
					for (User user : users) {
						out.print("<div class=\"table-row users-row\">");
						out.print("<div class=\"table-cell users-cell\" userid=\"" + user.getId() + "\">"
								+ user.getName() + "</div>");
						out.print("<div class=\"table-cell users-cell\">" + user.isAdmin() + "</div>");
						out.print("<div class=\"table-cell users-cell\">" + user.getProductsString() + "</div>");
						out.print("<div class=\"table-cell users-cell\">" + "<button userid=\"" + user.getId()
								+ "\" class=\"ui-button btn-edit-user\">Edit</button> " + "<button userid=\""
								+ user.getId() + "\" class=\"ui-button btn-delete-user\">Delete</button></div>");
						out.print("</div>");
					}
					out.print("</div>");

					out.print("<br /><br />");
					out.print("<span class=\"medium-header\">Add user</span>");
					out.print("<br /><br />");
					out.print("<div class=\"table add-user-table\">");
					out.print("<div class=\"table-row\">");
					out.print("<div class=\"table-cell add-user-table-cell\">User Name:</div>");
					out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"username\"></div>");
					out.print("</div>");
					out.print("<div class=\"table-row\">");
					out.print("<div class=\"table-cell add-user-table-cell\">Password:</div>");
					out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"pass\" type=\"password\" ></div>");
					out.print("</div>");
					out.print("<div class=\"table-row\">");
					out.print("<div class=\"table-cell add-user-table-cell\">Retype it:</div>");
					out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"retype-pass\" type=\"password\" ></div>");
					out.print("</div>");
					out.print("<div class=\"table-row\">");
					out.print("<div class=\"table-cell add-user-table-cell\">Is Admin:</div>");
					out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"isadmin\" type=\"checkbox\" ></div>");
					out.print("</div>");
					out.print("<div class=\"table-row\">");
					out.print("<div class=\"table-cell add-user-table-cell\">Products:</div>");
					out.print("<div class=\"table-cell add-user-table-cell\">");

					for (Product product : products) {
						out.print("<input id=\"" + product.getId() + "\" class=\"access-product\" type=\"checkbox\" > "
								+ product.getName() + "<br>");
					}

					out.print("</div>");
					out.print("</div>");
					out.print("</div>");
					out.print("<br><button id=\"add-user\" class=\"ui-button\">Add</button>");

					out.print("</div>"); // cell
					out.print("<div id=\"admin-database\" class=\"table-cell border-right border-left\">");
					out.print("<span class=\"medium-header\">Database</span>");
					out.print("<br /><br />");
					out.print("<div class=\"table users-list\">");
					out.print("<div class=\"table-row users-header-row\">");
					out.print("<div class=\"table-cell users-header-cell\">Property</div>");
					out.print("<div class=\"table-cell users-header-cell\">Value</div>");
					out.print("</div>");
					out.print("<div class=\"table-row users-row\">");
					out.print("<div class=\"table-cell users-cell\">Database host:</div>");
					out.print("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbhost\" value=\""
							+ GlobalSettings.getInstance().getDbHost() + "\"></div>");
					out.print("</div>");
					out.print("<div class=\"table-row users-row\">");
					out.print("<div class=\"table-cell users-cell\">Database port:</div>");
					out.print("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbport\" value=\""
							+ GlobalSettings.getInstance().getDbPort() + "\"></div>");
					out.print("</div>");
					out.print("<div class=\"table-row users-row\">");
					out.print("<div class=\"table-cell users-cell\">Database name:</div>");
					out.print("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dbname\" value=\""
							+ GlobalSettings.getInstance().getDbName() + "\"></div>");
					out.print("</div>");
					out.print("<div class=\"table-row users-row\">");
					out.print("<div class=\"table-cell users-cell\">Database login:</div>");
					out.print("<div class=\"table-cell users-cell\"><input class=\"dialog-edit\" name=\"dblogin\" value=\""
							+ GlobalSettings.getInstance().getDbLogin() + "\"></div>");
					out.print("</div>");
					out.print("<div class=\"table-row users-row\">");
					out.print("<div class=\"table-cell users-cell\">Database password:</div>");
					out.print("<div class=\"table-cell users-cell\"><input type=\"password\" class=\"dialog-edit\" name=\"dbpswd\" value=\""
							+ GlobalSettings.getInstance().getDbPswd() + "\"></div>");
					out.print("</div>");
					out.print("</div>");
					out.print("<br><button id=\"savedbsettings\" class=\"ui-button\">Save</button>");
					out.print("</div>"); // cell

					out.print("<div id=\"admin-pine-version\" class=\"table-cell border-left\">");
					out.print("<span class=\"medium-header\">Pine version</span>");
					out.print("<br /><br />");
					out.print("Current Pine version: "
							+ ServletHelper.getBuildNumber(getServletContext().getRealPath("")));
					out.print("<br /><br />");
					out.print("<button id=\"check-for-updates\" class=\"ui-button\">Check for updates</button>");
					out.print("<br /><br />");
					out.print("<div id=\"update-result\"></div>");
					out.print("</div>"); // cell
					out.print("</div>"); // row
					out.print("</div>"); // page

				}
				out.println(ServletHelper.getFooter(getServletContext().getRealPath(""), "../img"));
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
