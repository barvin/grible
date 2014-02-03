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

import org.grible.dao.DataManager;
import org.grible.dao.PostgresDao;
import org.grible.model.User;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/enumerations/")
public class Enumerations extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Enumerations() {
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
			responseHtml.append("<title>Enumerations - Grible</title>");
			responseHtml.append(ServletHelper.getIncludes());

			int productId;
			int tableId;
			if (request.getParameter("id") != null) {
				tableId = Integer.parseInt(request.getParameter("id"));
				if (isMultipleUsers()) {
					productId = DataManager.getInstance().getDao().getProductIdByPrimaryTableId(tableId);
				} else {
					productId = Integer.parseInt(request.getParameter("product"));
				}
			} else {
				productId = Integer.parseInt(request.getParameter("product"));
				tableId = 0;
			}

			User user = null;
			if (isMultipleUsers()) {
				String userName = (String) request.getSession(false).getAttribute("userName");
				user = new PostgresDao().getUserByName(userName);
			}

			if (isMultipleUsers() && (!user.hasAccessToProduct(productId))) {
				responseHtml.append("<a href=\".\"><span id=\"home\" class=\"header-text\">Home</span></a>");
				responseHtml.append("<br/><br/>"
						+ "<div class=\"error-message\">You do not have permissions to access this page.</div>");
			} else {
				responseHtml.append("<script type=\"text/javascript\">");
				responseHtml.append("var productId = \"").append(productId).append("\";");
				responseHtml.append("var tableId = \"").append(tableId).append("\";");
				responseHtml.append("var tableType = \"enumeration\";");
				if (isMultipleUsers()) {
					responseHtml.append("var isTooltipOnClick = ").append(user.isTooltipOnClick()).append(";");
				} else {
					responseHtml.append("var isTooltipOnClick = ")
							.append(GlobalSettings.getInstance().getConfigJson().isTooltipOnClick()).append(";");
				}
				responseHtml.append("var appType = \"")
						.append(GlobalSettings.getInstance().getAppType().toString().toLowerCase()).append("\";");
				responseHtml.append("</script>");
				responseHtml.append("<script type=\"text/javascript\" src=\"../js/dataCenter.js\"></script>");
				ServletHelper.showImportResult(request, responseHtml, tableId);
				ServletHelper.showAdvancedImportDialog(request, responseHtml);

				responseHtml.append("</head>");
				responseHtml.append("<body>");
				if (isMultipleUsers()) {
					responseHtml.append(ServletHelper.getUserPanel(user));
				} else {
					responseHtml.append(ServletHelper.getUserPanel());
				}
				responseHtml.append(ServletHelper.getBreadCrumb("enumerations", DataManager.getInstance().getDao()
						.getProduct(productId), "../img"));
				responseHtml.append(ServletHelper.getMain());
				responseHtml.append(ServletHelper.getContextMenus("enumeration"));
				responseHtml.append(ServletHelper.getLoadingGif());
			}
			responseHtml.append(ServletHelper.getFooter(getServletContext().getRealPath("")));
			responseHtml.append("</body>");
			responseHtml.append("</html>");
			out.print(responseHtml.toString());
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

	private boolean isMultipleUsers() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.POSTGRESQL;
	}
}
