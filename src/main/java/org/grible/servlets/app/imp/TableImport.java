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

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.excel.ExcelFile;
import org.grible.helpers.StringHelper;
import org.grible.model.Category;
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
	private PostgresDao pDao;
	private JsonDao jDao;

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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			String tableName = StringUtils.substringBefore(fileName, ".xls");
			int categoryId = Integer.parseInt(request.getParameter("category"));
			String categoryPath = StringHelper.getFolderPathWithoutLastSeparator(request.getParameter("categorypath"));
			int productId = Integer.parseInt(request.getParameter("product"));

			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));

			Category category = null;
			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				category = new Category(categoryPath, TableType.TABLE, productId);
			} else {
				pDao = new PostgresDao();
				category = new Category(categoryId);
			}

			if (DataManager.getInstance().getDao().isTableInProductExist(tableName, TableType.TABLE, category)) {
				Table table = null;
				int currentKeysCount = 0;
				if (ServletHelper.isJson()) {
					table = jDao.getTable(tableName, TableType.TABLE, category);
					currentKeysCount = table.getTableJson().getKeys().length;
				} else {
					table = pDao.getTable(tableName, categoryId);
					currentKeysCount = pDao.getKeys(table.getId()).size();
				}

				int importedKeysCount = excelFile.getKeys().size();
				if (currentKeysCount != importedKeysCount) {
					throw new Exception("Parameters number is different.<br>In the current table: " + currentKeysCount
							+ ". In the Excel file: " + importedKeysCount + ".");
				}

				request.getSession(true).setAttribute("importedTable", table);
				request.getSession(false).setAttribute("importedFile", excelFile);
				String destination = "/tables/?product=" + productId + "&id=" + table.getId();
				response.sendRedirect(destination);
			} else {
				int tableId = DataManager.getInstance().getDao()
						.insertTable(tableName, TableType.TABLE, category, null, null);

				if (ServletHelper.isJson()) {
					Table table = jDao.getTable(tableId, productId);
					table.getTableJson().setKeys(excelFile.getKeys());
					table.getTableJson().setValues(excelFile.getValues());
					table.save();
				} else {
					List<Integer> keyIds = pDao.insertKeys(tableId, excelFile.getKeys());
					ArrayList<ArrayList<String>> values = excelFile.getValues();
					List<Integer> rowIds = pDao.insertRows(tableId, values.size());
					pDao.insertValues(rowIds, keyIds, values);
				}

				if (excelFile.hasPreconditions()) {
					List<String> precondKeyNames = getKeyNames(excelFile.getPrecondition());
					ArrayList<ArrayList<String>> precondValues = getValues(excelFile.getPrecondition());
					int precondTableId = DataManager.getInstance().getDao()
							.insertTable(null, TableType.PRECONDITION, category, tableId, null);

					if (ServletHelper.isJson()) {
						Table table = jDao.getTable(precondTableId, productId);
						table.getTableJson().setKeys(precondKeyNames);
						table.getTableJson().setValues(precondValues);
						table.save();
					} else {
						List<Integer> precondKeyIds = pDao.insertKeys(precondTableId, precondKeyNames);
						List<Integer> precondRowIds = pDao.insertRows(precondTableId, 1);
						pDao.insertValues(precondRowIds, precondKeyIds, precondValues);
					}
				}

				if (excelFile.hasPostconditions()) {
					List<String> postcondKeyNames = getKeyNames(excelFile.getPostcondition());
					ArrayList<ArrayList<String>> postcondValues = getValues(excelFile.getPostcondition());
					int postcondTableId = DataManager.getInstance().getDao()
							.insertTable(null, TableType.POSTCONDITION, category, tableId, null);

					if (ServletHelper.isJson()) {
						Table table = jDao.getTable(postcondTableId, productId);
						table.getTableJson().setKeys(postcondKeyNames);
						table.getTableJson().setValues(postcondValues);
						table.save();
					} else {
						List<Integer> postcondKeyIds = pDao.insertKeys(postcondTableId, postcondKeyNames);
						List<Integer> postcondRowIds = pDao.insertRows(postcondTableId, 1);
						pDao.insertValues(postcondRowIds, postcondKeyIds, postcondValues);
					}
				}

				String message = "'" + tableName + "' table was successfully imported.";
				request.getSession(true).setAttribute("importResult", message);
				String destination = "/tables/?product=" + productId + "&id=" + tableId;
				response.sendRedirect(destination);
			}
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/tables/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			e.printStackTrace();
			request.getSession(true).setAttribute("importResult", message);
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
