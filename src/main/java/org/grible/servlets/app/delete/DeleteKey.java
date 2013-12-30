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
package org.grible.servlets.app.delete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.data.Dao;
import org.grible.model.Key;
import org.grible.security.Security;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteKey")
public class DeleteKey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteKey() {
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
			int keyId = Integer.parseInt(request.getParameter("keyid"));

			int tableId = Dao.getKey(keyId).getTableId();
			if (Dao.deleteKey(keyId)) {
				List<Integer> keyIds = new ArrayList<Integer>();
				List<Integer> keyNumbers = new ArrayList<Integer>();
				List<Key> keys = Dao.getKeys(tableId);
				for (int i = 0; i < keys.size(); i++) {
					keyIds.add(keys.get(i).getId());
					keyNumbers.add(i + 1);
				}
				Dao.updateKeys(keyIds, keyNumbers);
				out.print("success");
			} else {
				out.print("Could not delete the column. See server log for detail.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print(e.getLocalizedMessage());
		}
		out.flush();
		out.close();
	}
}
