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
package org.grible.servlets.app.create;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.model.Product;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddProduct")
public class AddProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddProduct() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}

			String name = request.getParameter("name");
			String path = request.getParameter("path");

			if ("".equals(name)) {
				out.print(Lang.get("error") + ": " + Lang.get("productnameempty"));
			} else if ("".equals(path)) {
				out.print(Lang.get("error") + ": " + Lang.get("productpathempty"));
			} else {
				if (isJson() && (!exists(path))) {
					out.print("folder-not-exists");
				} else {
					Product product = DataManager.getInstance().getDao().getProduct(name);
					if (product != null) {
						out.print(Lang.get("error") + ": " + Lang.get("productwithname") + " '" + name + "' "
								+ Lang.get("alreadyexists"));
					} else {
						path = (path == null) ? "" : path;
						if (isJson() && (new JsonDao().isProductWithPathExists(path))) {
							out.print(Lang.get("error") + ": " + Lang.get("productwithpath") + " '" + path + "' "
									+ Lang.get("alreadyexists"));
						} else {
							DataManager.getInstance().getDao().insertProduct(name, path);
							out.print("success");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		} finally {
			out.flush();
			out.close();
		}
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}

	private boolean exists(String path) {
		return new File(path).exists();
	}
}
