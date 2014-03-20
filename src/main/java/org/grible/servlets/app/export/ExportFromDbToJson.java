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
package org.grible.servlets.app.export;

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
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Category;
import org.grible.model.Product;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.settings.GlobalSettings;
import org.grible.uimodel.Section;
import org.grible.uimodel.Sections;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/ExportFromDbToJson")
public class ExportFromDbToJson extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportFromDbToJson() {
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
			String dest = request.getParameter("dest");
			if (!dest.endsWith(File.separator)) {
				dest += File.separator;
			}

			PostgresDao pDao = new PostgresDao();
			JsonDao jDao = new JsonDao();
			List<Product> products = pDao.getProducts();

			for (Product product : products) {
				String productPath = dest + product.getName();
				new File(productPath).mkdirs();
				int productId = jDao.insertProduct(product.getName(), productPath);
				List<Category> tableCategories = pDao.getAllCategories(productId, TableType.TABLE);
				for (Category category : tableCategories) {
					jDao.insertCategory(TableType.TABLE, productId, category.getName(), null,
							StringUtils.substringBeforeLast(category.getPath(), File.separator) + File.separator);
				}
			}

			GlobalSettings.getInstance().getConfigJson().setProducts(new ArrayList<Product>());
			GlobalSettings.getInstance().getConfigJson().save();

			out.print("Done.");
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
