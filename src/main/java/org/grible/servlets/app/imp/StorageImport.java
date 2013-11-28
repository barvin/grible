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
package org.grible.servlets.app.imp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.grible.dao.Dao;
import org.grible.excel.ExcelFile;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class DataFileImport
 */
@MultipartConfig
@WebServlet("/StorageImport")
public class StorageImport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StorageImport() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			String className = request.getParameter("class");
			String message = "";
			int storageId = 0;

			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			String storageName = fileName.substring(0, fileName.lastIndexOf(".xls"));
			int categoryId = Integer.parseInt(request.getParameter("category"));
			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));

			if (Dao.isTableInProductExist(storageName, TableType.STORAGE, categoryId)) {
				Table table = Dao.getTable(storageName, categoryId);

				int currentKeysCount = Dao.getKeys(table.getId()).size();
				int importedKeysCount = excelFile.getKeys().size();
				if (currentKeysCount != importedKeysCount) {
					throw new Exception("Parameters number is different.<br>In the current storage: " + currentKeysCount
							+ ". In the Excel file: " + importedKeysCount + ".");
				}

				request.getSession(false).setAttribute("importedTable", table);
				request.getSession(false).setAttribute("importedFile", excelFile);
				String destination = "/storages/?id=" + table.getId();
				response.sendRedirect(destination);
			} else {
				storageId = Dao.insertTable(storageName, TableType.STORAGE, categoryId, null, className);

				List<Integer> keyIds = Dao.insertKeys(storageId, excelFile.getKeys());
				ArrayList<ArrayList<String>> values = excelFile.getValues();
				List<Integer> rowIds = Dao.insertRows(storageId, values.size());
				Dao.insertValues(rowIds, keyIds, values);

				if (className.equals("")) {
					message = "'" + storageName + "' storage was successfully imported. WARNING: Class name is empty.";
				} else if (!className.endsWith("Info")) {
					message = "'" + storageName
							+ "' storage was successfully imported. WARNING: Class name does not end with 'Info'.";
				} else {
					message = "'" + storageName + "' storage was successfully imported.";
				}

				String destination = "";
				if (storageId > 0) {
					destination = "/storages/?id=" + storageId;
				} else {
					int productId = Integer.parseInt(request.getParameter("product"));
					destination = "/storages/?product=" + productId;
				}
				request.getSession(false).setAttribute("importResult", message);
				response.sendRedirect(destination);
			}
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/storages/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			e.printStackTrace();
			request.getSession(false).setAttribute("importResult", message);
			response.sendRedirect(destination);
		}
	}
}
