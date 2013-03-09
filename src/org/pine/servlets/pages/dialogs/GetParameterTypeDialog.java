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
package org.pine.servlets.pages.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.Key;
import org.pine.model.Table;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetParameterTypeDialog")
public class GetParameterTypeDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Dao dao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetParameterTypeDialog() {
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
			String content = request.getParameter("content");
			dao = new Dao();
			Key key = dao.getKey(Integer.parseInt(request.getParameter("keyid")));
			getDialog(out, key, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getDialog(PrintWriter out, Key key, String content) throws SQLException {
		String textChecked = "";
		String storageChecked = "";
		String storageSelectDisabled = "";
		if (key.getReferenceTableId() == 0) {
			textChecked = " checked=\"checked\" ";
			storageSelectDisabled = "disabled=\"disabled\" ";
		} else {
			storageChecked = " checked=\"checked\" ";
		}
		out.println("<span class=\"parameter-type-dialog\">");
		out.println("Choose parameter type:");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"cbx-text\" name=\"parameter-type\"" + textChecked + ">Text");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"cbx-storage\" name=\"parameter-type\"" + storageChecked
				+ ">Data Storage: ");
		out.println("<select class=\"select-storage\" " + storageSelectDisabled + ">");

		List<Table> dataSotages = dao.getStorageTablesOfProductByKeyId(key.getId());
		for (Table dataSotage : dataSotages) {
			String selected = "";
			if (key.getReferenceTableId() == dataSotage.getId()) {
				selected = "selected=\"selected\" ";
			}
			out.println("<option value=\"" + dataSotage.getId() + "\" " + selected + ">" + dataSotage.getName()
					+ "</option>");
		}

		out.println("</select>");
		out.println("<br><br>");
		out.println("<button class=\"ui-button btn-apply-type\">Apply type</button>");
		out.println("</span>");
		out.println("<input class='changed-value' value='" + content
				+ "' /><span class='old-value' style='display: none;'>" + content + "</span>");

		out.flush();
		out.close();
	}

}
