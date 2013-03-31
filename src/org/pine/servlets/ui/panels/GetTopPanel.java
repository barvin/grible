/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 * Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.pine.servlets.ui.panels;

import java.io.IOException;
import java.io.PrintWriter;

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
		try {
			StringBuilder responseHtml = new StringBuilder();
			TableType tableType = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
			if (tableType == TableType.STORAGE) {
				responseHtml.append("<div id=\"manage-buttons\">");
				responseHtml
						.append("<span id=\"btn-sort-keys\" class=\"top-panel-button button-disabled\"><input id=\"cbx-sort-keys\" type=\"checkbox\" /> Enable columns moving</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml
						.append("<span id=\"btn-save-data-item\" class=\"top-panel-button button-disabled\"><img class=\"top-panel-icon\" src=\"../img/save-icon.png\"> Save</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml
						.append("<span id=\"btn-edit-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/edit-icon.png\"> Edit</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml
						.append("<span id=\"btn-delete-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/delete-icon.png\"> Delete</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml
						.append("<span id=\"btn-class-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/brackets.png\"> Class</span>");
				responseHtml.append("</div>");

			} else {

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
					preId = Dao.getChildtable(tableId, TableType.PRECONDITION);
					postId = Dao.getChildtable(tableId, TableType.POSTCONDITION);
					generalSelected = " sheet-tab-selected";
					break;

				case PRECONDITION:
					preId = Integer.parseInt(request.getParameter("tableid"));
					tableId = Dao.getTable(preId).getParentId();
					postId = Dao.getChildtable(tableId, TableType.POSTCONDITION);
					preSelected = " sheet-tab-selected";
					editButtonEnable = "button-disabled";
					break;

				case POSTCONDITION:
					postId = Integer.parseInt(request.getParameter("tableid"));
					tableId = Dao.getTable(postId).getParentId();
					preId = Dao.getChildtable(tableId, TableType.PRECONDITION);
					postSelected = " sheet-tab-selected";
					editButtonEnable = "button-disabled";
					break;

				default:
					break;
				}
				responseHtml.append("<div id=\"manage-buttons\">");
				responseHtml
						.append("<span id=\"btn-sort-keys\" class=\"top-panel-button button-disabled\"><input id=\"cbx-sort-keys\" type=\"checkbox\" /> Enable columns moving</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml
						.append("<span id=\"btn-save-data-item\" class=\"top-panel-button button-disabled\"><img class=\"top-panel-icon\" src=\"../img/save-icon.png\"> Save</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml.append("<span id=\"btn-edit-data-item\" class=\"top-panel-button ")
						.append(editButtonEnable)
						.append("\"><img class=\"top-panel-icon\" src=\"../img/edit-icon.png\"> Edit</span>");
				responseHtml.append("&nbsp;&nbsp;");
				responseHtml
						.append("<span id=\"btn-delete-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/delete-icon.png\"> Delete</span>");
				responseHtml.append("</div>");

				responseHtml.append("<div id=\"table-tabs\">");
				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(generalSelected).append("\">");
				responseHtml.append("<div class=\"sheet-tab-top\"></div>");
				responseHtml.append("<div id=\"").append(tableId).append("\" class=\"sheet-tab\"");
				responseHtml.append(" label=\"table\">General</div>");
				responseHtml.append("</div>");

				responseHtml.append("<div class=\"sheet-tab-container\">");
				if (preId != null) {
					responseHtml.append("<div id=\"").append(preId).append("\" class=\"sheet-tab").append(preSelected)
							.append("\" label=\"precondition\">Preconditions</div>");
				} else {
					responseHtml
							.append("<span id=\"btn-add-preconditions\" class=\"add-tab-button\">Add preconditions</span>");
				}
				responseHtml.append("</div>");

				responseHtml.append("<div class=\"sheet-tab-container\">");
				if (postId != null) {
					responseHtml.append("<div id=\"").append(postId).append("\" class=\"sheet-tab")
							.append(postSelected).append("\" label=\"postcondition\">Postconditions</div>");
				} else {
					responseHtml
							.append("<span id=\"btn-add-postconditions\" class=\"add-tab-button\">Add postconditions</span>");
				}
				responseHtml.append("</div>");
				responseHtml.append("</div>");
			}
			out.print(responseHtml.toString());
		} catch (Exception e) {
			out.print(e.getLocalizedMessage());
			e.printStackTrace(out);
		}

		out.flush();
		out.close();
	}
}