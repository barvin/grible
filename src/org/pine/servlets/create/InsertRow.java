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
package org.pine.servlets.create;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			Dao dao = new Dao();
			int rowId = Integer.parseInt(request.getParameter("rowid"));

			Row currentRow = dao.getRow(rowId);
			int currentRowNumber = currentRow.getOrder();
			int tableId = currentRow.getTableId();
			List<Integer> rowIds = new ArrayList<>();
			List<Integer> rowNumbers = new ArrayList<>();
			List<Integer> oldRowNumbers = new ArrayList<>();
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
			dao.insertValuesEmptyWithRowId(newRowId, keys);

			out.print("success");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
