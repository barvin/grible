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
package org.grible.servlets.app.imp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
import org.grible.model.json.Key;
import org.grible.model.json.KeyType;
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
					currentKeysCount = pDao.getOldKeys(table.getId()).size();
				}

				int importedKeysCount = excelFile.getKeys().length;
				if (currentKeysCount != importedKeysCount) {
					throw new Exception("Parameters number is different.<br>In the current table: " + currentKeysCount
							+ ". In the Excel file: " + importedKeysCount + ".");
				}

				request.getSession(true).setAttribute("importedTable", table);
				request.getSession(false).setAttribute("importedFile", excelFile);
				String destination = "/tables/?product=" + productId + "&id=" + table.getId();
				response.sendRedirect(destination);
			} else {
				Key[] keys = excelFile.getKeys();
				String[][] values = excelFile.getValues();
				int tableId = DataManager.getInstance().getDao()
						.insertTable(tableName, TableType.TABLE, category, null, null, keys, values);

				if (excelFile.hasPreconditions()) {
					Key[] precondKeys = getKeys(excelFile.getPrecondition());
					String[][] precondValues = getValues(excelFile.getPrecondition());
					DataManager
							.getInstance()
							.getDao()
							.insertTable(null, TableType.PRECONDITION, category, tableId, null, precondKeys,
									precondValues);
				}

				if (excelFile.hasPostconditions()) {
					Key[] postcondKeys = getKeys(excelFile.getPostcondition());
					String[][] postcondValues = getValues(excelFile.getPostcondition());
					DataManager
							.getInstance()
							.getDao()
							.insertTable(null, TableType.POSTCONDITION, category, tableId, null, postcondKeys,
									postcondValues);
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

	private String[][] getValues(HashMap<String, String> preconditions) {
		String[][] result = new String[1][preconditions.size()];
		String[] values = preconditions.values().toArray(new String[0]);
		for (int i = 0; i < preconditions.size(); i++) {
			result[1][i] = values[i];
		}
		return result;
	}

	private Key[] getKeys(HashMap<String, String> preconditions) {
		Key[] result = new Key[preconditions.size()];
		String[] keyNames = preconditions.keySet().toArray(new String[0]);
		for (int i = 0; i < preconditions.size(); i++) {
			result[i] = new Key(keyNames[i], KeyType.TEXT, 0);
		}
		return result;
	}
}
