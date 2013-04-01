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
package org.pine.servlets.ui.panels;

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

import org.apache.commons.lang3.StringEscapeUtils;
import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Row;
import org.pine.model.Table;
import org.pine.model.TableType;
import org.pine.model.Value;

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
		responseHtml.append("<div class=\"table-row key-row\">");
		if (tableType == TableType.STORAGE || tableType == TableType.TABLE) {
			responseHtml.append("<div class=\"table-cell ui-cell index-header-cell\">Index</div>");
		}
		for (Key key : keys) {
			responseHtml.append("<div id=\"").append(key.getId()).append("\" key-order=\"").append(key.getOrder())
					.append("\" class=\"table-cell ui-cell key-cell\">").append(key.getName()).append("</div>");
		}
		if (showUsage) {
			responseHtml.append("<div class=\"table-cell ui-cell info-key-cell\">Used in tables</div>");
			responseHtml.append("<div class=\"table-cell ui-cell info-key-cell\">Used in storages</div>");
		}
		responseHtml.append("</div>");
	}

	private void writeValues(StringBuilder responseHtml, ArrayList<ArrayList<Value>> values) throws SQLException {
		int i = 1;
		for (ArrayList<Value> valuesRow : values) {
			responseHtml.append("<div class=\"table-row value-row\">");
			if (tableType == TableType.STORAGE || tableType == TableType.TABLE) {
				responseHtml.append("<div id=\"").append(valuesRow.get(0).getRowId())
						.append("\" class=\"table-cell ui-cell index-cell\">").append(i++).append("</div>");
			}
			for (Value value : valuesRow) {
				String storageCell = (value.isStorage()) ? " storage-cell" : "";
				responseHtml.append("<div id=\"").append(value.getId()).append("\" keyid=\"").append(value.getKeyId())
						.append("\" rowid=\"").append(value.getRowId())
						.append("\" class=\"table-cell ui-cell value-cell").append(storageCell).append("\">")
						.append(StringEscapeUtils.escapeHtml4(value.getValue())).append("</div>");
			}
			if (showUsage) {
				if (!valuesRow.isEmpty()) {
					List<Table> tables = Dao.getTablesUsingRow(valuesRow.get(0).getRowId());
					responseHtml.append("<div class=\"table-cell ui-cell info-cell\">")
							.append(getOccurences(tables, TableType.TABLE)).append("</div>");
					responseHtml.append("<div class=\"table-cell ui-cell info-cell\">")
							.append(getOccurences(tables, TableType.STORAGE)).append("</div>");
				}
			}
			responseHtml.append("</div>");
		}
	}

	private String getOccurences(List<Table> tables, TableType type) throws SQLException {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < tables.size(); i++) {
			if (type == tables.get(i).getType()) {
				result.append(tables.get(i).getName()).append(", ");
			}
		}
		if (result.toString().length() > 2) {
			return result.substring(0, result.length() - 2);
		} // remove ", "
		return result.toString();
	}
}
