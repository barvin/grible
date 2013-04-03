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

import org.apache.commons.lang3.StringUtils;
import org.pine.dao.Dao;
import org.pine.model.Product;
import org.pine.model.User;
import org.pine.servlets.ServletHelper;
import org.pine.settings.GlobalSettings;
import org.pine.uimodel.Section;
import org.pine.uimodel.Sections;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Home() {
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
				response.sendRedirect("/pine/firstlaunch");
				return;
			}

			StringBuilder responseHtml = new StringBuilder();
			responseHtml.append("<!DOCTYPE html>");
			responseHtml.append("<html>");
			responseHtml.append("<head>");
			responseHtml.append("<title>Pine</title>");
			responseHtml.append("<link rel=\"shortcut icon\" href=\"img/favicon.ico\" >");
			responseHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
			responseHtml.append("<link href=\"css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/jquery-1.9.1.min.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/home.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/footer.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/jquery.contextMenu.js\"></script>");
			responseHtml.append("</head>");
			responseHtml.append("<body>");

			if (request.getSession(false).getAttribute("userName") == null) {
				responseHtml.append("<div id=\"waiting-bg\">");
				responseHtml.append("<div id=\"login-form\">");
				responseHtml.append("<img id=\"login-logo\" src=\"img/pine_logo.png\"><br>");
				responseHtml.append("<form method=\"post\" action=\"Login\">");
				responseHtml.append("<div class=\"table\">");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div class=\"table-cell dialog-cell dialog-label\">Username:</div>");
				responseHtml
						.append("<div class=\"table-cell dialog-cell dialog-edit\"><input class=\"dialog-edit\" name=\"username\"></div>");
				responseHtml.append("</div>");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div class=\"table-cell dialog-cell dialog-label\">Password:</div>");
				responseHtml
						.append("<div class=\"table-cell dialog-cell dialog-edit\"><input type=\"password\" class=\"dialog-edit\" name=\"pass\"></div>");
				responseHtml.append("</div>");
				responseHtml.append("</div>");
				if (request.getParameter("url") != null) {
					responseHtml.append("<input type=\"hidden\" name=\"url\" value=\"" + request.getParameter("url")
							+ "\">");
				}
				responseHtml
						.append("<div class=\"dialog-buttons right\"><input type=\"submit\" value=\"Log in\" class=\"ui-button\"></div></form>");
				if (request.getSession(false).getAttribute("loginFailed") != null) {
					String message = (String) request.getSession(false).getAttribute("loginFailed");
					responseHtml.append("<br><span class=\"dialog-error-message\">" + message + "</span>");
				}
				responseHtml.append("</div>");
				responseHtml.append("</div>");
			} else {
				String userName = (String) request.getSession(false).getAttribute("userName");
				User user = Dao.getUserByName(userName);

				responseHtml.append(ServletHelper.getUserPanel(user));
				responseHtml
						.append("<div id=\"breadcrump\" class=\"header-text\"><a href=\".\"><span id=\"home\">Home</span></a>");

				if (request.getParameter("product") != null) {
					if (StringUtils.isNumeric(request.getParameter("product"))) {

						int id = Integer.parseInt(request.getParameter("product"));
						Product product = Dao.getProduct(id);
						if (product != null) {

							responseHtml.append("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
							responseHtml.append("<a href=\"?product=" + id
									+ "\"><span id=\"product-name\">" + product.getName()
									+ "</span></a></div>");

							if (!user.hasAccessToProduct(product.getId())) {
								responseHtml
										.append("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
							} else {
								includeSections(responseHtml, product);
							}

						} else {
							response.sendRedirect("/pine");
						}
					} else {
						response.sendRedirect("/pine");
					}
				} else {
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table\">");

					List<Product> products = Dao.getProducts();
					for (Product product : products) {
						if (user.hasAccessToProduct(product.getId())) {
							responseHtml.append("<div class=\"table-row\">");
							responseHtml.append("<div class=\"table-cell section-cell\">");
							responseHtml.append("<a href=\"?product=" + product.getId() + "\"><span id=\""
									+ product.getId() + "\" class=\"section product-item\">" + product.getName()
									+ "</span></a>");
							responseHtml.append("</div>");
							responseHtml.append("</div>");
						}
					}
					responseHtml.append("</div>");

					if (user.isAdmin()) {
						responseHtml.append("<div class=\"under-sections\">");
						responseHtml.append("<div class=\"icon-button button-enabled\" id=\"btn-add-product\">");
						responseHtml.append("<img src=\"img/add-icon.png\" class=\"icon-enabled\">");
						responseHtml.append("<span class=\"icon-button-text\"> Add product</span></div>");
						responseHtml.append("</div>");
					}
				}
			}

			responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath(""), "img"));
			responseHtml.append(getContextMenus());
			responseHtml.append("</body>");
			responseHtml.append("</html>");
			out.print(responseHtml.toString());

		} catch (Exception e) {
			e.printStackTrace(out);
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void includeSections(StringBuilder responseHtml, Product product) {
		responseHtml.append("<div class=\"table\">");
		List<Section> sections = Sections.getSections();
		for (Section section : sections) {
			responseHtml.append("<div class=\"table-row\">");
			responseHtml.append("<div class=\"table-cell section-cell\">");
			responseHtml.append("<a href=\"" + section.getKey() + "/?product=" + product.getId()
					+ "\"><span class=\"section\">" + section.getName() + "</span></a>");
			responseHtml.append("</div>");
			responseHtml.append("<div class=\"table-cell gap\">");
			responseHtml.append("</div>");
			responseHtml.append("<div class=\"table-cell\">");
			responseHtml.append("<div class=\"section-desription\">" + section.getDescription() + "</div>");
			responseHtml.append("</div>");
			responseHtml.append("</div>");
		}
		responseHtml.append("</div>");
	}

	public String getContextMenus() {
		StringBuilder builder = new StringBuilder();
		builder.append("<ul id=\"productMenu\" class=\"contextMenu\">");
		builder.append("<li class=\"edit\"><a href=\"#edit\">Edit product</a></li>");
		builder.append("<li class=\"delete\"><a href=\"#delete\">Delete product</a></li>");
		builder.append("</ul>");
		return builder.toString();
	}
}
