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
package org.grible.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.model.Product;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetEditProductDialog")
public class GetEditProductDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetEditProductDialog() {
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
			int id = Integer.parseInt(request.getParameter("id"));
			Product product = DataManager.getInstance().getDao().getProduct(id);
			String name = product.getName();

			out.println("<div id=\"edit-product-dialog\" class=\"ui-dialog\">");
			out.println("<div class=\"ui-dialog-title\">Edit product</div>");
			out.println("<div class=\"ui-dialog-content\">");
			out.println("<div class=\"table\">");
			out.println("<div class=\"table-row\">");
			out.println("<div class=\"table-cell dialog-cell dialog-label\">Name:</div>");
			out.println("<div class=\"table-cell dialog-cell dialog-edit\"><input class=\"product-name dialog-edit\" value=\""
					+ name + "\" size=\"50\"></div>");
			out.println("</div>");

			if (isPathPresent()) {
				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell dialog-cell dialog-label\">Path:</div>");
				out.println("<div class=\"table-cell dialog-cell dialog-edit\"><input class=\"product-path dialog-edit\" disabled=\"disabled\" value=\""
						+ product.getPath() + "\" size=\"50\"></div>");
				out.println("</div>");
			}

			out.println("</div>");
			out.println("<div class=\"dialog-buttons right\">");
			out.println("<button id=\"dialog-btn-edit-product\" product-id=\"" + id
					+ "\" class=\"ui-button\">Save</button> ");
			out.println("<button class=\"ui-button btn-cancel\">Cancel</button> ");
			out.println("</div></div></div>");
		} catch (Exception e) {
			out.print("ERROR: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private boolean isPathPresent() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}
}
