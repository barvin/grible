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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.grible.data.Dao;
import org.grible.model.Key;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.Value;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetTableValues")
public class GetTableValues extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private boolean showUsage;
	private TableType tableType;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetTableValues() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			StringBuilder responseHtml = new StringBuilder();
			int tableId = Integer.parseInt(request.getParameter("id"));
			Table table = Dao.getTable(tableId);
			tableType = table.getType();
			showUsage = table.isShowUsage();

			List<Key> keys = Dao.getKeys(tableId);
			writeKeys(responseHtml, keys);

			List<Row> rows = Dao.getRows(tableId);
			ArrayList<ArrayList<Value>> values = new ArrayList<ArrayList<Value>>();
			for (Row row : rows) {
				values.add(Dao.getValues(row));
			}
			writeValues(responseHtml, values);
			out.print(responseHtml.toString());
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void writeKeys(StringBuilder responseHtml, List<Key> keys) {
		responseHtml.append("{\"isIndex\":");
		if (tableType == TableType.STORAGE || tableType == TableType.TABLE || tableType == TableType.ENUMERATION) {
			responseHtml.append("true");
		} else {
			responseHtml.append("false");
		}
		responseHtml.append(",\"keys\":[");
		for (Key key : keys) {
			responseHtml.append("{\"order\":").append(key.getOrder()).append(",\"id\":").append(key.getId())
					.append(",\"text\":\"").append(key.getName()).append("\"},");
		}
		responseHtml.deleteCharAt(responseHtml.length() - 1);
		responseHtml.append("],");
		if (showUsage) {
			responseHtml.append("\"info\":{\"tables\":\"Used in tables\",\"storages\":\"Used in storages\"},");
		}
	}

	private void writeValues(StringBuilder responseHtml, ArrayList<ArrayList<Value>> values) throws SQLException {
		responseHtml.append("\"values\":[");
		int i = 1;
		for (ArrayList<Value> valuesRow : values) {
			responseHtml.append("{");
			if (tableType == TableType.STORAGE || tableType == TableType.TABLE || tableType == TableType.ENUMERATION) {
				responseHtml.append("\"index\":{\"id\":").append(valuesRow.get(0).getRowId()).append(",\"order\":")
						.append(i++).append("},");
			}
			responseHtml.append("\"values\":[");
			for (Value value : valuesRow) {
				responseHtml.append("{\"isStorage\":");
				if (value.isStorage()) {
					responseHtml.append("true");
				} else {
					responseHtml.append("false");
				}
				responseHtml.append(",\"isEnum\":");
				if (ServletHelper.isEnumValue(value)) {
					responseHtml.append("true");
				} else {
					responseHtml.append("false");
				}
				responseHtml.append(",\"rowid\":").append(value.getRowId()).append(",\"keyid\":")
						.append(value.getKeyId()).append(",\"id\":").append(value.getId()).append(",\"text\":\"")
						.append(StringEscapeUtils.escapeHtml(value.getValue())).append("\"},");
			}
			responseHtml.deleteCharAt(responseHtml.length() - 1);
			responseHtml.append("]");
			if (showUsage) {
				if (!valuesRow.isEmpty()) {
					List<Table> tables = Dao.getTablesUsingRow(valuesRow.get(0).getRowId());
					responseHtml.append(",\"info\":{\"tables\":\"").append(getTestTableOccurences(tables))
							.append("\",\"storages\":\"").append(getDataStorageOccurences(tables)).append("\"}");
				}
			}
			responseHtml.append("},");
		}
		responseHtml.deleteCharAt(responseHtml.length() - 1);
		responseHtml.append("]}");
	}

	private String getTestTableOccurences(List<Table> tables) throws SQLException {
		List<String> tableNames = new ArrayList<String>();
		for (int i = 0; i < tables.size(); i++) {
			if (TableType.TABLE == tables.get(i).getType()) {
				if (!tableNames.contains(tables.get(i).getName())) {
					tableNames.add(tables.get(i).getName());
				}
			} else if (TableType.PRECONDITION == tables.get(i).getType()
					|| TableType.POSTCONDITION == tables.get(i).getType()) {
				String tableName = Dao.getTable(tables.get(i).getParentId()).getName();
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
