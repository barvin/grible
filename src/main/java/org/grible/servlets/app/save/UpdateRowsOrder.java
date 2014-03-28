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
package org.grible.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.Key;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/UpdateRowsOrder")
public class UpdateRowsOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;
	private List<int[]> processedCells;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateRowsOrder() {
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

			int tableId = Integer.parseInt(request.getParameter("tableid"));
			int productId = Integer.parseInt(request.getParameter("product"));
			String[] rows = request.getParameterValues("rows[]");
			HashMap<Integer, Integer> newRowNumbers = new HashMap<>();
			for (int i = 0; i < rows.length; i++) {
				if (!rows[i].equals("-1")) {
					newRowNumbers.put(Integer.parseInt(rows[i]) + 1, i + 1);
				}
			}

			Table table = null;
			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				table = jDao.getTable(tableId, productId);
			} else {
				pDao = new PostgresDao();
				table = pDao.getTable(tableId);
			}

			if (table.getType() == TableType.STORAGE) {
				processedCells = new ArrayList<int[]>();
				for (int rowNum : newRowNumbers.keySet()) {
					if (rowNum != newRowNumbers.get(rowNum)) {
						List<Table> allTablesUsingRow = DataManager.getInstance().getDao()
								.getTablesUsingRow(productId, table, rowNum);
						for (Table usingTable : allTablesUsingRow) {
							boolean isTableChanged = false;
							Key[] keys = null;
							String[][] values = null;
							if (ServletHelper.isJson()) {
								keys = usingTable.getTableJson().getKeys();
								values = usingTable.getTableJson().getValues();
							} else {
								keys = usingTable.getKeys();
								values = usingTable.getValues();
							}
							for (int i = 0; i < values.length; i++) {
								for (int j = 0; j < values[0].length; j++) {
									if (keys[j].getRefid() == table.getId()) {
										if (!isCellProcessed(usingTable.getId(), i, j)) {
											processedCells.add(new int[] { usingTable.getId(), i, j });
											String[] indexes = values[i][j].split(";");
											for (int indexNum = 0; indexNum < indexes.length; indexNum++) {
												int index = Integer.parseInt(indexes[indexNum]);
												if (!isTableChanged) {
													isTableChanged = true;
												}
												indexes[indexNum] = String.valueOf(newRowNumbers.get(index));
											}
											values[i][j] = StringUtils.join(indexes, ";");
										}
									}
								}
							}
							if (isTableChanged) {
								if (ServletHelper.isJson()) {
									usingTable.getTableJson().setValues(values);
									usingTable.save();
								} else {
									usingTable.setValues(values);
									pDao.updateTable(usingTable);
								}
							}
						}
					}
				}
			}

			out.print("success");

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	private boolean isCellProcessed(int id, int i, int j) {
		for (int[] cells : processedCells) {
			if (Arrays.equals(cells, new int[] { id, i, j })) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
