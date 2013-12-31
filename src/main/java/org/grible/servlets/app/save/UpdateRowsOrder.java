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
import org.grible.security.Security;

/**
 * Servlet implementation class SaveTable
 */
@WebServlet("/UpdateRowsOrder")
public class UpdateRowsOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateRowsOrder() {
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
			if (request.getParameterValues("rowids[]") != null) {
				String[] strRowIds = request.getParameterValues("rowids[]");
				String[] strOldOrder = request.getParameterValues("oldorder[]");
				String[] strNewOrder = request.getParameterValues("neworder[]");
				List<Integer> modifiedRowIds = new ArrayList<Integer>();
				List<Integer> oldRowNumbers = new ArrayList<Integer>();
				List<Integer> modifiedRowNumbers = new ArrayList<Integer>();
				for (int i = 0; i < strRowIds.length; i++) {
					modifiedRowIds.add(Integer.parseInt(strRowIds[i]));
					oldRowNumbers.add(Integer.parseInt(strOldOrder[i]));
					modifiedRowNumbers.add(Integer.parseInt(strNewOrder[i]));
				}
				DataManager.getInstance().getDao().updateRows(modifiedRowIds, oldRowNumbers, modifiedRowNumbers);
			}
			out.print("success");

		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
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
