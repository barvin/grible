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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.model.Table;
import org.grible.model.json.KeyJson;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/UpdateKeysOrder")
public class UpdateKeysOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateKeysOrder() {
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
			if (request.getParameterValues("modkeyids[]") != null) {
				String[] strKeyIds = request.getParameterValues("modkeyids[]");
				String[] strKeyNumbers = request.getParameterValues("modkeynumbers[]");
				List<Integer> keyIds = new ArrayList<Integer>();
				List<Integer> keyNumbers = new ArrayList<Integer>();
				for (int i = 0; i < strKeyIds.length; i++) {
					keyIds.add(Integer.parseInt(strKeyIds[i]));
					keyNumbers.add(Integer.parseInt(strKeyNumbers[i]));
				}
				if (ServletHelper.isJson()) {
					int tableId = Integer.parseInt(request.getParameter("tableid"));
					int productId = Integer.parseInt(request.getParameter("product"));
					JsonDao dao = new JsonDao();
					Table table = dao.getTable(tableId, productId);
					KeyJson[] keys = table.getTableJson().getKeys();
					KeyJson[] newKeys = new KeyJson[keys.length];
					String[][] values = table.getTableJson().getValues();
					String[][] newValues = new String[values.length][values[0].length];
					for (int i = 0; i < keys.length; i++) {
						boolean isMoved = false;
						for (int j = 0; j < keyNumbers.size(); j++) {
							if ((i + 1) == keyNumbers.get(j)) {
								isMoved = true;
								newKeys[i] = keys[keyIds.get(j) - 1];
								for (int k = 0; k < values.length; k++) {
									newValues[k][i] = values[k][keyIds.get(j) - 1];
								}
								break;
							}
						}
						if (!isMoved) {
							newKeys[i] = keys[i];
							for (int k = 0; k < values.length; k++) {
								newValues[k][i] = values[k][i];
							}
						}
					}
					table.getTableJson().setKeys(newKeys);
					table.getTableJson().setValues(newValues);
					table.save();
				} else {
					DataManager.getInstance().getDao().updateKeys(keyIds, keyNumbers);
				}
			}
			out.print("success");

		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
