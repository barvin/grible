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
package org.pine.servlets.save;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			Dao dao = new Dao();

			int keyId = Integer.parseInt(request.getParameter("keyId"));
			int refStorageId = Integer.parseInt(request.getParameter("storageId"));
			String type = request.getParameter("type"); // "cbx-text"
														// "cbx-storage"

			Key key = dao.getKey(keyId);
			if (((key.getReferenceTableId() == 0) && ("cbx-text".equals(type)))
					|| ((key.getReferenceTableId() == refStorageId) && ("cbx-storage".equals(type)))) {
				out.print("not-changed");
			} else if ("cbx-text".equals(type)) {
				key.setReferenceTableId(0);
				dao.updateKey(key);
				dao.updateValuesTypes(keyId, false, "NULL");
				out.print("success");
			} else {
				key.setReferenceTableId(refStorageId);
				List<Value> values = dao.getValues(key);
				for (Value value : values) {
					String[] strRows = value.getValue().split(";");
					for (int i = 0; i < strRows.length; i++) {
						if (!StringUtils.isNumeric(strRows[i])) {
							out.print("ERROR: One of indexes is not numeric. Row: "
									+ dao.getRow(value.getRowId()).getOrder()
									+ ".\nIf you want to set no index, set '0'.");
							out.flush();
							out.close();
							return;
						} else if ((!strRows[i].equals("0"))
								&& (dao.getRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
							out.print("ERROR: Data storage '" + dao.getTable(refStorageId).getName()
									+ "' does not contain row number " + strRows[i] + ".\nYou specified it in row: "
									+ dao.getRow(value.getRowId()).getOrder()
									+ ".\nYou must first create this row in specified data storage.");
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
							intRows[i] = dao.getRow(refStorageId, Integer.parseInt(strRows[i])).getId();
						}
						value.setStorageIds(intRows);
					}
					value.setIsStorage(true);
					dao.updateValue(value);
				}
				dao.updateKey(key);
				out.print("success");
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
