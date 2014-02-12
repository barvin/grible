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
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Key;
import org.grible.model.Table;
import org.grible.model.Value;
import org.grible.model.json.KeyJson;
import org.grible.model.json.KeyType;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/CorrectValuesForParameterType")
public class CorrectValuesForParameterType extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CorrectValuesForParameterType() {
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

			if (ServletHelper.isJson()) {
				jDao = new JsonDao();
			} else {
				pDao = new PostgresDao();
			}

			int refTableId = 0;
			if ((request.getParameter("refId") != null) && StringUtils.isNumeric(request.getParameter("refId"))) {
				refTableId = Integer.parseInt(request.getParameter("refId"));
			}
			KeyType type = KeyType.valueOf(request.getParameter("type").toUpperCase());

			Key key = null;
			Table table = null;
			int productId = 0;
			KeyJson keyJson = null;
			int keyOrder = 0;
			if (ServletHelper.isJson()) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				productId = Integer.parseInt(request.getParameter("product"));
				keyOrder = Integer.parseInt(request.getParameter("keyorder")) - 1;
				table = jDao.getTable(tableId, productId);
				keyJson = table.getTableJson().getKeys()[keyOrder];
			} else {
				int keyId = Integer.parseInt(request.getParameter("keyId"));
				key = pDao.getKey(keyId);
			}

			if (type == KeyType.STORAGE) {
				if (ServletHelper.isJson()) {
					keyJson.setRefid(refTableId);
					keyJson.setType(type);

					List<String> columnValues = jDao.getValuesByKeyOrder(table, keyOrder);
					String[][] values = table.getTableJson().getValues();

					for (int row = 0; row < columnValues.size(); row++) {
						String[] strRows = columnValues.get(row).split(";");
						for (String strRow : strRows) {
							if (!StringUtils.isNumeric(strRow)) {
								values[row][keyOrder] = "0";
								break;
							}
						}
					}

					table.getTableJson().setValues(values);
					table.getTableJson().getKeys()[keyOrder] = keyJson;
					table.save();
					out.print("success");
				} else {
					key.setReferenceTableId(refTableId);
					List<Value> values = pDao.getValues(key);
					for (Value value : values) {
						String[] strRows = value.getValue().split(";");
						for (String strRow : strRows) {
							if (!StringUtils.isNumeric(strRow)) {
								value.setValue("0");
								break;
							}
						}
					}
					for (Value value : values) {
						if ("0".equals(value.getValue())) {
							value.setStorageIds(null);
						} else {
							String[] strRows = value.getValue().split(";");
							Integer[] intRows = new Integer[strRows.length];
							for (int i = 0; i < strRows.length; i++) {
								intRows[i] = pDao
										.getRow(refTableId, Integer.parseInt(strRows[i])).getId();
							}
							value.setStorageIds(intRows);
						}
						value.setIsStorage(true);
						pDao.updateValue(value);
					}
					pDao.updateKey(key);
					out.print("success");
				}
			} else {
				if (ServletHelper.isJson()) {
					keyJson.setRefid(refTableId);
					keyJson.setType(type);

					JsonDao dao = new JsonDao();
					List<String> columnValues = dao.getValuesByKeyOrder(table, keyOrder);
					Table refTable = dao.getTable(refTableId, productId);
					List<String> enumValues = dao.getValuesByKeyOrder(refTable, 0);
					String[][] values = table.getTableJson().getValues();
					for (int row = 0; row < columnValues.size(); row++) {
						boolean isValid = false;
						for (String enumValue : enumValues) {
							if (columnValues.get(row).equals(enumValue)) {
								isValid = true;
								break;
							}
						}
						if (!isValid) {
							values[row][keyOrder] = enumValues.get(0);
						}
					}

					table.getTableJson().setValues(values);
					table.getTableJson().getKeys()[keyOrder] = keyJson;
					table.save();
					out.print("success");
				} else {
					key.setReferenceTableId(refTableId);
					Key enumKey = pDao.getKeys(refTableId).get(0);
					List<Value> enumValues = pDao.getValues(enumKey);
					List<Value> values = pDao.getValues(key);
					for (Value value : values) {
						boolean isValid = false;
						for (Value enumValue : enumValues) {
							if (value.getValue().equals(enumValue.getValue())) {
								isValid = true;
								break;
							}
						}
						if (!isValid) {
							value.setValue(enumValues.get(0).getValue());
						}
					}
					pDao.updateKey(key);
					pDao.updateValuesTypes(key.getId(), false, "NULL");
					out.print("success");
				}
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

}
