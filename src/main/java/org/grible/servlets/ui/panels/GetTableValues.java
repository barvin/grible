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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.json.ui.UiColumn;
import org.grible.json.ui.UiInfo;
import org.grible.json.ui.UiTable;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.KeyJson;
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
	private boolean showUsage;
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
			showUsage = table.isShowUsage();

			UiTable uiTable = new UiTable();
			if (isJson()) {
				transformTableToUiTable(table, uiTable);
			} else {
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

		String[][] values = tableJson.getValues();
		uiTable.setValues(values);

		if (showUsage) {
			UiInfo uiInfo = new UiInfo();
			uiInfo.setTables("Used in tables");
			uiInfo.setTables("Used in storages");
			uiTable.setInfo(uiInfo);
		}

		// UiRow[] uiRows = new UiRow[values.length];
		// for (int i = 0; i < values.length; i++) {
		// uiRows[i] = new UiRow();
		// if (tableType == TableType.STORAGE || tableType == TableType.TABLE ||
		// tableType == TableType.ENUMERATION) {
		// UiIndex uiIndex = new UiIndex();
		// uiIndex.setId(i + 1);
		// uiIndex.setOrder(i + 1);
		// uiRows[i].setIndex(uiIndex);
		// }
		// UiValue[] uiValues = new UiValue[values[i].length];
		// for (int j = 0; j < values[i].length; j++) {
		// uiValues[j] = new UiValue();
		// uiValues[j].setStorage(keys[j].getType() == KeyType.STORAGE);
		// uiValues[j].setEnum(keys[j].getType() == KeyType.ENUMERATION);
		// uiValues[j].setRowid(i + 1);
		// uiValues[j].setKeyid(j + 1);
		// uiValues[j].setId(0);
		// uiValues[j].setText(StringEscapeUtils.escapeHtml4(values[i][j]));
		// }
		// uiRows[i].setValues(uiValues);
		// if (showUsage) {
		// if (values[i].length > 0) {
		// List<Table> tables = jDao.getTablesUsingRow(productId, table, (i +
		// 1));
		// UiInfo uiInfo = new UiInfo();
		// uiInfo.setTables(getTestTableOccurences(tables));
		// uiInfo.setStorages(getDataStorageOccurences(tables));
		// uiRows[i].setInfo(uiInfo);
		// }
		// }
		// }
	}

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}

	// private void writeKeys(UiTable uiTable, List<Key> keys) {
	// if (tableType == TableType.STORAGE || tableType == TableType.TABLE ||
	// tableType == TableType.ENUMERATION) {
	// uiTable.setIndex(true);
	// } else {
	// uiTable.setIndex(false);
	// }
	// UiKey[] uiKeys = new UiKey[keys.size()];
	// for (int i = 0; i < keys.size(); i++) {
	// uiKeys[i] = new UiKey();
	// uiKeys[i].setOrder(keys.get(i).getOrder());
	// uiKeys[i].setId(keys.get(i).getId());
	// uiKeys[i].setText(keys.get(i).getName());
	// }
	// uiTable.setKeys(uiKeys);
	// if (showUsage) {
	// UiInfo uiInfo = new UiInfo();
	// uiInfo.setTables("Used in tables");
	// uiInfo.setTables("Used in storages");
	// uiTable.setInfo(uiInfo);
	// }
	// }
	//
	// private void writeValues(UiTable uiTable, ArrayList<ArrayList<Value>>
	// values) throws Exception {
	// UiRow[] uiRows = new UiRow[values.size()];
	// for (int i = 0; i < values.size(); i++) {
	// uiRows[i] = new UiRow();
	// if (tableType == TableType.STORAGE || tableType == TableType.TABLE ||
	// tableType == TableType.ENUMERATION) {
	// UiIndex uiIndex = new UiIndex();
	// uiIndex.setId(values.get(i).get(0).getRowId());
	// uiIndex.setOrder(i + 1);
	// uiRows[i].setIndex(uiIndex);
	// }
	// UiValue[] uiValues = new UiValue[values.get(i).size()];
	// for (int j = 0; j < values.get(i).size(); j++) {
	// uiValues[j] = new UiValue();
	// uiValues[j].setStorage(values.get(i).get(j).isStorage());
	// uiValues[j].setEnum(ServletHelper.isEnumValue(values.get(i).get(j)));
	// uiValues[j].setRowid(values.get(i).get(j).getRowId());
	// uiValues[j].setKeyid(values.get(i).get(j).getKeyId());
	// uiValues[j].setId(values.get(i).get(j).getId());
	// uiValues[j].setText(StringEscapeUtils.escapeHtml4(values.get(i).get(j).getValue()));
	// }
	// uiRows[i].setValues(uiValues);
	// if (showUsage) {
	// if (!values.get(i).isEmpty()) {
	// List<Table> tables =
	// pDao.getTablesUsingRow(values.get(i).get(0).getRowId());
	// UiInfo uiInfo = new UiInfo();
	// uiInfo.setTables(getTestTableOccurences(tables));
	// uiInfo.setStorages(getDataStorageOccurences(tables));
	// uiRows[i].setInfo(uiInfo);
	// }
	// }
	// }
	// uiTable.setValues(uiRows);
	// }

	private String getTestTableOccurences(List<Table> tables) throws Exception {
		List<String> tableNames = new ArrayList<String>();
		for (int i = 0; i < tables.size(); i++) {
			if (TableType.TABLE == tables.get(i).getType()) {
				if (!tableNames.contains(tables.get(i).getName())) {
					tableNames.add(tables.get(i).getName());
				}
			} else if (TableType.PRECONDITION == tables.get(i).getType()
					|| TableType.POSTCONDITION == tables.get(i).getType()) {
				String tableName = isJson() ? jDao.getTable(
						jDao.getParentTableId(tables.get(i).getId(), productId, tables.get(i).getType()), productId)
						.getName() : pDao.getTable(tables.get(i).getParentId()).getName();
				if (!tableNames.contains(tableName)) {
					tableNames.add(tableName);
				}
			}
		}
		return StringUtils.join(tableNames, ", ");
	}

	private String getDataStorageOccurences(List<Table> tables) throws SQLException {
		List<String> tableNames = new ArrayList<String>();
		for (int i = 0; i < tables.size(); i++) {
			if (TableType.STORAGE == tables.get(i).getType()) {
				tableNames.add(tables.get(i).getName());
			}
		}
		return StringUtils.join(tableNames, ", ");
	}
}
