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
	private Dao dao;
	private TableType tableType;

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
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			dao = new Dao();
			int tableId = Integer.parseInt(request.getParameter("id"));
			Table table = dao.getTable(tableId);
			tableType = table.getType();
			showUsage = table.isShowUsage();

			List<Key> keys = dao.getKeys(tableId);
			writeKeys(out, keys);

			List<Row> rows = dao.getRows(tableId);
			ArrayList<ArrayList<Value>> values = new ArrayList<ArrayList<Value>>();
			for (Row row : rows) {
				values.add(dao.getValues(row));
			}
			writeValues(out, values);

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeKeys(PrintWriter out, List<Key> keys) {
		out.println("<div class=\"table-row key-row\">");
		if (tableType == TableType.STORAGE || tableType == TableType.TABLE) {
			out.println("<div class=\"table-cell ui-cell index-header-cell\">Index</div>");
		}
		for (Key key : keys) {
			out.println("<div id=\"" + key.getId() + "\" class=\"table-cell ui-cell key-cell\">" + key.getName()
					+ "</div>");
		}
		if (showUsage) {
			out.println("<div class=\"table-cell ui-cell info-key-cell\">Used in tables</div>");
			out.println("<div class=\"table-cell ui-cell info-key-cell\">Used in storages</div>");
		}
		out.println("</div>");
	}

	private void writeValues(PrintWriter out, ArrayList<ArrayList<Value>> values) throws SQLException {
		int i = 1;
		for (ArrayList<Value> valuesRow : values) {
			out.println("<div class=\"table-row value-row\">");
			if (tableType == TableType.STORAGE || tableType == TableType.TABLE) {
				out.println("<div id=\"" + valuesRow.get(0).getRowId() + "\" class=\"table-cell ui-cell index-cell\">"
						+ (i++) + "</div>");
			}
			for (Value value : valuesRow) {
				String storageCell = (value.isStorage()) ? " storage-cell" : "";
				out.println("<div id=\"" + value.getId() + "\" class=\"table-cell ui-cell value-cell" + storageCell
						+ "\">" + StringEscapeUtils.escapeHtml4(value.getValue()) + "</div>");
			}
			if (showUsage) {
				if (!valuesRow.isEmpty()) {
					List<Table> tables = dao.getTablesUsingRow(valuesRow.get(0).getRowId());
					out.println("<div class=\"table-cell ui-cell info-cell\">" + getOccurences(tables, TableType.TABLE)
							+ "</div>");
					out.println("<div class=\"table-cell ui-cell info-cell\">"
							+ getOccurences(tables, TableType.STORAGE) + "</div>");
				}
			}
			out.println("</div>");
		}
	}

	private String getOccurences(List<Table> tables, TableType type) throws SQLException {
		String result = "";
		for (int i = 0; i < tables.size(); i++) {
			if (type == tables.get(i).getType()) {
				result += tables.get(i).getName() + ", ";
			}
		}
		if (result.length() > 2) {
			result = result.substring(0, result.length() - 2);
		} // remove ", "
		return result;
	}
}
