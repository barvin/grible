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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.grible.dao.PostgresDao;
import org.grible.dbmigrate.oldmodel.Value;
import org.grible.model.TableType;
import org.grible.security.Security;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/SaveCellValue")
public class SaveCellValue extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PostgresDao pDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveCellValue() {
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
			pDao = new PostgresDao();
			
			String strId = request.getParameter("id");
			String strValue = request.getParameter("value");
			int id = Integer.parseInt(strId);

			Value value = pDao.getValue(id);
			String oldValue = value.getValue();
			value.setValue(StringEscapeUtils.unescapeHtml4(strValue));

			if (value.isStorage()) {
				String[] strRows = value.getValue().split(";");
				int refStorageId = pDao.getRefStorageId(value.getKeyId());
				for (int i = 0; i < strRows.length; i++) {
					if (!StringUtils.isNumeric(strRows[i])) {
						out.print("<br>ERROR: Indexes is not numeric. Row: "
								+ pDao.getRow(value.getRowId()).getOrder()
								+ ".<br>If you want to set no index, set '0'.");
						out.flush();
						out.close();
						return;
					} else if ((!strRows[i].equals("0"))
							&& (pDao.getRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
						out.print("<br>ERROR: Data storage '" + pDao.getTable(refStorageId).getName()
								+ "' does not contain row number " + strRows[i] + ".<br>You specified it in row: "
								+ pDao.getRow(value.getRowId()).getOrder()
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
						intRows[i] = pDao
								.getRow(refStorageId, Integer.parseInt(strRows[i])).getId();
					}
					value.setStorageIds(intRows);
				}
				value.setIsStorage(true);
			} else if (isValueOfEnumeration(value)) {
				List<Value> dependedValues = pDao.getValuesByEnumValue(value, oldValue);
				for (Value dependedValue : dependedValues) {
					dependedValue.setValue(value.getValue());
					pDao.updateValue(dependedValue);
				}
			}
			pDao.updateValue(value);
			out.print("success");

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private boolean isValueOfEnumeration(Value value) throws Exception {
		return TableType.ENUMERATION == pDao.getTable(
				pDao.getKey(value.getKeyId()).getTableId()).getType();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
