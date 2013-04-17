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
package org.pine.servlets.app.save;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Value;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		try {

			int keyId = Integer.parseInt(request.getParameter("keyId"));
			int refStorageId = Integer.parseInt(request.getParameter("storageId"));
			String type = request.getParameter("type"); // "text"
														// "storage"

			Key key = Dao.getKey(keyId);
			if (((key.getReferenceTableId() == 0) && ("text".equals(type)))
					|| ((key.getReferenceTableId() == refStorageId) && ("storage".equals(type)))) {
				out.print("not-changed");
			} else if ("text".equals(type)) {
				key.setReferenceTableId(0);
				Dao.updateKey(key);
				Dao.updateValuesTypes(keyId, false, "NULL");
				out.print("success");
			} else {
				key.setReferenceTableId(refStorageId);
				List<Value> values = Dao.getValues(key);
				for (Value value : values) {
					String[] strRows = value.getValue().split(";");
					for (int i = 0; i < strRows.length; i++) {
						if (!StringUtils.isNumeric(strRows[i])) {
							out.print("ERROR: One of indexes is not numeric. Row: "
									+ Dao.getRow(value.getRowId()).getOrder()
									+ ".<br>If you want to set no index, set '0'.");
							out.flush();
							out.close();
							return;
						} else if ((!strRows[i].equals("0"))
								&& (Dao.getRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
							out.print("ERROR: Data storage '" + Dao.getTable(refStorageId).getName()
									+ "' does not contain row number " + strRows[i] + ".<br>You specified it in row: "
									+ Dao.getRow(value.getRowId()).getOrder()
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
							intRows[i] = Dao.getRow(refStorageId, Integer.parseInt(strRows[i])).getId();
						}
						value.setStorageIds(intRows);
					}
					value.setIsStorage(true);
					Dao.updateValue(value);
				}
				Dao.updateKey(key);
				out.print("success" + keyId);
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
