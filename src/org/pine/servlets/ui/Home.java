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
			Dao dao = new Dao();
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Pine</title>");
			out.println("<link rel=\"shortcut icon\" href=\"img/favicon.ico\" >");
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
			out.println("<link href=\"css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
			out.println("<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-latest.min.js\"></script>");
			out.println("<script type=\"text/javascript\" src=\"js/home.js\"></script>");
			out.println("<script type=\"text/javascript\" src=\"js/footer.js\"></script>");
			out.println("<script type=\"text/javascript\" src=\"js/jquery.contextMenu.js\"></script>");
			out.println("</head>");
			out.println("<body>");

			if (request.getSession(false).getAttribute("userName") == null) {
				out.println("<div id=\"waiting-bg\">");
				out.println("<div id=\"login-form\">");
				out.println("<img id=\"login-logo\" src=\"img/pine_logo.png\"><br>");
				out.println("<form method=\"post\" action=\"Login\">");
				out.println("<div class=\"table\">");
				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell\">Username:</div>");
				out.println("<div class=\"table-cell dialog-cell\"><input class=\"dialog-edit\" name=\"username\"></div>");
				out.println("</div>");
				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell\">Password:</div>");
				out.println("<div class=\"table-cell dialog-cell\"><input type=\"password\" class=\"dialog-edit\" name=\"pass\"></div>");
				out.println("</div>");
				out.println("</div>");
				if (request.getParameter("url") != null) {
					out.println("<input type=\"hidden\" name=\"url\" value=\"" + request.getParameter("url") + "\">");
				}
				out.println("<div class=\"dialog-buttons right\"><input type=\"submit\" value=\"Log in\" class=\"ui-button\"></div></form>");
				if (request.getSession(false).getAttribute("loginFailed") != null) {
					String message = (String) request.getSession(false).getAttribute("loginFailed");
					out.println("<br><span class=\"dialog-error-message\">" + message + "</span>");
				}
				out.println("</div>");
				out.println("</div>");
			} else {
				String userName = (String) request.getSession(false).getAttribute("userName");
				User user = dao.getUserByName(userName);

				out.print(ServletHelper.getUserPanel(user));
				out.print("<a href=\".\"><img id=\"logo-mini\" src=\"img/pine_logo_mini.png\"></a>");

				if (request.getParameter("product") != null) {
					if (StringUtils.isNumeric(request.getParameter("product"))) {

						int id = Integer.parseInt(request.getParameter("product"));
						Product product = dao.getProduct(id);
						if (product != null) {

							out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
							out.print("<a href=\"?product=" + id
									+ "\"><span id=\"product-name\" class=\"header-text\">" + product.getName()
									+ "</span></a>");

							if (!user.hasAccessToProduct(product.getId())) {
								out.println("<span id=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
								out.println("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
							} else {
								includeSections(out, product);
							}

						} else {
							response.sendRedirect("/pine");
						}
					} else {
						response.sendRedirect("/pine");
					}
				} else {
					out.println("<span id=\"extends-symbol\" style=\"color: rgba(255,255,255,0);\">&nbsp;&gt;&nbsp;</span>");
					out.println("<div class=\"table\">");

					List<Product> products = dao.getProducts();
					for (Product product : products) {
						if (user.hasAccessToProduct(product.getId())) {
							out.println("<div class=\"table-row\">");
							out.println("<div class=\"table-cell section-cell\">");
							out.println("<a href=\"?product=" + product.getId() + "\"><span id=\"" + product.getId()
									+ "\" class=\"section product-item\">" + product.getName() + "</span></a>");
							out.println("</div>");
							out.println("</div>");
						}
					}
					out.println("</div>");

					if (user.isAdmin()) {
						out.println("<div class=\"under-sections\">");
						out.println("<span class=\"top-panel-button button-enabled\" id=\"btn-add-product\">"
								+ "<img src=\"img/add-icon.png\" class=\"top-panel-icon\">"
								+ "&nbsp;&nbsp;Add product</span>");
						out.println("</div>");
					}

				}
			}

			out.println(ServletHelper.getFooter(getServletContext().getRealPath("")));
			out.println(getContextMenus());
			out.println("</body>");
			out.println("</html>");

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

	private void includeSections(PrintWriter out, Product product) {
		out.println("<div class=\"table\">");
		List<Section> sections = Sections.getSections();
		for (Section section : sections) {
			out.println("<div class=\"table-row\">");
			out.println("<div class=\"table-cell section-cell\">");
			out.println("<a href=\"" + section.getKey() + "/?product=" + product.getId()
					+ "\"><span class=\"section\">" + section.getName() + "</span></a>");
			out.println("</div>");
			out.println("<div class=\"table-cell gap\">");
			out.println("</div>");
			out.println("<div class=\"table-cell\">");
			out.println("<div class=\"section-desription\">" + section.getDescription() + "</div>");
			out.println("</div>");
			out.println("</div>");
		}
		out.println("</div>");
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
