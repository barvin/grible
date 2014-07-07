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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Product;
import org.grible.model.User;
import org.grible.servlets.ServletHelper;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;
import org.grible.uimodel.Section;
import org.grible.uimodel.Sections;

import com.google.gson.Gson;

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
				response.sendRedirect("/firstlaunch");
				return;
			}

			StringBuilder responseHtml = new StringBuilder();
			responseHtml.append("<!DOCTYPE html>");
			responseHtml.append("<html>");
			responseHtml.append("<head>");
			responseHtml.append("<title>Grible</title>");
			responseHtml.append("<link rel=\"shortcut icon\" href=\"img/favicon.ico\" >");
			responseHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
			responseHtml.append("<link href=\"css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/jquery-1.11.0.min.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/jquery-ui.min.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"lang/current.json\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/home.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/jquery.contextMenu.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/noty/jquery.noty.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/noty/top.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/noty/defaultVarsHome.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\" src=\"js/noty/default.js\"></script>");
			responseHtml.append("<script type=\"text/javascript\">");
			responseHtml.append("var appType = \"")
					.append(GlobalSettings.getInstance().getAppType().toString().toLowerCase()).append("\";");
			if (!isMultipleUsers()) {
				responseHtml.append("var productsWhosePathsNotExist = ").append(getProductsWhosePathsNotExist())
						.append(";");
			} else {
				responseHtml.append("var productsWhosePathsNotExist = [];");
			}
			responseHtml.append("</script>");
			responseHtml.append("</head>");
			responseHtml.append("<body>");

			if (isMultipleUsers() && request.getSession(false).getAttribute("userName") == null) {
				responseHtml.append("<div id=\"login-form\" class=\"table\">");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div id=\"login-grible-logo\" class=\"table-cell\">");
				responseHtml.append("<img id=\"login-logo\" src=\"img/grible_logo.png\">");
				responseHtml.append("</div>");
				responseHtml.append("<div id=\"outer-credentials\" class=\"table-cell\">");
				responseHtml.append("<form method=\"post\" action=\"Login\">");
				responseHtml.append("<div id=\"credentials\" class=\"table\">");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div class=\"table-cell dialog-cell dialog-label\">");
				responseHtml.append(Lang.get("username"));
				responseHtml.append(":</div>");
				responseHtml.append("<div class=\"table-cell dialog-cell dialog-edit\">");
				responseHtml.append("<input class=\"dialog-edit\" name=\"username\" style=\"width: 250px\"></div>");
				responseHtml.append("</div>");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div class=\"table-cell dialog-cell dialog-label\">");
				responseHtml.append(Lang.get("password"));
				responseHtml.append(":</div>");
				responseHtml.append("<div class=\"table-cell dialog-cell dialog-edit\">");
				responseHtml
						.append("<input type=\"password\" class=\"dialog-edit\" name=\"pass\" style=\"width: 250px\"></div>");
				responseHtml.append("</div>");
				responseHtml.append("</div>");
				if (request.getParameter("url") != null) {
					responseHtml.append("<input type=\"hidden\" name=\"url\" value=\"" + request.getParameter("url")
							+ "\">");
				}
				responseHtml.append("<div class=\"login-dialog-buttons table\">");
				responseHtml.append("<div class=\"table-row\">");
				responseHtml.append("<div id=\"outer-dialog-error-message\" class=\"table-cell\">");
				if (request.getSession(false).getAttribute("loginFailed") != null) {
					String message = (String) request.getSession(false).getAttribute("loginFailed");
					responseHtml.append("<span class=\"dialog-error-message\">" + message + "</span>");
				}
				responseHtml.append("</div>");
				responseHtml.append("<div class=\"table-cell\">");

				responseHtml.append("<script type=\"text/javascript\">document.write(");
				responseHtml.append("'<input type=\"submit\" value=\"'+ lang['login'] + '\" ");
				responseHtml.append("class=\"ui-button\">')</script>");

				responseHtml.append("</div>");
				responseHtml.append("</div>");
				responseHtml.append("</div>");
				responseHtml.append("</form>");
				responseHtml.append("</div>");
				responseHtml.append("</div>");
				responseHtml.append("</div>");
			} else {
				User user = null;
				if (isMultipleUsers()) {
					String userName = (String) request.getSession(false).getAttribute("userName");
					user = new PostgresDao().getUserByName(userName);
					responseHtml.append(ServletHelper.getUserPanel(user));
				} else {
					responseHtml.append(ServletHelper.getUserPanel());
				}

				responseHtml.append("<div id=\"breadcrumb\" class=\"header-text\">");
				responseHtml.append("<span id=\"home-image\"><img src=\"img/grible_logo_mini.png\"></span>");
				responseHtml.append("<a href=\".\"><span id=\"home\">");
				responseHtml.append(Lang.get("home"));
				responseHtml.append("</span></a>");

				if (request.getParameter("product") != null) {
					if (StringUtils.isNumeric(request.getParameter("product"))) {

						int id = Integer.parseInt(request.getParameter("product"));
						Product product = DataManager.getInstance().getDao().getProduct(id);
						if (product != null) {
							responseHtml.replace(responseHtml.indexOf("id=\"home\">") + 9,
									responseHtml.indexOf("id=\"home\">") + 10, " class=\"link-infront\">");
							responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
							responseHtml.append("<a href=\"?product=" + id + "\"><span id=\"product-name\">");
							responseHtml.append(product.getName());
							responseHtml.append("</span></a></div>");

							if (isMultipleUsers() && (!user.hasAccessToProduct(product.getId()))) {
								responseHtml.append("<br/><br/>");
								responseHtml.append("<div class=\"error-message\">");
								responseHtml.append(Lang.get("nopermissions") + "</div>");
							} else {
								includeSections(responseHtml, product);
							}

						} else {
							response.sendRedirect("/");
						}
					} else {
						response.sendRedirect("/");
					}
				} else {
					responseHtml.append("</div>");
					responseHtml.append("<div class=\"table\">");

					List<Product> products = DataManager.getInstance().getDao().getProducts();
					for (Product product : products) {
						if ((isMultipleUsers() && user.hasAccessToProduct(product.getId())) || (!isMultipleUsers())) {
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

					if ((isMultipleUsers() && user.isAdmin()) || (!isMultipleUsers())) {
						responseHtml.append("<div class=\"under-sections\">");
						responseHtml.append("<div class=\"icon-button button-enabled\" id=\"btn-add-product\">");
						responseHtml.append("<img src=\"img/add-icon.png\" class=\"icon-enabled\">");
						responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("addproduct")
								+ "</span></div>");
						responseHtml.append("<p><img src=\"img/info-icon.png\"><span class=\"msg-text\">"
								+ "<a href=\"#\" id=\"lnk-product-info\">" + Lang.get("whatisproduct")
								+ "</a></span></p>");
						responseHtml.append("</div>");
					}

					responseHtml.append("<div id=\"video-tutorial-msg\">");
					responseHtml.append("<img src=\"img/info-icon.png\"> ");
					responseHtml.append("<span class=\"msg-text\">" + Lang.get("newtogrible1"));
					responseHtml.append("<a href=\"http://www.grible.org/docs.php#video\" ");
					responseHtml.append("target=\"_blank\">" + Lang.get("video") + "</a>");
					responseHtml.append(Lang.get("newtogrible2"));
					responseHtml.append("</span>");
					responseHtml.append("</div>");
				}
				responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath("")));
			}

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

	private String getProductsWhosePathsNotExist() throws Exception {
		List<Product> products = new JsonDao().getProducts();
		List<String> productList = new ArrayList<>();
		for (Product product : products) {
			File dir = new File(product.getPath());
			if (!dir.canWrite()) {
				productList.add(product.getName());
			}
		}
		String[] productArray = new String[productList.size()];
		for (int i = 0; i < productArray.length; i++) {
			productArray[i] = productList.get(i);
		}
		return new Gson().toJson(productArray, String[].class);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private boolean isMultipleUsers() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.POSTGRESQL;
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
}
