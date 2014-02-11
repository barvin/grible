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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Key;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.KeyJson;
import org.grible.security.Security;
import org.grible.servlets.ServletHelper;

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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}

			Table table = null;
			int keyId = 0;
			int keyOrder = 0;
			int refTableId = 0;
			int productId = Integer.parseInt(request.getParameter("product"));
			if (ServletHelper.isJson()) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				table = new JsonDao().getTable(tableId, productId);

				keyOrder = Integer.parseInt(request.getParameter("keyorder"));
				KeyJson[] keys = table.getTableJson().getKeys();
				refTableId = keys[keyOrder - 1].getRefid();
			} else {
				PostgresDao pDao = new PostgresDao();
				Key key = pDao.getKey(Integer.parseInt(request.getParameter("keyid")));
				table = pDao.getTable(key.getTableId());
				refTableId = key.getReferenceTableId();
				keyId = key.getId();
			}

			if (table.getType() != TableType.ENUMERATION) {
				getDialog(out, refTableId, productId, keyId, keyOrder);
			}
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace();
		}
		out.flush();
		out.close();
	}

	private void getDialog(PrintWriter out, int refTableId, int productId, int keyId, int keyOrder) throws Exception {
		String textChecked = "";
		String storageChecked = "";
		String storageDisabled = "";
		String storageSelectDisabled = "";
		String enumChecked = "";
		String enumDisabled = "";
		String enumSelectDisabled = "";
		if (refTableId == 0) {
			textChecked = " checked=\"checked\" ";
			storageSelectDisabled = "disabled=\"disabled\" ";
			enumSelectDisabled = "disabled=\"disabled\" ";
		} else {
			Table refTable = null;
			if (ServletHelper.isJson()) {
				refTable = new JsonDao().getTable(refTableId, productId);
			} else {
				refTable = new PostgresDao().getTable(refTableId);
			}
			if (refTable.getType() == TableType.STORAGE) {
				storageChecked = " checked=\"checked\" ";
				enumSelectDisabled = "disabled=\"disabled\" ";
			} else if (refTable.getType() == TableType.ENUMERATION) {
				enumChecked = " checked=\"checked\" ";
				storageSelectDisabled = "disabled=\"disabled\" ";
			}
		}

		List<Table> dataSotages = DataManager.getInstance().getDao().getTablesOfProduct(productId, TableType.STORAGE);
		if (dataSotages.isEmpty()) {
			storageDisabled = " disabled=\"disabled\" ";
		}

		List<Table> enums = DataManager.getInstance().getDao().getTablesOfProduct(productId, TableType.ENUMERATION);
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
			if (refTableId == dataSotage.getId()) {
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
			if (refTableId == enumeration.getId()) {
				selected = "selected=\"selected\" ";
			}
			out.println("<option value=\"" + enumeration.getId() + "\" " + selected + ">" + enumeration.getName()
					+ "</option>");
		}

		out.println("</select>");
		out.println("<br><br>");
		out.println("<div class=\"dialog-buttons right\">");
		out.println("<button id=\"btn-apply-type\" class=\"ui-button\" keyid=\"" + keyId + "\" keyorder=\"" + keyOrder
				+ "\">Apply</button>");
		out.println("<button class=\"ui-button btn-cancel\">Cancel</button> ");
		out.println("</div></div></div>");
	}

}
