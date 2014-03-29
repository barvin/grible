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
package org.grible.servlets.app.get;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.json.ui.UiColumn;
import org.grible.json.ui.UiTable;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.Key;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetTableValues")
public class GetTableValues extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;
	private int productId;
	private int filter;
	private int tableId;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetTableValues() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			tableId = Integer.parseInt(request.getParameter("id"));
			productId = Integer.parseInt(request.getParameter("product"));
			filter = Integer.parseInt(request.getParameter("filter"));
			Table table = null;
			if (isJson()) {
				jDao = new JsonDao();
				table = jDao.getTable(tableId, productId);
			} else {
				pDao = new PostgresDao();
				table = pDao.getTable(tableId);
			}

			UiTable uiTable = new UiTable();
			transformTableToUiTable(table, uiTable);
			out.print(new Gson().toJson(uiTable));
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void transformTableToUiTable(Table table, UiTable uiTable) throws Exception {
		Key[] keys = null;
		if (isJson()) {
			keys = table.getTableJson().getKeys();
		} else {
			keys = table.getKeys();
		}
		uiTable.setKeys(keys);

		List<Table> dataSotages = DataManager.getInstance().getDao().getTablesOfProduct(productId, TableType.STORAGE);
		List<Table> enumerations = DataManager.getInstance().getDao()
				.getTablesOfProduct(productId, TableType.ENUMERATION);

		UiColumn[] columns = new UiColumn[keys.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new UiColumn();
			switch (keys[i].getType()) {
			case TEXT:
				columns[i].setType("text");
				columns[i].setAllowInvalid(true);
				break;

			case STORAGE:
				columns[i].setType("text");
				columns[i].setAllowInvalid(false);
				break;

			case ENUMERATION:
				columns[i].setType("dropdown");
				columns[i].setAllowInvalid(false);

				String[] source = null;
				if (isJson()) {
					Table refTable = jDao.getTable(keys[i].getRefid(), productId);
					source = jDao.getValuesByKeyOrder(refTable, 0).toArray(new String[0]);
				} else {
					Table refTable = pDao.getTable(keys[i].getRefid());
					source = pDao.getValuesByKeyOrder(refTable, 0).toArray(new String[0]);
				}
				columns[i].setSource(source);
				break;

			default:
				break;
			}

		}
		uiTable.setColumns(columns);

		if (!dataSotages.isEmpty()) {
			int[] storageIds = new int[dataSotages.size()];
			String[] storages = new String[dataSotages.size()];
			for (int i = 0; i < dataSotages.size(); i++) {
				storageIds[i] = dataSotages.get(i).getId();
				storages[i] = dataSotages.get(i).getName();
			}
			uiTable.setStorageIds(storageIds);
			uiTable.setStorages(storages);
		}

		if (!enumerations.isEmpty()) {
			int[] enumIds = new int[enumerations.size()];
			String[] enumNames = new String[enumerations.size()];
			for (int i = 0; i < enumerations.size(); i++) {
				enumIds[i] = enumerations.get(i).getId();
				enumNames[i] = enumerations.get(i).getName();
			}
			uiTable.setEnumerationIds(enumIds);
			uiTable.setEnumerations(enumNames);
		}

		String[][] values = null;
		if (isJson()) {
			values = table.getTableJson().getValues();
		} else {
			values = table.getValues();
		}

		int[] rowHeaders = null;

		if (filter > 0) {
			List<Integer> filteredRows = DataManager.getInstance().getDao()
					.getStorageRowsUsedByTable(productId, tableId, filter);
			Integer[] sortedFilteredRows = filteredRows.toArray(new Integer[0]);
			Arrays.sort(sortedFilteredRows);
			String[][] filteredValues = new String[sortedFilteredRows.length][values[0].length];
			int rowCounter = 0;
			for (int row : sortedFilteredRows) {
				for (int key = 0; key < values[0].length; key++) {
					filteredValues[rowCounter][key] = values[row][key];
				}
				rowCounter++;
			}
			values = filteredValues;

			rowHeaders = new int[sortedFilteredRows.length];
			for (int i = 0; i < rowHeaders.length; i++) {
				rowHeaders[i] = sortedFilteredRows[i] + 1;
			}
		} else {
			rowHeaders = new int[values.length];
			for (int i = 0; i < rowHeaders.length; i++) {
				rowHeaders[i] = i + 1;
			}
		}
		uiTable.setValues(values);
		uiTable.setRowHeaders(rowHeaders);
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}

}
