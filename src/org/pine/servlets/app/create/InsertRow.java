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
package org.pine.servlets.app.create;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Row;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/InsertRow")
public class InsertRow extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertRow() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try {
			Dao dao = new Dao();
			int rowId = Integer.parseInt(request.getParameter("rowid"));

			Row currentRow = dao.getRow(rowId);
			int currentRowNumber = currentRow.getOrder();
			int tableId = currentRow.getTableId();
			List<Integer> rowIds = new ArrayList<Integer>();
			List<Integer> rowNumbers = new ArrayList<Integer>();
			List<Integer> oldRowNumbers = new ArrayList<Integer>();
			List<Row> rows = dao.getRows(tableId);
			for (int i = 0; i < rows.size(); i++) {
				rowIds.add(rows.get(i).getId());
				if (rows.get(i).getOrder() >= currentRowNumber) {
					rowNumbers.add(i + 2);
				} else {
					rowNumbers.add(i + 1);
				}
				oldRowNumbers.add(i + 1);
			}
			dao.updateRows(rowIds, oldRowNumbers, rowNumbers);
			currentRow.setOrder(currentRowNumber);
			int newRowId = dao.insertRowCopy(currentRow);

			List<Key> keys = dao.getKeys(tableId);
			List<Integer> ids = dao.insertValuesEmptyWithRowId(newRowId, keys);

			String result = newRowId + ";" + StringUtils.join(ids, ";");
			out.print(result);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
