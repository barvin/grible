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
import java.util.HashMap;
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			String tableName = fileName.substring(0, fileName.lastIndexOf(".xls"));
			int categoryId = Integer.parseInt(request.getParameter("category"));
			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));

			if (Dao.isTableInProductExist(tableName, TableType.TABLE, categoryId)) {
				Table table = Dao.getTable(tableName, categoryId);

				int currentKeysCount = Dao.getKeys(table.getId()).size();
				int importedKeysCount = excelFile.getKeys().size();
				if (currentKeysCount != importedKeysCount) {
					throw new Exception("Parameters number is different.<br>In the current table: " + currentKeysCount
							+ ". In the Excel file: " + importedKeysCount + ".");
				}

				request.getSession(false).setAttribute("importedTable", table);
				request.getSession(false).setAttribute("importedFile", excelFile);
				String destination = "/grible/tables/?id=" + table.getId();
				response.sendRedirect(destination);
			} else {
				int tableId = Dao.insertTable(tableName, TableType.TABLE, categoryId, null, null);
				List<Integer> keyIds = Dao.insertKeys(tableId, excelFile.getKeys());
				ArrayList<ArrayList<String>> values = excelFile.getValues();
				List<Integer> rowIds = Dao.insertRows(tableId, values.size());
				Dao.insertValues(rowIds, keyIds, values);

				if (excelFile.hasPreconditions()) {
					List<String> precondKeyNames = getKeyNames(excelFile.getPrecondition());
					ArrayList<ArrayList<String>> precondValues = getValues(excelFile.getPrecondition());
					int precondTableId = Dao.insertTable(null, TableType.PRECONDITION, null, tableId, null);
					List<Integer> precondKeyIds = Dao.insertKeys(precondTableId, precondKeyNames);
					List<Integer> precondRowIds = Dao.insertRows(precondTableId, 1);
					Dao.insertValues(precondRowIds, precondKeyIds, precondValues);
				}

				if (excelFile.hasPostconditions()) {
					List<String> postcondKeyNames = getKeyNames(excelFile.getPostcondition());
					ArrayList<ArrayList<String>> postcondValues = getValues(excelFile.getPostcondition());
					int postcondTableId = Dao.insertTable(null, TableType.POSTCONDITION, null, tableId, null);
					List<Integer> postcondKeyIds = Dao.insertKeys(postcondTableId, postcondKeyNames);
					List<Integer> postcondRowIds = Dao.insertRows(postcondTableId, 1);
					Dao.insertValues(postcondRowIds, postcondKeyIds, postcondValues);
				}

				String message = "'" + tableName + "' storage was successfully imported.";
				request.getSession(false).setAttribute("importResult", message);
				String destination = "/grible/tables/?id=" + tableId;
				response.sendRedirect(destination);
			}
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/grible/tables/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			e.printStackTrace();
			request.getSession(false).setAttribute("importResult", message);
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
