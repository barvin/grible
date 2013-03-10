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
import java.util.HashMap;
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
@WebServlet("/TableImport")
public class TableImport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TableImport() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));
			Dao dao = new Dao();

			String tableName = fileName.substring(0, fileName.lastIndexOf(".xls"));
			int categoryId = Integer.parseInt(request.getParameter("category"));

			int tableId = dao.insertTable(tableName, TableType.TABLE, categoryId, null, null);
			List<Integer> keyIds = dao.insertKeys(tableId, excelFile.getKeys());
			ArrayList<ArrayList<String>> values = excelFile.getValues();
			List<Integer> rowIds = dao.insertRows(tableId, values.size());
			dao.insertValues(rowIds, keyIds, values);

			if (excelFile.hasPreconditions()) {
				List<String> precondKeyNames = getKeyNames(excelFile.getPrecondition());
				ArrayList<ArrayList<String>> precondValues = getValues(excelFile.getPrecondition());
				int precondTableId = dao.insertTable(null, TableType.PRECONDITION, null, tableId, null);
				List<Integer> precondKeyIds = dao.insertKeys(precondTableId, precondKeyNames);
				List<Integer> precondRowIds = dao.insertRows(precondTableId, 1);
				dao.insertValues(precondRowIds, precondKeyIds, precondValues);
			}

			if (excelFile.hasPostconditions()) {
				List<String> postcondKeyNames = getKeyNames(excelFile.getPostcondition());
				ArrayList<ArrayList<String>> postcondValues = getValues(excelFile.getPostcondition());
				int postcondTableId = dao.insertTable(null, TableType.POSTCONDITION, null, tableId, null);
				List<Integer> postcondKeyIds = dao.insertKeys(postcondTableId, postcondKeyNames);
				List<Integer> postcondRowIds = dao.insertRows(postcondTableId, 1);
				dao.insertValues(postcondRowIds, postcondKeyIds, postcondValues);
			}

			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			String message = "'" + tableName + "' storage was successfully imported.";
			TempVars.setTableImportResult(message);
			response.sendRedirect(destination);
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			TempVars.setTableImportResult(message);
			response.sendRedirect(destination);
		}
	}

	private ArrayList<ArrayList<String>> getValues(HashMap<String, String> preconditions) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> values = new ArrayList<String>();
		for (String value : preconditions.values()) {
			values.add(value);
		}
		result.add(values);
		return result;
	}

	private List<String> getKeyNames(HashMap<String, String> preconditions) {
		List<String> result = new ArrayList<String>();
		for (String keyName : preconditions.keySet()) {
			result.add(keyName);
		}
		return result;
	}
}
