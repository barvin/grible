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
import java.util.ArrayList;
import java.util.List;

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
@WebServlet("/SaveTable")
public class SaveTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		try {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			Dao dao = new Dao();

			if (request.getParameterValues("keyids[]") != null) {
				String[] keyIds = request.getParameterValues("keyids[]");
				String[] keyValues = request.getParameterValues("keyvalues[]");
				dao.updateKeys(keyIds, keyValues);
			}

			if (request.getParameterValues("rowids[]") != null) {

				String[] strRowIds = request.getParameterValues("rowids[]");
				String[] strRowNumbers = request.getParameterValues("rownumbers[]");
				int[] rowNumbers = new int[strRowNumbers.length];
				for (int i = 0; i < strRowNumbers.length; i++) {
					rowNumbers[i] = Integer.parseInt(strRowNumbers[i]);
				}

				List<Integer> modifiedRowIds = new ArrayList<>();
				List<Integer> oldRowNumbers = new ArrayList<>();
				List<Integer> modifiedRowNumbers = new ArrayList<>();
				for (int i = 0; i < rowNumbers.length; i++) {
					if (rowNumbers[i] != i + 1) {
						modifiedRowIds.add(Integer.parseInt(strRowIds[i]));
						oldRowNumbers.add(rowNumbers[i]);
						modifiedRowNumbers.add(i + 1);
					}
				}

				dao.updateRows(modifiedRowIds, oldRowNumbers, modifiedRowNumbers);
			}

			if (request.getParameterValues("modkeyids[]") != null) {

				String[] strKeyIds = request.getParameterValues("modkeyids[]");
				String[] strKeyNumbers = request.getParameterValues("modkeynumbers[]");
				List<Integer> keyIds = new ArrayList<Integer>();
				for (int i = 0; i < strKeyIds.length; i++) {
					keyIds.add(Integer.parseInt(strKeyIds[i]));
				}
				List<Integer> keyNumbers = new ArrayList<Integer>();
				for (int i = 0; i < strKeyNumbers.length; i++) {
					keyNumbers.add(Integer.parseInt(strKeyNumbers[i]));
				}
				dao.updateKeys(keyIds, keyNumbers);
			}

			if (request.getParameterValues("ids[]") != null) {
				String[] strIds = request.getParameterValues("ids[]");
				int[] ids = new int[strIds.length];
				for (int i = 0; i < strIds.length; i++) {
					ids[i] = Integer.parseInt(strIds[i]);
				}
				String[] strValues = request.getParameterValues("values[]");

				ArrayList<Value> values = new ArrayList<>();
				for (int i = 0; i < ids.length; i++) {
					Value value = dao.getValue(ids[i]);
					value.setValue(StringEscapeUtils.unescapeHtml4(strValues[i]));
					values.add(value);
				}

				for (Value value : values) {
					if (value.isStorage()) {
						String[] strRows = value.getValue().split(";");
						int refStorageId = dao.getRefStorageId(value.getKeyId());
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
										+ "' does not contain row number " + strRows[i]
										+ ".\nYou specified it in row: "
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
				}
				dao.updateValues(values);
			}
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
