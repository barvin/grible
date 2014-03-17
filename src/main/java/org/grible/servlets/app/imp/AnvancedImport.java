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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.PostgresDao;
import org.grible.dbmigrate.oldmodel.Key;
import org.grible.dbmigrate.oldmodel.Row;
import org.grible.dbmigrate.oldmodel.Value;
import org.grible.excel.ExcelFile;
import org.grible.model.Table;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AnvancedImport")
public class AnvancedImport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnvancedImport() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			Table currTable = (Table) request.getSession(false).getAttribute("importedTable");
			ExcelFile excelFile = (ExcelFile) request.getSession(false).getAttribute("importedFile");

			String option = request.getParameter("option"); // "addtoend",
															// "addfromrow"

			if (!ServletHelper.isJson()) {
				pDao = new PostgresDao();
			}

			if ("addtoend".equals(option)) {
				ArrayList<ArrayList<String>> excelValues = excelFile.getValues();
				if (ServletHelper.isJson()) {
					String[][] oldValues = currTable.getTableJson().getValues();
					String[][] newValues = new String[oldValues.length + excelValues.size()][oldValues[0].length];
					for (int i = 0; i < newValues.length; i++) {
						if (i < oldValues.length) {
							for (int j = 0; j < newValues[0].length; j++) {
								newValues[i][j] = oldValues[i][j];
							}
						} else {
							for (int j = 0; j < newValues[0].length; j++) {
								newValues[i][j] = excelValues.get(i - oldValues.length).get(j);
							}
						}
					}
					currTable.getTableJson().setValues(newValues);
					currTable.save();
				} else {
					List<Integer> rowIds = pDao.addRows(currTable.getId(), pDao.getRows(currTable.getId()).size(),
							excelValues.size());
					pDao.insertValues(rowIds, getKeyIds(pDao.getKeys(currTable.getId())), excelValues);
				}
			} else if ("addfromrow".equals(option)) {
				int startRow = 0;
				if (request.getParameter("startrow") != null) {
					if (StringUtils.isNumeric(request.getParameter("startrow"))) {
						startRow = Integer.parseInt(request.getParameter("startrow"));
					} else {
						throw new Exception("ERROR: Start row is null.");
					}
				} else {
					throw new Exception("ERROR: Start row is null.");
				}

				ArrayList<ArrayList<String>> excelValues = excelFile.getValues();
				int excelValuesCount = excelValues.size();
				
				if (ServletHelper.isJson()) {
					String[][] oldValues = currTable.getTableJson().getValues();

					int rowsCount = oldValues.length;
					if (startRow > rowsCount) {
						throw new Exception("ERROR: Start row is out of range.");
					}
					int limit = rowsCount;
					if (rowsCount < (startRow - 1 + excelValuesCount)) {
						limit = startRow - 1 + excelValuesCount;
					}
					String[][] newValues = new String[limit][oldValues[0].length];

					for (int i = 0; i < limit; i++) {
						if ((i < startRow - 1) || (i >= startRow - 1 + excelValuesCount)) {
							for (int j = 0; j < newValues[0].length; j++) {
								newValues[i][j] = oldValues[i][j];
							}
						} else {
							for (int j = 0; j < newValues[0].length; j++) {
								newValues[i][j] = excelValues.get(i - (startRow - 1)).get(j);
							}
						}
					}
					currTable.getTableJson().setValues(newValues);
					currTable.save();
				} else {
					List<Row> rows = pDao.getRows(currTable.getId());
					int rowsCount = rows.size();
					if (startRow > rowsCount) {
						throw new Exception("ERROR: Start row is out of range.");
					}
					int limit = rowsCount;
					if (rowsCount > (startRow - 1 + excelValuesCount)) {
						limit = startRow - 1 + excelValuesCount;
					}
					for (int i = startRow - 1; i < limit; i++) {
						List<Value> currValues = pDao.getValues(rows.get(i));
						for (int j = 0; j < currValues.size(); j++) {
							Value value = currValues.get(j);
							value.setValue(excelValues.get(0).get(j));
							pDao.updateValue(value);
						}
						excelValues.remove(0);
					}
					if (excelValues.size() > 0) {
						List<Integer> rowIds = pDao.addRows(currTable.getId(), rowsCount, excelValues.size());
						pDao.insertValues(rowIds, getKeyIds(pDao.getKeys(currTable.getId())), excelValues);
					}
				}
			}
			request.getSession(true).setAttribute("importedTable", null);
			request.getSession(false).setAttribute("importedFile", null);
			String message = StringUtils.capitalize(currTable.getType().toString().toLowerCase())
					+ " imported successfully.";
			request.getSession(false).setAttribute("importResult", message);
			out.print("success");
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private List<Integer> getKeyIds(List<Key> keys) {
		List<Integer> result = new ArrayList<Integer>();
		for (Key key : keys) {
			result.add(key.getId());
		}
		return result;
	}
}
