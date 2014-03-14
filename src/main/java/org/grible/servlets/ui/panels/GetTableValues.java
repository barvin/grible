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
package org.grible.servlets.ui.panels;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.grible.model.json.KeyJson;
import org.grible.model.json.KeyType;
import org.grible.model.json.TableJson;
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
	private TableType tableType;
	private JsonDao jDao;
	private PostgresDao pDao;
	private int productId;

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
			int tableId = Integer.parseInt(request.getParameter("id"));
			Table table = null;
			if (isJson()) {
				jDao = new JsonDao();
				productId = Integer.parseInt(request.getParameter("product"));
				table = jDao.getTable(tableId, productId);
			} else {
				pDao = new PostgresDao();
				table = pDao.getTable(tableId);
			}

			tableType = table.getType();

			UiTable uiTable = new UiTable();
			if (isJson()) {
				transformTableToUiTable(table, uiTable);
			} else {
				// TODO: implement PostgreSQL part.
				
				// List<Key> keys = pDao.getKeys(tableId);
				// writeKeys(uiTable, keys);
				//
				// List<Row> rows = pDao.getRows(tableId);
				// ArrayList<ArrayList<Value>> values = new
				// ArrayList<ArrayList<Value>>();
				// for (Row row : rows) {
				// values.add(pDao.getValues(row));
				// }
				// writeValues(uiTable, values);
				uiTable.setIndex(true);
				uiTable.setKeys(new KeyJson[] { new KeyJson("editme", KeyType.TEXT, 0) });
				UiColumn[] columns = new UiColumn[1];
				columns[0] = new UiColumn();
				columns[0].setType("text");
				columns[0].setAllowInvalid(true);
				uiTable.setColumns(columns);
				uiTable.setValues(new String[][] { { "" } });
			}
			out.print(new Gson().toJson(uiTable));
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void transformTableToUiTable(Table table, UiTable uiTable) throws Exception {
		if (tableType == TableType.STORAGE || tableType == TableType.TABLE || tableType == TableType.ENUMERATION) {
			uiTable.setIndex(true);
		} else {
			uiTable.setIndex(false);
		}
		TableJson tableJson = table.getTableJson();
		KeyJson[] keys = tableJson.getKeys();
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
				Table refTable = jDao.getTable(keys[i].getRefid(), productId);
				String[] source = jDao.getValuesByKeyOrder(refTable, 0).toArray(new String[0]);
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

		String[][] values = tableJson.getValues();
		uiTable.setValues(values);
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}

}
