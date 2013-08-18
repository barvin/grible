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
import org.pine.model.Table;
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
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				Table table = Dao.getTable(tableId);
				responseHtml.append("<div id=\"manage-buttons\">");

				responseHtml.append("<div id=\"btn-save-data-item\" class=\"icon-button button-disabled\">");
				responseHtml.append("<img src=\"../img/save-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/save-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Save</span></div>");

				responseHtml.append("<div id=\"btn-edit-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Edit</span></div>");

				responseHtml.append("<div id=\"btn-delete-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Delete</span></div>");

				responseHtml.append("<div id=\"btn-more\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> More</span>");

				responseHtml.append("<div id=\"data-item-options\">");
				responseHtml.append("<div id=\"btn-sort-keys\" class=\"checkbox-option\">");
				responseHtml.append("<input id=\"cbx-sort-keys\" type=\"checkbox\" />");
				responseHtml.append("<span class=\"icon-button-text\">Enable columns moving</span></div>");

				String usage = "";
				if (table.isShowUsage()) {
					usage = "checked=\"checked\"";
				}
				responseHtml.append("<div id=\"btn-show-usage\" class=\"checkbox-option\">");
				responseHtml.append("<input id=\"cbx-show-usage\" type=\"checkbox\" ").append(usage).append(" />");
				responseHtml.append("<span class=\"icon-button-text\">Show rows usage</span></div>");

				String warning = "";
				if (table.isShowWarning()) {
					warning = "checked=\"checked\"";
				}
				responseHtml.append("<div id=\"btn-show-warning\" class=\"checkbox-option\">");
				responseHtml.append("<input id=\"cbx-show-warning\" type=\"checkbox\" ").append(warning).append(" />");
				responseHtml.append("<span class=\"icon-button-text\">Show duplication warning</span></div>");

				responseHtml.append("<div id=\"btn-class-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/brackets.png\">");
				responseHtml.append("<span class=\"icon-button-text\">Generate class</span></div>");

				responseHtml.append("<div id=\"btn-export-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/export-icon.png\">");
				responseHtml.append("<span class=\"icon-button-text\">Export to Excel</span></div>");

				responseHtml.append("</div></div>");

				responseHtml.append("</div>");

			} else if (tableType == TableType.ENUMERATION) {
				responseHtml.append("<div id=\"manage-buttons\">");

				responseHtml.append("<div id=\"btn-save-data-item\" class=\"icon-button button-disabled\">");
				responseHtml.append("<img src=\"../img/save-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/save-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Save</span></div>");

				responseHtml.append("<div id=\"btn-edit-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Edit</span></div>");

				responseHtml.append("<div id=\"btn-delete-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Delete</span></div>");

				responseHtml.append("<div id=\"btn-more\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> More</span>");

				responseHtml.append("<div id=\"data-item-options\">");

				responseHtml.append("<div id=\"btn-class-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/brackets.png\">");
				responseHtml.append("<span class=\"icon-button-text\">Generate class</span></div>");

				responseHtml.append("</div></div>");

				responseHtml.append("</div>");
			} else {

				Integer tableId = null;
				Integer preId = null;
				Integer postId = null;
				String generalSelected = "";
				String preSelected = "";
				String postSelected = "";
				String editButtonEnable = "button-enabled";
				String showWarning = "";

				switch (tableType) {
				case TABLE:
					tableId = Integer.parseInt(request.getParameter("tableid"));
					preId = Dao.getChildtable(tableId, TableType.PRECONDITION);
					postId = Dao.getChildtable(tableId, TableType.POSTCONDITION);
					generalSelected = " sheet-tab-selected";

					String warning = "";
					if (Dao.getTable(tableId).isShowWarning()) {
						warning = "checked=\"checked\"";
					}
					showWarning += "<div id=\"btn-show-warning\" class=\"checkbox-option\">";
					showWarning += "<input id=\"cbx-show-warning\" type=\"checkbox\" " + warning + " />";
					showWarning += "<span class=\"icon-button-text\">Show duplication warning</span></div>";
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

				responseHtml.append("<div id=\"btn-save-data-item\" class=\"icon-button button-disabled\">");
				responseHtml.append("<img src=\"../img/save-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/save-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Save</span></div>");

				responseHtml.append("<div id=\"btn-edit-data-item\" class=\"icon-button ");
				responseHtml.append(editButtonEnable).append("\">");
				responseHtml.append("<img src=\"../img/edit-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Edit</span></div>");

				responseHtml.append("<div id=\"btn-delete-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> Delete</span></div>");

				responseHtml.append("<div id=\"btn-more\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> More</span>");
				responseHtml.append("<div id=\"data-item-options\">");
				responseHtml.append("<div id=\"btn-sort-keys\" class=\"checkbox-option\">");
				responseHtml.append("<input id=\"cbx-sort-keys\" type=\"checkbox\" />");
				responseHtml.append("<span class=\"icon-button-text\">Enable columns moving</span></div>");
				responseHtml.append(showWarning);
				responseHtml.append("<div id=\"btn-export-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/export-icon.png\">");
				responseHtml.append("<span class=\"icon-button-text\">Export to Excel</span></div>");
				responseHtml.append("</div></div>");

				responseHtml.append("</div>");

				responseHtml.append("<div id=\"table-tabs\">");
				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(generalSelected).append("\">");
				responseHtml.append("<div class=\"sheet-tab-top-border\"></div>");
				responseHtml.append("<div class=\"sheet-tab-top\"></div>");
				responseHtml.append("<div id=\"").append(tableId).append("\" class=\"sheet-tab\"");
				responseHtml.append(" label=\"table\">General</div>");
				responseHtml.append("</div>");

				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(preSelected).append("\">");
				if (preId != null) {
					responseHtml.append("<div class=\"sheet-tab-top-border\"></div>");
					responseHtml.append("<div class=\"sheet-tab-top\"></div>");
					responseHtml.append("<div id=\"").append(preId)
							.append("\" class=\"sheet-tab\" label=\"precondition\">Preconditions</div>");
				} else {
					responseHtml.append("<div class=\"add-tab-top\"></div>");
					responseHtml.append("<div id=\"btn-add-preconditions\" class=\"add-tab\">Add preconditions</div>");
				}
				responseHtml.append("</div>");

				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(postSelected).append("\">");
				if (postId != null) {
					responseHtml.append("<div class=\"sheet-tab-top-border\"></div>");
					responseHtml.append("<div class=\"sheet-tab-top\"></div>");
					responseHtml.append("<div id=\"").append(postId)
							.append("\" class=\"sheet-tab\" label=\"postcondition\">Postconditions</div>");
				} else {
					responseHtml.append("<div class=\"add-tab-top\"></div>");
					responseHtml
							.append("<div id=\"btn-add-postconditions\" class=\"add-tab\">Add postconditions</div>");
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