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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.model.json.Key;
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
			Table refTable = null;
			int refId = Integer.parseInt(request.getParameter("refid"));
			int productId = Integer.parseInt(request.getParameter("product"));
			int rowsInRefTable = 0;
			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				refTable = jDao.getTable(refId, productId);
				rowsInRefTable = refTable.getTableJson().getValues().length;
			} else {
				pDao = new PostgresDao();
				refTable = pDao.getTable(refId);
				//TODO: implement PostgreSQL part.
			}
			
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
				if (Integer.parseInt(indexes[i]) > rowsInRefTable) {
					correctFormat = false;
					break;
				}
			}

			if (correctFormat) {
				out.print(content + getStorageTooltip(indexes, refTable, productId));
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
		result.append("<div class=\"table-cell ui-cell-mini index-header-cell\"></div>");
		Key[] keys = refTable.getTableJson().getKeys();
		for (Key key : keys) {
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
		result.append("<a href=\"/storages/?product=").append(productId).append("&id=").append(refTable.getId())
				.append("\" target=\"_blank\">Open storage in the new tab</a></div>");
		return result.toString();
	}
}
