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

import org.apache.commons.lang3.StringUtils;
import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Value;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/CopyKey")
public class CopyKey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CopyKey() {
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
			
			int keyId = Integer.parseInt(request.getParameter("keyid"));

			Key currentKey = Dao.getKey(keyId);
			int currentKeyNumber = currentKey.getOrder();
			int tableId = currentKey.getTableId();
			List<Integer> keyIds = new ArrayList<Integer>();
			List<Integer> keyNumbers = new ArrayList<Integer>();
			List<Key> keys = Dao.getKeys(tableId);
			for (int i = keys.size() - 1; i >= 0; i--) {
				keyIds.add(keys.get(i).getId());
				if (keys.get(i).getOrder() > currentKeyNumber) {
					keyNumbers.add(i + 2);
				} else {
					keyNumbers.add(i + 1);
				}
			}
			Dao.updateKeys(keyIds, keyNumbers);
			currentKey.setOrder(currentKeyNumber + 1);
			int newKeyId = Dao.insertKeyCopy(currentKey);

			List<Value> values = Dao.getValues(currentKey);
			List<Integer> ids = Dao.insertValuesWithKeyId(newKeyId, values);

			String result = newKeyId + ";" + StringUtils.join(ids, ";");
			out.print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}
}
