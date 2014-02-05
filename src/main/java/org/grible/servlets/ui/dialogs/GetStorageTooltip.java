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
import org.grible.dao.DataManager;
import org.grible.model.Key;
import org.grible.model.Row;
import org.grible.model.Value;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetStorageTooltip")
public class GetStorageTooltip extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
				Value value = DataManager.getInstance().getDao().getValue(Integer.parseInt(request.getParameter("id")));
				Integer[] storageIds = value.getStorageIds();
				out.print(content + getStorageTooltip(storageIds));
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

	private String getStorageTooltip(Integer[] integers) throws Exception {
		if (integers != null) {
			StringBuilder result = new StringBuilder(
					"<div class=\"tooltip\"><div style=\"width: auto;\" class=\"table\">");
			int tableId = DataManager.getInstance().getDao().getRow(integers[0]).getTableId();

			List<Key> keys = DataManager.getInstance().getDao().getKeys(tableId);
			result.append("<div class=\"table-row key-row\">");
			result.append("<div class=\"table-cell ui-cell-mini index-header-cell\">Index</div>");
			for (Key key : keys) {
				result.append("<div class=\"table-cell ui-cell-mini key-cell\">");
				result.append(key.getName());
				result.append("</div>");
			}
			result.append("</div>");

			for (int i = 0; i < integers.length; i++) {
				Row row = DataManager.getInstance().getDao().getRow(integers[i]);
				List<Value> values = DataManager.getInstance().getDao().getValues(row);
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
