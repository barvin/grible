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
package org.pine.servlets.app.imp;

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

import org.pine.dao.Dao;
import org.pine.excel.ExcelFile;
import org.pine.excel.TempVars;
import org.pine.model.TableType;
import org.pine.servlets.ServletHelper;


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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			String className = request.getParameter("class");
			String message = "";

			if (!className.endsWith("Info")) {
				message = "ERROR: class name does not end with 'Info'.";
			} else {
				Part filePart = request.getPart("file");
				String fileName = ServletHelper.getFilename(filePart);
				InputStream filecontent = filePart.getInputStream();
				ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));
				
				
				String storageName = fileName.substring(0, fileName.lastIndexOf(".xls"));
				int categoryId = Integer.parseInt(request.getParameter("category"));
				int storageId = Dao.insertTable(storageName, TableType.STORAGE, categoryId, null, className);
				
				List<Integer> keyIds = Dao.insertKeys(storageId, excelFile.getKeys());
				ArrayList<ArrayList<String>> values = excelFile.getValues();
				List<Integer> rowIds = Dao.insertRows(storageId, values.size());
				Dao.insertValues(rowIds, keyIds, values);

				message = "'" + storageName + "' storage was successfully imported.";
			}

			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			TempVars.setStorageImportResult(message);
			response.sendRedirect(destination);
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			TempVars.setStorageImportResult(message);
			response.sendRedirect(destination);
		}
	}
}
