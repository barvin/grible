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

import org.apache.commons.fileupload.util.Streams;
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
@WebServlet("/StorageImport")
public class StorageImport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PostgresDao pDao;
	private JsonDao jDao;

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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			String className = Streams.asString(request.getPart("class").getInputStream());

			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			String storageName = StringUtils.substringBefore(fileName, ".xls");
			int categoryId = Integer.parseInt(request.getParameter("category"));
			String categoryPath = StringHelper.getFolderPathWithoutLastSeparator(request.getParameter("categorypath"));
			int productId = Integer.parseInt(request.getParameter("product"));

			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));

			Category category = null;
			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				category = new Category(categoryPath, TableType.STORAGE, productId);
			} else {
				pDao = new PostgresDao();
				category = new Category(categoryId);
			}

			if (DataManager.getInstance().getDao().isTableInProductExist(storageName, TableType.STORAGE, category)) {

				Table table = null;
				int currentKeysCount = 0;
				if (ServletHelper.isJson()) {
					table = jDao.getTable(storageName, TableType.STORAGE, category);
					currentKeysCount = table.getTableJson().getKeys().length;
				} else {
					table = pDao.getTable(storageName, categoryId);
					currentKeysCount = DataManager.getInstance().getDao().getKeys(table.getId()).size();
				}
				int importedKeysCount = excelFile.getKeys().size();

				if (currentKeysCount != importedKeysCount) {
					throw new Exception("Parameters number is different.<br>In the current storage: "
							+ currentKeysCount + ". In the Excel file: " + importedKeysCount + ".");
				}

				request.getSession(true).setAttribute("importedTable", table);
				request.getSession(false).setAttribute("importedFile", excelFile);
				String destination = "/storages/?product=" + productId + "&id=" + table.getId();
				response.sendRedirect(destination);
			} else {
				int storageId = 0;
				storageId = DataManager.getInstance().getDao()
						.insertTable(storageName, TableType.STORAGE, category, null, className);

				if (ServletHelper.isJson()) {
					Table table = jDao.getTable(storageId, productId);
					table.getTableJson().setKeys(excelFile.getKeys());
					table.getTableJson().setValues(excelFile.getValues());
					table.save();
				} else {
					List<Integer> keyIds = DataManager.getInstance().getDao()
							.insertKeys(storageId, excelFile.getKeys());
					ArrayList<ArrayList<String>> values = excelFile.getValues();
					List<Integer> rowIds = DataManager.getInstance().getDao().insertRows(storageId, values.size());
					DataManager.getInstance().getDao().insertValues(rowIds, keyIds, values);
				}

				String message = "";
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
					destination = "/storages/?product=" + productId + "&id=" + storageId;
				} else {
					destination = "/storages/?product=" + productId;
				}
				request.getSession(true).setAttribute("importResult", message);
				response.sendRedirect(destination);
			}
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/storages/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			e.printStackTrace();
			request.getSession(true).setAttribute("importResult", message);
			response.sendRedirect(destination);
		}
	}
}
