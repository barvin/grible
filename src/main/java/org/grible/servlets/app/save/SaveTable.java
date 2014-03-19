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

import com.google.gson.Gson;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/SaveTable")
public class SaveTable extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveTable() {
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
			Table table = null;
			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
				table = jDao.getTable(tableId, productId);
			} else {
				pDao = new PostgresDao();
				table = pDao.getTable(tableId);
			}

			String[] keyNames = request.getParameterValues("keys[]");
			String[] keyTypes = request.getParameterValues("keyTypes[]");
			String[] keyRefids = request.getParameterValues("keyRefids[]");
			Key[] keys = new Key[keyNames.length];

			for (int i = 0; i < keys.length; i++) {
				keys[i] = new Key(keyNames[i], KeyType.valueOf(keyTypes[i].toUpperCase()),
						Integer.parseInt(keyRefids[i]));
			}

			String[] valueRows = request.getParameterValues("values[]");
			String[][] values = new String[valueRows.length][keys.length];

			Gson gson = new Gson();
			for (int i = 0; i < values.length; i++) {
				String[] row = gson.fromJson(valueRows[i], String[].class);
				for (int j = 0; j < row.length; j++) {
					String value = row[j];
					if (keys[j].getType() == KeyType.STORAGE) {
						String[] strIndexes = value.split(";");
						for (String index : strIndexes) {
							if (!StringUtils.isNumeric(index)) {
								throw new Exception("ERROR: One of indexes in the row " + (i + 1) + " is not numeric.");
							}
							if (!"0".equals(index)) {
								Table refTable = null;
								if (ServletHelper.isJson()) {
									refTable = jDao.getTable(keys[j].getRefid(), productId);
								} else {
									refTable = pDao.getTable(keys[j].getRefid());
								}
								String[][] refRows = refTable.getTableJson().getValues();
								if (refRows.length < Integer.parseInt(index)) {
									throw new Exception("ERROR: Data storage '" + refTable.getName()
											+ "' does not contain row number " + index
											+ ".<br>You specified it in row: " + (i + 1)
											+ ".<br>You must first create this row in the specified data storage.");
								}
							}
						}

					} else if (keys[j].getType() == KeyType.ENUMERATION) {
						Table refTable = null;
						if (ServletHelper.isJson()) {
							refTable = jDao.getTable(keys[j].getRefid(), productId);
						} else {
							refTable = pDao.getTable(keys[j].getRefid());
						}

						List<String> enumValues = DataManager.getInstance().getDao().getValuesByKeyOrder(refTable, 0);
						if (!enumValues.contains(value)) {
							throw new Exception("ERROR: Enumeration '" + refTable.getName()
									+ "' does not contain value '" + value + "'.<br>You specified it in row: "
									+ (i + 1) + ".");
						}
					}
					values[i][j] = value;
				}
			}

			if (ServletHelper.isJson()) {
				table.getTableJson().setKeys(keys);
				table.getTableJson().setValues(values);
				table.save();
			} else {
				table.setKeys(keys);
				table.setValues(values);
				pDao.updateTable(table);
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
