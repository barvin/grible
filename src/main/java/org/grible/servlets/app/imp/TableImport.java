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

import org.grible.dao.DataManager;
import org.grible.excel.ExcelFile;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			String tableName = fileName.substring(0, fileName.lastIndexOf(".xls"));
			int categoryId = Integer.parseInt(request.getParameter("category"));
			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));

			if (DataManager.getInstance().getDao().isTableInProductExist(tableName, TableType.TABLE, categoryId)) {
				Table table = DataManager.getInstance().getDao().getTable(tableName, categoryId);

				int currentKeysCount = DataManager.getInstance().getDao().getKeys(table.getId()).size();
				int importedKeysCount = excelFile.getKeys().size();
				if (currentKeysCount != importedKeysCount) {
					throw new Exception("Parameters number is different.<br>In the current table: " + currentKeysCount
							+ ". In the Excel file: " + importedKeysCount + ".");
				}

				request.getSession(false).setAttribute("importedTable", table);
				request.getSession(false).setAttribute("importedFile", excelFile);
				String destination = "/tables/?id=" + table.getId();
				response.sendRedirect(destination);
			} else {
				int tableId = DataManager.getInstance().getDao().insertTable(tableName, TableType.TABLE, categoryId, null, null);
				List<Integer> keyIds = DataManager.getInstance().getDao().insertKeys(tableId, excelFile.getKeys());
				ArrayList<ArrayList<String>> values = excelFile.getValues();
				List<Integer> rowIds = DataManager.getInstance().getDao().insertRows(tableId, values.size());
				DataManager.getInstance().getDao().insertValues(rowIds, keyIds, values);

				if (excelFile.hasPreconditions()) {
					List<String> precondKeyNames = getKeyNames(excelFile.getPrecondition());
					ArrayList<ArrayList<String>> precondValues = getValues(excelFile.getPrecondition());
					int precondTableId = DataManager.getInstance().getDao().insertTable(null, TableType.PRECONDITION, null, tableId, null);
					List<Integer> precondKeyIds = DataManager.getInstance().getDao().insertKeys(precondTableId, precondKeyNames);
					List<Integer> precondRowIds = DataManager.getInstance().getDao().insertRows(precondTableId, 1);
					DataManager.getInstance().getDao().insertValues(precondRowIds, precondKeyIds, precondValues);
				}

				if (excelFile.hasPostconditions()) {
					List<String> postcondKeyNames = getKeyNames(excelFile.getPostcondition());
					ArrayList<ArrayList<String>> postcondValues = getValues(excelFile.getPostcondition());
					int postcondTableId = DataManager.getInstance().getDao().insertTable(null, TableType.POSTCONDITION, null, tableId, null);
					List<Integer> postcondKeyIds = DataManager.getInstance().getDao().insertKeys(postcondTableId, postcondKeyNames);
					List<Integer> postcondRowIds = DataManager.getInstance().getDao().insertRows(postcondTableId, 1);
					DataManager.getInstance().getDao().insertValues(postcondRowIds, postcondKeyIds, postcondValues);
				}

				String message = "'" + tableName + "' storage was successfully imported.";
				request.getSession(false).setAttribute("importResult", message);
				String destination = "/tables/?id=" + tableId;
				response.sendRedirect(destination);
			}
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/tables/?product=" + productId;
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
