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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.excel.ExcelFile;
import org.grible.model.Table;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/ExportToExcel")
public class ExportToExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExportToExcel() {
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
			int tableId = Integer.parseInt(request.getParameter("id"));
			Table table = null;
			if (ServletHelper.isJson()) {
				int productId = Integer.parseInt(request.getParameter("product"));
				table = new JsonDao().getTable(tableId, productId);
			} else {
				table = new PostgresDao().getTable(tableId);
			}

			ExcelFile excelFile = new ExcelFile();
			File exportDir = new File(getServletContext().getRealPath("") + "/export");
			if (!exportDir.exists()) {
				exportDir.mkdir();
			} else {
				exportDir.delete();
				exportDir.mkdir();
			}
			String filePath = exportDir + "/" + table.getName() + ".xls";
			out.print(excelFile.saveToFile(table, filePath));
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
