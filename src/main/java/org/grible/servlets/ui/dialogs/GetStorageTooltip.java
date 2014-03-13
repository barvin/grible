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
package org.grible.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Key;
import org.grible.model.Row;
import org.grible.model.Table;
import org.grible.model.Value;
import org.grible.model.json.KeyJson;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetStorageTooltip")
public class GetStorageTooltip extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetStorageTooltip() {
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

			String content = request.getParameter("content");

			String[] indexes = content.split(";");
			boolean correctFormat = true;
			for (int i = 0; i < indexes.length; i++) {
				if (!StringUtils.isNumeric(indexes[i])) {
					correctFormat = false;
					break;
				}
				if (("0").equals(indexes[i])) {
					correctFormat = false;
					break;
				}
			}

			if (correctFormat) {

				if (ServletHelper.isJson()) {
					int productId = Integer.parseInt(request.getParameter("product"));
					int refId = Integer.parseInt(request.getParameter("refid"));
					jDao = new JsonDao();
					Table refTable = jDao.getTable(refId, productId);
					out.print(content + getStorageTooltip(indexes, refTable, productId));
				} else {
					pDao = new PostgresDao();
					Value value = pDao
							.getValue(Integer.parseInt(request.getParameter("id")));
					Integer[] storageIds = value.getStorageIds();
					out.print(content + getStorageTooltip(storageIds));
				}
			} else {
				out.print(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		} finally {
			out.flush();
			out.close();
		}
	}

	private String getStorageTooltip(String[] indexes, Table refTable, int productId) {
		StringBuilder result = new StringBuilder("<div class=\"tooltip\"><div style=\"width: auto;\" class=\"table\">");
		result.append("<div class=\"table-row key-row\">");
		result.append("<div class=\"table-cell ui-cell-mini index-header-cell\">Index</div>");
		KeyJson[] keys = refTable.getTableJson().getKeys();
		for (KeyJson key : keys) {
			result.append("<div class=\"table-cell ui-cell-mini key-cell\">");
			result.append(key.getName());
			result.append("</div>");
		}
		result.append("</div>");

		String[][] rows = refTable.getTableJson().getValues();
		for (int i = 0; i < indexes.length; i++) {
			result.append("<div class=\"table-row value-row\">");
			result.append("<div id=\"").append(indexes[i]);
			result.append("\" class=\"table-cell ui-cell-mini index-cell\">");
			result.append(indexes[i]);
			result.append("</div>");
			String[] values = rows[Integer.parseInt(indexes[i]) - 1];
			for (int j = 0; j < values.length; j++) {
				String storageCell = (keys[j].getType() == KeyType.STORAGE) ? " storage-cell" : "";
				result.append("<div class=\"table-cell ui-cell-mini value-cell ");
				result.append(storageCell).append("\">");
				result.append(values[j]);
				result.append("</div>");
			}
			result.append("</div>");
		}
		result.append("</div>");
		result.append("<br><a href=\"/storages/?product=").append(productId).append("&id=").append(refTable.getId())
				.append("\" target=\"_blank\">Open storage in the new tab</a></div>");
		return result.toString();
	}

	private String getStorageTooltip(Integer[] integers) throws Exception {
		if (integers != null) {
			StringBuilder result = new StringBuilder(
					"<div class=\"tooltip\"><div style=\"width: auto;\" class=\"table\">");
			int tableId = pDao.getRow(integers[0]).getTableId();

			List<Key> keys = pDao.getKeys(tableId);
			result.append("<div class=\"table-row key-row\">");
			result.append("<div class=\"table-cell ui-cell-mini index-header-cell\">Index</div>");
			for (Key key : keys) {
				result.append("<div class=\"table-cell ui-cell-mini key-cell\">");
				result.append(key.getName());
				result.append("</div>");
			}
			result.append("</div>");

			for (int i = 0; i < integers.length; i++) {
				Row row = pDao.getRow(integers[i]);
				List<Value> values = pDao.getValues(row);
				result.append("<div class=\"table-row value-row\">");
				result.append("<div id=\"").append(row.getId());
				result.append("\" class=\"table-cell ui-cell-mini index-cell\">");
				result.append(row.getOrder());
				result.append("</div>");
				for (Value value : values) {
					String storageCell = (value.isStorage()) ? " storage-cell" : "";
					result.append("<div class=\"table-cell ui-cell-mini value-cell ");
					result.append(storageCell).append("\">");
					result.append(value.getValue());
					result.append("</div>");
				}
				result.append("</div>");
			}
			result.append("</div>");
			result.append("<br><a href=\"/storages/?id=").append(tableId)
					.append("\" target=\"_blank\">Open storage in the new tab</a></div>");
			return result.toString();
		}
		return "";
	}

}
