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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.grible.data.Dao;
import org.grible.excel.ExcelFile;
import org.grible.model.Key;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.Value;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AnvancedImport")
public class AnvancedImport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AnvancedImport() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
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

			String option = request.getParameter("option"); // "addtoend", "addfromrow"

			if ("addtoend".equals(option)) {
				ArrayList<ArrayList<String>> values = excelFile.getValues();
				List<Integer> rowIds = Dao.addRows(currTable.getId(), Dao.getRows(currTable.getId()).size(),
						values.size());
				Dao.insertValues(rowIds, getKeyIds(Dao.getKeys(currTable.getId())), values);
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
				List<Row> rows = Dao.getRows(currTable.getId());
				ArrayList<ArrayList<String>> newValues = excelFile.getValues();
				int rowsCount = rows.size();
				int newValuesCount = newValues.size();
				if (startRow > rowsCount) {
					throw new Exception("ERROR: Start row is out of range.");
				}
				int limit = rowsCount;
				if (rowsCount > (startRow + newValuesCount - 1)) {
					limit = newValuesCount;
				}
				for (int i = startRow - 1; i < limit; i++) {
					List<Value> currValues = Dao.getValues(rows.get(i));
					for (int j = 0; j < currValues.size(); j++) {
						Value value = currValues.get(j);
						value.setValue(newValues.get(0).get(j));
						Dao.updateValue(value);
					}
					newValues.remove(0);
				}
				if (newValues.size() > 0) {
					List<Integer> rowIds = Dao.addRows(currTable.getId(), rowsCount, newValues.size());
					Dao.insertValues(rowIds, getKeyIds(Dao.getKeys(currTable.getId())), newValues);
				}
			}
			request.getSession(false).setAttribute("importedTable", null);
			request.getSession(false).setAttribute("importedFile", null);
			String message = StringUtils.capitalize(currTable.getType().toString().toLowerCase()) + " imported successfully.";
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
