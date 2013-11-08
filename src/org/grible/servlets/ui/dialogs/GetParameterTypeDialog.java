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
package org.grible.servlets.ui.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.Dao;
import org.grible.model.Key;
import org.grible.model.Table;
import org.grible.model.TableType;

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
			Key key = Dao.getKey(Integer.parseInt(request.getParameter("keyid")));
			if (Dao.getTable(key.getTableId()).getType() != TableType.ENUMERATION) {
				getDialog(out, key);
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void getDialog(PrintWriter out, Key key) throws SQLException {
		String textChecked = "";
		String storageChecked = "";
		String storageDisabled = "";
		String storageSelectDisabled = "";
		String enumChecked = "";
		String enumDisabled = "";
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

		List<Table> dataSotages = Dao.getRefTablesOfProductByKeyId(key.getId(), TableType.STORAGE);
		if (dataSotages.isEmpty()) {
			storageDisabled = " disabled=\"disabled\" ";
		}

		List<Table> enums = Dao.getRefTablesOfProductByKeyId(key.getId(), TableType.ENUMERATION);
		if (enums.isEmpty()) {
			enumDisabled = " disabled=\"disabled\" ";
		}

		out.println("<div id=\"parameter-type-dialog\" class=\"ui-dialog\">");
		out.println("<div class=\"ui-dialog-title\">Change parameter type</div>");
		out.println("<div class=\"ui-dialog-content\">");
		out.println("<br>");
		out.println("<input type=\"radio\" value=\"text\" name=\"parameter-type\"" + textChecked
				+ "><span id=\"label-option-text\" class=\"label-option\">Text</span>");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"storage\" name=\"parameter-type\"" + storageChecked
				+ storageDisabled + "><span id=\"label-option-storage\" class=\"label-option\">Data Storage: </span>");
		out.println("<select class=\"select-storage\" " + storageSelectDisabled + ">");

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
		out.println("<input type=\"radio\" value=\"enumeration\" name=\"parameter-type\"" + enumChecked + enumDisabled
				+ "><span id=\"label-option-enumeration\" class=\"label-option\">Enumeration: </span>");
		out.println("<select class=\"select-enum\" " + enumSelectDisabled + ">");

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
		out.println("<div class=\"dialog-buttons right\">");
		out.println("<button id=\"btn-apply-type\" class=\"ui-button\" keyid=\"" + key.getId() + "\">Apply</button>");
		out.println("<button class=\"ui-button btn-cancel\">Cancel</button> ");
		out.println("</div></div></div>");
	}

}
