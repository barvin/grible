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
package org.pine.servlets.app.create;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Row;
import org.pine.model.Table;
import org.pine.model.TableType;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddTable")
public class AddTable extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddTable() {
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
			Dao dao = new Dao();

			Integer categoryId = null;
			if (request.getParameter("categoryid") != null) {
				categoryId = Integer.parseInt(request.getParameter("categoryid"));
			}

			Integer parentId = null;
			if (request.getParameter("parentid") != null) {
				TableType currType = TableType.valueOf(request.getParameter("currTabletype").toUpperCase());
				if (currType == TableType.TABLE) {
					parentId = Integer.parseInt(request.getParameter("parentid"));
				} else {
					Table sibling = dao.getTable(Integer.parseInt(request.getParameter("parentid")));
					parentId = sibling.getParentId();
				}
			}

			String name = request.getParameter("name");
			if (("").equals(name)) {
				throw new Exception("ERROR: Name cannot be empty.");
			}
			TableType type = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
			if (dao.isTableInProductExist(name, type, categoryId)) {
				throw new Exception("ERROR: " + type.toString().toLowerCase() + " with name '" + name
						+ "' already exists.");
			}
			String className = request.getParameter("classname");

			int tableId = dao.insertTable(name, type, categoryId, parentId, className);
			boolean isCopy = Boolean.parseBoolean(request.getParameter("iscopy"));
			if (isCopy) {
				int copyTableId = Integer.parseInt(request.getParameter("copytableid"));
				boolean isOnlyColumns = Boolean.parseBoolean(request.getParameter("isonlycolumns"));
				dao.insertKeysFromOneTableToAnother(copyTableId, tableId);
			} else {
				List<String> keys = new ArrayList<String>();
				keys.add("editme");
				int keyId = dao.insertKeys(tableId, keys).get(0);
				dao.insertRow(tableId, 1);
				List<Row> rows = dao.getRows(tableId);
				dao.insertValuesEmptyWithKeyId(keyId, rows);
			}
			out.print(tableId);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			if (!e.getLocalizedMessage().startsWith("ERROR")) {
				e.printStackTrace();
			}
		}
		out.flush();
		out.close();
	}
}
