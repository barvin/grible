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
package org.pine.servlets.ui.dialogs;

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
import org.pine.model.TableType;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetParameterTypeDialog")
public class GetParameterTypeDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			String content = request.getParameter("content").replace("'", "&#39;");
			Key key = Dao.getKey(Integer.parseInt(request.getParameter("keyid")));
			getDialog(out, key, content);
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void getDialog(PrintWriter out, Key key, String content) throws SQLException {
		String textChecked = "";
		String storageChecked = "";
		String storageSelectDisabled = "";
		String enumChecked = "";
		String enumSelectDisabled = "";
		if (key.getReferenceTableId() == 0) {
			textChecked = " checked=\"checked\" ";
			storageSelectDisabled = "disabled=\"disabled\" ";
			enumSelectDisabled = "disabled=\"disabled\" ";
		} else {
			Table refTable = Dao.getTable(key.getReferenceTableId());
			if (refTable.getType() == TableType.STORAGE) {
				storageChecked = " checked=\"checked\" ";
				enumSelectDisabled = "disabled=\"disabled\" ";
			} else if (refTable.getType() == TableType.ENUMERATION) {
				enumChecked = " checked=\"checked\" ";
				storageSelectDisabled = "disabled=\"disabled\" ";
			}
		}
		out.println("<span class=\"parameter-type-dialog\">");
		out.println("Choose parameter type:");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"text\" name=\"parameter-type\"" + textChecked + ">Text");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"storage\" name=\"parameter-type\"" + storageChecked
				+ ">Data Storage: ");
		out.println("<select class=\"select-storage\" " + storageSelectDisabled + ">");

		List<Table> dataSotages = Dao.getRefTablesOfProductByKeyId(key.getId(), TableType.STORAGE);
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
		out.println("<input type=\"radio\" value=\"enumeration\" name=\"parameter-type\"" + enumChecked
				+ ">Enumeration: ");
		out.println("<select class=\"select-enum\" " + enumSelectDisabled + ">");

		List<Table> enums = Dao.getRefTablesOfProductByKeyId(key.getId(), TableType.ENUMERATION);
		for (Table enumeration : enums) {
			String selected = "";
			if (key.getReferenceTableId() == enumeration.getId()) {
				selected = "selected=\"selected\" ";
			}
			out.println("<option value=\"" + enumeration.getId() + "\" " + selected + ">" + enumeration.getName()
					+ "</option>");
		}

		out.println("</select>");
		out.println("<br><br>");
		out.println("<button class=\"ui-button btn-apply-type\">Apply type</button>");
		out.println("</span>");
		out.println("<input class='changed-value' value='" + content
				+ "' /><span class='old-value' style='display: none;'>" + content + "</span>");
	}

}
