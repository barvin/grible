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
@WebServlet("/ApplyParameterType")
public class ApplyParameterType extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JsonDao jDao;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApplyParameterType() {
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
			int currentRefId = 0;
			int keyOrder = 0;
			if (ServletHelper.isJson()) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				productId = Integer.parseInt(request.getParameter("product"));
				keyOrder = Integer.parseInt(request.getParameter("keyorder")) - 1;
				table = jDao.getTable(tableId, productId);
				keyJson = table.getTableJson().getKeys()[keyOrder];
				currentRefId = keyJson.getRefid();
			} else {
				int keyId = Integer.parseInt(request.getParameter("keyId"));
				key = pDao.getKey(keyId);
				currentRefId = key.getReferenceTableId();
			}

			if (((currentRefId == 0) && (type == KeyType.TEXT))
					|| ((currentRefId == refTableId) && (type == KeyType.STORAGE || type == KeyType.ENUMERATION))) {
				out.print("not-changed");
			} else if (type == KeyType.TEXT) {
				if (ServletHelper.isJson()) {
					keyJson.setRefid(0);
					keyJson.setType(type);
					table.getTableJson().getKeys()[keyOrder] = keyJson;
					table.save();
				} else {
					key.setReferenceTableId(0);
					pDao.updateKey(key);
					pDao.updateValuesTypes(key.getId(), false, "NULL");
				}
				out.print("success");
			} else if (type == KeyType.STORAGE) {
				if (ServletHelper.isJson()) {
					keyJson.setRefid(refTableId);
					keyJson.setType(type);

					List<String> values = jDao.getValuesByKeyOrder(table, keyOrder);

					for (int row = 0; row < values.size(); row++) {
						String[] strRows = values.get(row).split(";");
						for (String strRow : strRows) {
							if ((StringUtils.isNumeric(strRow)) && (!"0".equals(strRow))) {
								Table refTable = jDao.getTable(refTableId, productId);
								String[][] refRows = refTable.getTableJson().getValues();
								if (refRows.length < Integer.parseInt(strRow)) {
									out.print("ERROR: Data storage '" + refTable.getName()
											+ "' does not contain row number " + strRow
											+ ".<br>You specified it in row: " + (row + 1)
											+ ".<br>You must first create this row in the specified data storage.");
									out.flush();
									out.close();
									return;
								}
							}
						}
					}
					for (int row = 0; row < values.size(); row++) {
						String[] strRows = values.get(row).split(";");
						for (String strRow : strRows) {
							if (!StringUtils.isNumeric(strRow)) {
								out.print("need-correction");
								out.flush();
								out.close();
								return;
							}
						}
					}

					table.getTableJson().getKeys()[keyOrder] = keyJson;
					table.save();
					out.print("success");
				} else {
					key.setReferenceTableId(refTableId);
					List<Value> values = pDao.getValues(key);
					for (Value value : values) {
						String[] strRows = value.getValue().split(";");
						for (String strRow : strRows) {
							if ((StringUtils.isNumeric(strRow))
									&& (!strRow.equals("0"))
									&& (pDao.getRow(refTableId, Integer.parseInt(strRow))) == null) {
								out.print("ERROR: Data storage '" + new PostgresDao().getTable(refTableId).getName()
										+ "' does not contain row number " + strRow + ".<br>You specified it in row: "
										+ pDao.getRow(value.getRowId()).getOrder()
										+ ".<br>You must first create this row in specified data storage.");
								out.flush();
								out.close();
								return;
							}
						}
					}
					for (Value value : values) {
						String[] strRows = value.getValue().split(";");
						for (String strRow : strRows) {
							if (!StringUtils.isNumeric(strRow)) {
								out.print("need-correction");
								out.flush();
								out.close();
								return;
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
					List<String> values = dao.getValuesByKeyOrder(table, keyOrder);
					Table refTable = dao.getTable(refTableId, productId);
					List<String> enumValues = dao.getValuesByKeyOrder(refTable, 0);
					for (int row = 0; row < values.size(); row++) {
						boolean isValid = false;
						for (String enumValue : enumValues) {
							if (values.get(row).equals(enumValue)) {
								isValid = true;
								break;
							}
						}
						if (!isValid) {
							out.print("need-correction");
							out.flush();
							out.close();
							return;
						}
					}

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
							out.print("need-correction");
							out.flush();
							out.close();
							return;
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
