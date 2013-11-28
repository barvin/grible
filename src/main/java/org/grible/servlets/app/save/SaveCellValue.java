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
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.grible.dao.Dao;
import org.grible.model.TableType;
import org.grible.model.Value;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/SaveCellValue")
public class SaveCellValue extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveCellValue() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			String strId = request.getParameter("id");
			String strValue = request.getParameter("value");
			int id = Integer.parseInt(strId);

			Value value = Dao.getValue(id);
			String oldValue = value.getValue();
			value.setValue(StringEscapeUtils.unescapeHtml(strValue));

			if (value.isStorage()) {
				String[] strRows = value.getValue().split(";");
				int refStorageId = Dao.getRefStorageId(value.getKeyId());
				for (int i = 0; i < strRows.length; i++) {
					if (!StringUtils.isNumeric(strRows[i])) {
						out.print("<br>ERROR: Indexes is not numeric. Row: " + Dao.getRow(value.getRowId()).getOrder()
								+ ".<br>If you want to set no index, set '0'.");
						out.flush();
						out.close();
						return;
					} else if ((!strRows[i].equals("0"))
							&& (Dao.getRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
						out.print("<br>ERROR: Data storage '" + Dao.getTable(refStorageId).getName()
								+ "' does not contain row number " + strRows[i] + ".<br>You specified it in row: "
								+ Dao.getRow(value.getRowId()).getOrder()
								+ ".<br>You must first create this row in specified data storage.");
						out.flush();
						out.close();
						return;
					}
				}
				if ("0".equals(value.getValue())) {
					value.setStorageIds(null);
				} else {
					Integer[] intRows = new Integer[strRows.length];
					for (int i = 0; i < strRows.length; i++) {
						intRows[i] = Dao.getRow(refStorageId, Integer.parseInt(strRows[i])).getId();
					}
					value.setStorageIds(intRows);
				}
				value.setIsStorage(true);
			} else if (isValueOfEnumeration(value)) {
				List<Value> dependedValues = Dao.getValuesByEnumValue(value, oldValue);
				for (Value dependedValue : dependedValues) {
					dependedValue.setValue(value.getValue());
					Dao.updateValue(dependedValue);
				}
			}
			Dao.updateValue(value);
			out.print("success");

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private boolean isValueOfEnumeration(Value value) throws SQLException {
		return TableType.ENUMERATION == Dao.getTable(Dao.getKey(value.getKeyId()).getTableId()).getType();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
