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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.pine.dao.Dao;
import org.pine.model.Value;

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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			Dao dao = new Dao();

			String strId = request.getParameter("id");
			String strValue = request.getParameter("value");
			int id = Integer.parseInt(strId);

			Value value = dao.getValue(id);
			value.setValue(StringEscapeUtils.unescapeHtml4(strValue));

			if (value.isStorage()) {
				String[] strRows = value.getValue().split(";");
				int refStorageId = dao.getRefStorageId(value.getKeyId());
				for (int i = 0; i < strRows.length; i++) {
					if (!StringUtils.isNumeric(strRows[i])) {
						out.print("\nERROR: Indexes is not numeric. Row: "
								+ dao.getRow(value.getRowId()).getOrder() + ".\nIf you want to set no index, set '0'.");
						out.flush();
						out.close();
						return;
					} else if ((!strRows[i].equals("0"))
							&& (dao.getRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
						out.print("\nERROR: Data storage '" + dao.getTable(refStorageId).getName()
								+ "' does not contain row number " + strRows[i] + ".\nYou specified it in row: "
								+ dao.getRow(value.getRowId()).getOrder()
								+ ".\nYou must first create this row in specified data storage.");
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
						intRows[i] = dao.getRow(refStorageId, Integer.parseInt(strRows[i])).getId();
					}
					value.setStorageIds(intRows);
				}
				value.setIsStorage(true);
			}
			dao.updateValue(value);
			out.print("success");

			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
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
