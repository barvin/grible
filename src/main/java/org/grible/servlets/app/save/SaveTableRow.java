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
import java.text.SimpleDateFormat;
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
import org.grible.model.TableType;
import org.grible.model.json.Key;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;
import org.grible.settings.Lang;

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
							throw new Exception(Lang.get("error") + ": " + String.format(Lang.get("indexnotnumeric"), (rowNumber + 1)));
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
								throw new Exception(Lang.get("error") + ": "
										+ String.format(Lang.get("norowinstorage"), refTable.getName(), index,
												(rowNumber + 1)));
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
						throw new Exception(Lang.get("error") + ": " + String.format(Lang.get("norowinenum"), refTable.getName(), value, (rowNumber + 1)));
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

			String time = "";
			boolean isLastRow = Boolean.parseBoolean(request.getParameter("islastrow"));
			if (isLastRow) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));

				Table table = null;
				if (ServletHelper.isJson()) {
					table = jDao.getTable(tableId, productId);
				} else {
					table = pDao.getTable(tableId);
				}
				if (table.getType() == TableType.ENUMERATION) {
					List<String> oldEnumValues = DataManager.getInstance().getDao().getValuesByKeyOrder(table, 0);
					List<String> newEnumValues = DataManager.getInstance().getDao().getValuesByKeyOrder(tempTable, 0);
					for (int i = 0; i < oldEnumValues.size(); i++) {
						if (i >= newEnumValues.size()) {
							changeEnumValueInAllTables(productId, table, oldEnumValues.get(i), newEnumValues.get(0));
						} else if (!oldEnumValues.get(i).equals(newEnumValues.get(i))) {
							changeEnumValueInAllTables(productId, table, oldEnumValues.get(i), newEnumValues.get(i));
						}
					}
				}

				if (ServletHelper.isJson()) {
					table.getTableJson().setKeys(keys);
					table.getTableJson().setValues(values);
					table.save();
					time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(table.getModifiedTime());
				} else {
					table.setKeys(keys);
					table.setValues(values);
					time = pDao.updateTable(table);
				}
				request.getSession(false).setAttribute("SaveTable", null);
			}

			out.print("success|" + time);

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	private void changeEnumValueInAllTables(int productId, Table table, String oldValue, String newValue)
			throws Exception {
		List<Table> tables = null;
		if (tables == null) {
			tables = DataManager.getInstance().getDao().getTablesUsingStorage(table, productId);
		}
		for (Table usingTable : tables) {
			Key[] usingKeys = null;
			String[][] usingValues = null;
			boolean isTableChanged = false;
			if (ServletHelper.isJson()) {
				usingKeys = usingTable.getTableJson().getKeys();
				usingValues = usingTable.getTableJson().getValues();
			} else {
				usingKeys = usingTable.getKeys();
				usingValues = usingTable.getValues();
			}
			for (int row = 0; row < usingValues.length; row++) {
				for (int key = 0; key < usingKeys.length; key++) {
					if (usingKeys[key].getRefid() == table.getId() && usingValues[row][key].equals(oldValue)) {
						usingValues[row][key] = newValue;
						if (!isTableChanged) {
							isTableChanged = true;
						}
					}
				}
			}
			if (isTableChanged) {
				if (ServletHelper.isJson()) {
					usingTable.getTableJson().setValues(usingValues);
					usingTable.save();
				} else {
					usingTable.setValues(usingValues);
					pDao.updateTable(usingTable);
				}
			}
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
