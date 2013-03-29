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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Product;
import org.pine.model.Table;
import org.pine.model.User;
import org.pine.servlets.ServletHelper;
import org.pine.settings.GlobalSettings;
import org.pine.uimodel.Sections;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/tables/")
public class Tables extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Tables() {
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
				response.sendRedirect("/pine/firstlaunch");
				return;
			}
			StringBuilder responseHtml = new StringBuilder();
			

			if (request.getSession(false) == null) {
				response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if (request.getSession(false).getAttribute("userName") == null) {
				response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
			} else if ((request.getParameter("product") == null) && (request.getParameter("id") == null)) {
				response.sendRedirect("/pine");
			} else {

				responseHtml.append("<!DOCTYPE html>");
				responseHtml.append("<html>");
				responseHtml.append("<head>");
				responseHtml.append("<title>Data Tables - Pine</title>");
				responseHtml.append("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
				responseHtml.append("<link href=\"../css/style.css\" rel=\"stylesheet\" type=\"text/css\" />");
				responseHtml
						.append("<link href=\"../css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-1.9.1.min.js\"></script>");
				responseHtml
						.append("<script type=\"text/javascript\" src=\"../js/jquery-ui-1.10.2.custom.min.js\"></script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery.contextMenu.js\"></script>");

				String userName = (String) request.getSession(false).getAttribute("userName");
				User user = Dao.getUserByName(userName);

				int productId = 0;
				int tableId = 0;
				String tableType = "table";
				if (request.getParameter("id") != null) {
					tableId = Integer.parseInt(request.getParameter("id"));
					Table table = Dao.getTable(tableId);
					switch (table.getType()) {
					case TABLE:
						productId = Dao.getProductIdByPrimaryTableId(tableId);
						break;

					case PRECONDITION:
						productId = Dao.getProductIdBySecondaryTableId(tableId);
						break;

					case POSTCONDITION:
						productId = Dao.getProductIdBySecondaryTableId(tableId);
						break;

					default:
						break;
					}
					tableType = table.getType().toString().toLowerCase();
				} else {
					productId = Integer.parseInt(request.getParameter("product"));
				}

				if (!user.hasAccessToProduct(productId)) {
					responseHtml.append("<a href=\".\"><span id=\"home\" class=\"header-text\">Home</span></a>");
					responseHtml
							.append("<span id=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
					responseHtml
							.append("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
				} else {

					responseHtml.append("<script type=\"text/javascript\">");
					responseHtml.append("var productId = \"").append(productId).append("\";");
					responseHtml.append("var tableId = \"").append(tableId).append("\";");
					responseHtml.append("var tableType = \"").append(tableType).append("\";");
					responseHtml.append("</script>");
					responseHtml.append("<script type=\"text/javascript\" src=\"../js/dataCenter.js\"></script>");

					responseHtml.append("</head>");
					responseHtml.append("<body>");
					responseHtml.append(ServletHelper.getUserPanel(user));
					includeHeader(responseHtml, "tables", Dao.getProduct(productId));

					responseHtml.append("<div id=\"main\" class=\"table\">");
					responseHtml.append("<div class=\"table-row\">");
					responseHtml.append("<div class=\"table-cell left-panel\">");
					responseHtml.append("<span class=\"lbl-categories\">Categories:</span>");
					responseHtml.append("<div id=\"entities-list\">");
					responseHtml.append("<div id=\"category-container\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("<div id=\"waiting\" class=\"table-cell\">");
					responseHtml.append("<img src=\"../img/ajax-loader.gif\" class=\"waiting-gif\" />");
					responseHtml.append("<div class=\"top-panel\"></div>");
					responseHtml.append("<div id=\"table-container\">");
					responseHtml.append("<div class=\"table entities-values\"></div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append("</div>");
					responseHtml.append(ServletHelper.getContextMenus("table"));
					responseHtml.append(ServletHelper.getLoadingGif());
				}
				responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath(""), "../img"));
				responseHtml.append("</body>");
				responseHtml.append("</html>");
				out.print(responseHtml.toString());
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void includeHeader(StringBuilder responseHtml, String sectionKey, Product product) {

		String productName = product.getName();
		String sectionName = Sections.getNameByKey(sectionKey);

		responseHtml.append("<div id=\"breadcrump\"><a href=\"/pine\"><span id=\"home\" class=\"header-text\">Home</span></a>");
		responseHtml.append("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a href=\"/pine/?product=").append(product.getId()).append("\">");
		responseHtml.append("<span id=\"product-name\" class=\"header-text\">").append(productName)
				.append("</span></a>");
		responseHtml.append("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a href=\"/pine/").append(sectionKey).append("/?product=").append(product.getId())
				.append("\">");
		responseHtml.append("<span id=\"section-name\" class=\"header-text\">").append(sectionName)
				.append("</span></a></div>");
	}
}
