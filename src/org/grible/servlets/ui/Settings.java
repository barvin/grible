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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.Dao;
import org.grible.model.User;
import org.grible.servlets.ServletHelper;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/settings/")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Settings() {
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
				response.sendRedirect("/grible/firstlaunch");
				return;
			}
			StringBuilder responseHtml = new StringBuilder();

			if (request.getSession(false) == null) {
				response.sendRedirect("/grible/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if (request.getSession(false).getAttribute("userName") == null) {
				response.sendRedirect("/grible/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else {
				responseHtml.append("<!DOCTYPE html>");
				responseHtml.append("<html>");
				responseHtml.append("<head>");
				responseHtml.append("<title>Settings - Grible</title>");
				responseHtml.append("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
				responseHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-1.9.1.min.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/settings.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/footer.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/jquery.noty.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/top.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/defaultVars.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/default.js\"></script>");
				responseHtml.append("</head>");
				responseHtml.append("<body>");

				String userName = (String) request.getSession(false).getAttribute("userName");
				User currentUser = Dao.getUserByName(userName);
				responseHtml.append(ServletHelper.getUserPanel(currentUser));
				responseHtml.append("<div id=\"breadcrumb\" class=\"header-text\">"
						+ "<a href=\"/grible\"><span id=\"home\" class=\"link-infront\">Home</span></a>");
				responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
				responseHtml.append("<a href=\"/grible/settings/\"><span id=\"product-name\">Settings</span></a></div>");

				responseHtml.append("<br /><br />");
				responseHtml.append("<div id=\"settings-page\" class=\"table\">");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div id=\"admin-users\" class=\"table-cell border-right\">");
				responseHtml.append("<span class=\"medium-header\">Table view</span>");
				responseHtml.append("<br /><br />");

				String tooltipOnClick = "";
				if (currentUser.isTooltipOnClick()) {
					tooltipOnClick = "checked=\"checked\"";
				}
				responseHtml.append("<input id=\"cbx-tooltiponclick\" type=\"checkbox\" ").append(tooltipOnClick)
						.append(" />");
				responseHtml.append("<span id=\"option-tooltiponclick\" class=\"label-option\"> Show storage cell info on click</span>");

				responseHtml.append("<br><br><button id=\"btn-save-settings\" class=\"ui-button\">Save</button>");

				responseHtml.append("</div>"); // cell
				responseHtml.append("</div>"); // row
				responseHtml.append("</div>"); // page

				responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath(""), "../img"));
				responseHtml.append("</body>");
				responseHtml.append("</html>");
				out.print(responseHtml.toString());
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
			request.getSession(false).setAttribute("importResult", null);
			request.getSession(false).setAttribute("importedTable", null);
			request.getSession(false).setAttribute("importedFile", null);
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
