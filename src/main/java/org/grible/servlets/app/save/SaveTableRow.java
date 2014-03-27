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
import java.util.Collections;
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
import org.grible.model.json.Key;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/SaveTableRow")
public class SaveTableRow extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveTableRow() {
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

			int productId = Integer.parseInt(request.getParameter("product"));

			Table tempTable = (Table) request.getSession(false).getAttribute("SaveTable");
			Key[] keys = tempTable.getKeys();

			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
			} else {
				pDao = new PostgresDao();
			}

			int rowNumber = Integer.parseInt(request.getParameter("row"));
			String[] valueRow = request.getParameterValues("values[]");

			for (int i = 0; i < valueRow.length; i++) {
				String value = valueRow[i];
				if (keys[i].getType() == KeyType.STORAGE) {
					String[] strIndexes = value.split(";");
					for (String index : strIndexes) {
						if (!StringUtils.isNumeric(index)) {
							throw new Exception("ERROR: One of indexes in the row " + (rowNumber + 1)
									+ " is not numeric.");
						}
						if (!"0".equals(index)) {
							Table refTable = null;
							String[][] refRows = null;
							if (ServletHelper.isJson()) {
								refTable = jDao.getTable(keys[i].getRefid(), productId);
								refRows = refTable.getTableJson().getValues();
							} else {
								refTable = pDao.getTable(keys[i].getRefid());
								refRows = refTable.getValues();
							}
							if (refRows.length < Integer.parseInt(index)) {
								throw new Exception("ERROR: Data storage '" + refTable.getName()
										+ "' does not contain row number " + index + ".<br>You specified it in row: "
										+ (rowNumber + 1)
										+ ".<br>You must first create this row in the specified data storage.");
							}
						}
					}

				} else if (keys[i].getType() == KeyType.ENUMERATION) {
					Table refTable = null;
					if (ServletHelper.isJson()) {
						refTable = jDao.getTable(keys[i].getRefid(), productId);
					} else {
						refTable = pDao.getTable(keys[i].getRefid());
					}

					List<String> enumValues = DataManager.getInstance().getDao().getValuesByKeyOrder(refTable, 0);
					if (!enumValues.contains(value)) {
						throw new Exception("ERROR: Enumeration '" + refTable.getName() + "' does not contain value '"
								+ value + "'.<br>You specified it in row: " + (rowNumber + 1) + ".");
					}
				}
			}

			String[][] values = null;
			if (rowNumber == 0) {
				values = new String[][] { valueRow };
			} else {
				ArrayList<String[]> lstValues = new ArrayList<>();
				Collections.addAll(lstValues, tempTable.getValues());
				lstValues.add(valueRow);
				values = lstValues.toArray(new String[0][0]);
			}
			tempTable.setValues(values);

			boolean isLastRow = Boolean.parseBoolean(request.getParameter("islastrow"));
			if (isLastRow) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));

				if (ServletHelper.isJson()) {
					Table table = jDao.getTable(tableId, productId);
					table.getTableJson().setKeys(keys);
					table.getTableJson().setValues(values);
					table.save();
				} else {
					Table table = pDao.getTable(tableId);
					table.setKeys(keys);
					table.setValues(values);
					pDao.updateTable(table);
				}
				request.getSession(false).setAttribute("SaveTable", null);
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
