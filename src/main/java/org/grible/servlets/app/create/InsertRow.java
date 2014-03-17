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
package org.grible.servlets.app.create;

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
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.dbmigrate.oldmodel.Row;
import org.grible.dbmigrate.oldmodel.Value;
import org.grible.model.Table;
import org.grible.model.json.KeyJson;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			String result = null;

			if (ServletHelper.isJson()) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				int productId = Integer.parseInt(request.getParameter("product"));
				int rowOrder = Integer.parseInt(request.getParameter("roworder")) - 1;

				JsonDao dao = new JsonDao();
				Table table = dao.getTable(tableId, productId);
				KeyJson[] keys = table.getTableJson().getKeys();
				String[][] values = table.getTableJson().getValues();
				String[][] newValues = new String[values.length + 1][values[0].length];
				for (int i = newValues.length - 1; i >= 0; i--) {
					if (i == rowOrder) {
						for (int j = 0; j < newValues[0].length; j++) {
							switch (keys[j].getType()) {
							case STORAGE:
								newValues[i][j] = "0";
								break;

							case ENUMERATION:
								Table refTable = dao.getTable(keys[j].getRefid(), productId);
								newValues[i][j] = dao.getValuesByKeyOrder(refTable, 0).get(0);
								break;

							default:
								newValues[i][j] = "";
								break;
							}
						}
					} else if (i < rowOrder) {
						for (int j = 0; j < newValues[0].length; j++) {
							newValues[i][j] = values[i][j];
						}
					} else {
						for (int j = 0; j < newValues[0].length; j++) {
							newValues[i][j] = values[i - 1][j];
						}
					}
				}
				table.getTableJson().setValues(newValues);
				table.save();
				result = "success";
			} else {
				int rowId = Integer.parseInt(request.getParameter("rowid"));

				PostgresDao pDao = new PostgresDao();
				Row currentRow = pDao.getRow(rowId);
				int currentRowNumber = currentRow.getOrder();
				int tableId = currentRow.getTableId();
				List<Integer> rowIds = new ArrayList<Integer>();
				List<Integer> rowNumbers = new ArrayList<Integer>();
				List<Integer> oldRowNumbers = new ArrayList<Integer>();
				List<Row> rows = pDao.getRows(tableId);
				for (int i = rows.size() - 1; i >= 0; i--) {
					rowIds.add(rows.get(i).getId());
					if (rows.get(i).getOrder() >= currentRowNumber) {
						rowNumbers.add(i + 2);
					} else {
						rowNumbers.add(i + 1);
					}
					oldRowNumbers.add(i + 1);
				}
				pDao.updateRows(rowIds, oldRowNumbers, rowNumbers);
				currentRow.setOrder(currentRowNumber);
				int newRowId = pDao.insertRowCopy(currentRow);

				List<Value> values = pDao.getValues(currentRow);
				List<Integer> ids = pDao.insertValuesEmptyByRowIdFromExistingRow(newRowId, values);

				result = newRowId + ";" + StringUtils.join(ids, ";");
			}
			out.print(result);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
