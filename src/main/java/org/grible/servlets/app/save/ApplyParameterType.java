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
package org.grible.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.PostgresDao;
import org.grible.model.Key;
import org.grible.model.Value;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/ApplyParameterType")
public class ApplyParameterType extends HttpServlet {
	private static final long serialVersionUID = 1L;

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

			int keyId = Integer.parseInt(request.getParameter("keyId"));
			int refTableId = 0;
			if ((request.getParameter("refId") != null) && StringUtils.isNumeric(request.getParameter("refId"))) {
				refTableId = Integer.parseInt(request.getParameter("refId"));
			}
			// "text", "storage", "enumeration"
			String type = request.getParameter("type");
			Key key = DataManager.getInstance().getDao().getKey(keyId);
			if (((key.getReferenceTableId() == 0) && ("text".equals(type)))
					|| ((key.getReferenceTableId() == refTableId) && ("storage".equals(type) || "enumeration"
							.equals(type)))) {
				out.print("not-changed");
			} else if ("text".equals(type)) {
				key.setReferenceTableId(0);
				DataManager.getInstance().getDao().updateKey(key);
				DataManager.getInstance().getDao().updateValuesTypes(keyId, false, "NULL");
				out.print("success");
			} else if ("storage".equals(type)) {
				key.setReferenceTableId(refTableId);
				List<Value> values = DataManager.getInstance().getDao().getValues(key);
				for (Value value : values) {
					String[] strRows = value.getValue().split(";");
					for (int i = 0; i < strRows.length; i++) {
						if (!StringUtils.isNumeric(strRows[i])) {
							out.print("ERROR: One of indexes is not numeric. Row: "
									+ DataManager.getInstance().getDao().getRow(value.getRowId()).getOrder()
									+ ".<br>If you want to set no index, set '0'.");
							out.flush();
							out.close();
							return;
						} else if ((!strRows[i].equals("0"))
								&& (DataManager.getInstance().getDao().getRow(refTableId, Integer.parseInt(strRows[i]))) == null) {
							out.print("ERROR: Data storage '" + new PostgresDao().getTable(refTableId).getName()
									+ "' does not contain row number " + strRows[i] + ".<br>You specified it in row: "
									+ DataManager.getInstance().getDao().getRow(value.getRowId()).getOrder()
									+ ".<br>You must first create this row in specified data storage.");
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
							intRows[i] = DataManager.getInstance().getDao().getRow(refTableId, Integer.parseInt(strRows[i])).getId();
						}
						value.setStorageIds(intRows);
					}
					value.setIsStorage(true);
					DataManager.getInstance().getDao().updateValue(value);
				}
				DataManager.getInstance().getDao().updateKey(key);
				out.print("success|" + keyId + "|storage");
			} else {
				key.setReferenceTableId(refTableId);
				Key enumKey = DataManager.getInstance().getDao().getKeys(refTableId).get(0);
				List<Value> enumValues = DataManager.getInstance().getDao().getValues(enumKey);
				List<Value> values = DataManager.getInstance().getDao().getValues(key);
				for (Value value : values) {
					boolean valid = false;
					for (Value enumValue : enumValues) {
						if (value.getValue().equals(enumValue.getValue())) {
							valid = true;
							break;
						}
					}
					if (!valid) {
						out.print("ERROR: One of values is not from the enumeration. Row: "
								+ DataManager.getInstance().getDao().getRow(value.getRowId()).getOrder() + ".");
						out.flush();
						out.close();
						return;
					}
				}
				DataManager.getInstance().getDao().updateKey(key);
				out.print("success|" + keyId + "|enum");
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
