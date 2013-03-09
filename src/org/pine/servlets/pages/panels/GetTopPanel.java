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
package org.pine.servlets.pages.panels;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.dao.Dao;
import org.pine.model.TableType;

/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetTopPanel")
public class GetTopPanel extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetTopPanel() {
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
		Dao dao = new Dao();
		TableType tableType = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
		if (tableType == TableType.STORAGE) {
			out.print("<div class=\"table-row\">");
			out.print("<div class=\"table-cell right\">");
			out.println("<span id=\"btn-sort-keys\" class=\"top-panel-button button-disabled\"><input id=\"cbx-sort-keys\" type=\"checkbox\" /> Enable keys ordering</span>");
			out.println("<span id=\"btn-save-data-item\" class=\"top-panel-button button-disabled\"><img class=\"top-panel-icon\" src=\"../img/save-icon.png\"> Save</span>");
			out.println("<span id=\"btn-edit-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/edit-icon.png\"> Edit</span>");
			out.println("<span id=\"btn-delete-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/delete-icon.png\"> Delete</span>");
			out.println("<span id=\"btn-class-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/brackets.png\"> Class</span>");
			out.print("</div>");
			out.print("</div>");

		} else {
			try {
				Integer tableId = null;
				Integer preId = null;
				Integer postId = null;
				String generalSelected = "";
				String preSelected = "";
				String postSelected = "";
				String editButtonEnable = "button-enabled";

				switch (tableType) {
				case TABLE:
					tableId = Integer.parseInt(request.getParameter("tableid"));
					preId = dao.getChildtable(tableId, TableType.PRECONDITION);
					postId = dao.getChildtable(tableId, TableType.POSTCONDITION);
					generalSelected = " sheet-tab-selected";
					break;

				case PRECONDITION:
					preId = Integer.parseInt(request.getParameter("tableid"));
					tableId = dao.getTable(preId).getParentId();
					postId = dao.getChildtable(tableId, TableType.POSTCONDITION);
					preSelected = " sheet-tab-selected";
					editButtonEnable = "button-disabled";
					break;

				case POSTCONDITION:
					postId = Integer.parseInt(request.getParameter("tableid"));
					tableId = dao.getTable(postId).getParentId();
					preId = dao.getChildtable(tableId, TableType.PRECONDITION);
					postSelected = " sheet-tab-selected";
					editButtonEnable = "button-disabled";
					break;

				default:
					break;
				}

				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell sheet-tab-container left\">");
				out.print("<div id=\"" + tableId + "\" class=\"sheet-tab" + generalSelected
						+ "\" label=\"table\">General</div>");
				out.print("</div>");

				out.print("<div class=\"table-cell sheet-tab-container left\">");
				if (preId != null) {
					out.print("<div id=\"" + preId + "\" class=\"sheet-tab" + preSelected
							+ "\" label=\"precondition\">Preconditions</div>");
				} else {
					out.println("<span id=\"btn-add-preconditions\" class=\"add-tab-button\">Add preconditions</span>");
				}
				out.print("</div>");

				out.print("<div class=\"table-cell sheet-tab-container left\">");
				if (postId != null) {
					out.print("<div id=\"" + postId + "\" class=\"sheet-tab" + postSelected
							+ "\" label=\"postcondition\">Postconditions</div>");
				} else {
					out.println("<span id=\"btn-add-postconditions\" class=\"add-tab-button\">Add postconditions</span>");
				}
				out.print("</div>");

				out.print("<div class=\"table-cell right\">");
				out.println("<span id=\"btn-sort-keys\" class=\"top-panel-button button-disabled\"><input id=\"cbx-sort-keys\" type=\"checkbox\" /> Enable keys ordering</span>");
				out.println("<span id=\"btn-save-data-item\" class=\"top-panel-button button-disabled\"><img class=\"top-panel-icon\" src=\"../img/save-icon.png\"> Save</span>");
				out.println("<span id=\"btn-edit-data-item\" class=\"top-panel-button " + editButtonEnable
						+ "\"><img class=\"top-panel-icon\" src=\"../img/edit-icon.png\"> Edit</span>");
				out.println("<span id=\"btn-delete-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/delete-icon.png\"> Delete</span>");
				out.print("</div></div>");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.flush();
		out.close();
	}
}
