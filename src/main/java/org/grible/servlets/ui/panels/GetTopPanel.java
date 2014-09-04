/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 * Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.servlets.ui.panels;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.security.Security;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.settings.Lang;

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
			if (Security.anyServletEntryCheckFailed(request, response)) {
				return;
			}
			StringBuilder responseHtml = new StringBuilder();
			TableType tableType = TableType.valueOf(request.getParameter("tabletype").toUpperCase());
			int productId = 0;
			if (isJson()) {
				productId = Integer.parseInt(request.getParameter("product"));
			}
			if (tableType == TableType.STORAGE) {
				int tableId = Integer.parseInt(request.getParameter("tableid"));
				int filter = Integer.parseInt(request.getParameter("filter"));
				Table table = null;
				if (isJson()) {
					table = new JsonDao().getTable(tableId, productId);
				} else {
					table = new PostgresDao().getTable(tableId);
				}
				responseHtml.append("<div id=\"manage-buttons\">");

				if (filter == 0) {
					responseHtml.append("<div id=\"btn-save-data-item\" class=\"icon-button button-disabled\">");
					responseHtml.append("<img src=\"../img/save-icon.png\" class=\"icon-enabled\">");
					responseHtml.append("<img src=\"../img/save-icon-disabled.png\" class=\"icon-disabled\">");
					responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("save") + "</span></div>");
				} else {
					responseHtml.append("<div id=\"btn-discard-filter\" class=\"icon-button button-enabled\">");
					responseHtml.append("<img src=\"../img/discard-icon.png\" class=\"icon-enabled\">");
					responseHtml.append("<img src=\"../img/discard-icon.png\" class=\"icon-disabled\">");
					responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("discardfilter")
							+ "</span></div>");
				}

				responseHtml.append("<div id=\"btn-edit-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("edit") + "</span></div>");

				responseHtml.append("<div id=\"btn-delete-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("del") + "</span></div>");

				responseHtml.append("<div id=\"btn-more\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("more") + "</span>");

				responseHtml.append("<div id=\"data-item-options\">");

				responseHtml.append("<div id=\"btn-show-usage\" class=\"checkbox-option\">");
				responseHtml.append("<input id=\"cbx-show-usage\" type=\"checkbox\" />");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("showrowsusage") + "</span></div>");

				String warning = "";
				if (table.isShowWarning()) {
					warning = "checked=\"checked\"";
				}
				responseHtml.append("<div id=\"btn-show-warning\" class=\"checkbox-option\">");
				responseHtml.append("<input id=\"cbx-show-warning\" type=\"checkbox\" ").append(warning).append(" />");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("showduplicates") + "</span></div>");

				responseHtml.append("<div id=\"btn-class-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/brackets.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("generateclass") + "</span></div>");

				responseHtml.append("<div id=\"btn-filter\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/filter-icon.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("filterby") + "</span></div>");

				responseHtml.append("<div id=\"btn-export-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/export-icon.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("exporttoexcel") + "</span></div>");

				responseHtml.append("<div id=\"btn-help\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/info-icon-mini.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("help") + "</span></div>");

				responseHtml.append("</div></div>");

				responseHtml.append("</div>");

			} else if (tableType == TableType.ENUMERATION) {
				responseHtml.append("<div id=\"manage-buttons\">");

				responseHtml.append("<div id=\"btn-save-data-item\" class=\"icon-button button-disabled\">");
				responseHtml.append("<img src=\"../img/save-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/save-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("save") + "</span></div>");

				responseHtml.append("<div id=\"btn-edit-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("edit") + "</span></div>");

				responseHtml.append("<div id=\"btn-delete-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("del") + "</span></div>");

				responseHtml.append("<div id=\"btn-more\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("more") + "</span>");

				responseHtml.append("<div id=\"data-item-options\">");

				responseHtml.append("<div id=\"btn-class-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/brackets.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("generateclass") + "</span></div>");

				responseHtml.append("<div id=\"btn-help\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/info-icon-mini.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("help") + "</span></div>");

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
					Table table = null;
					if (isJson()) {
						JsonDao dao = new JsonDao();
						table = dao.getTable(tableId, productId);
						preId = dao.getChildTableId(tableId, productId, TableType.PRECONDITION);
						postId = dao.getChildTableId(tableId, productId, TableType.POSTCONDITION);
					} else {
						PostgresDao dao = new PostgresDao();
						table = dao.getTable(tableId);
						preId = dao.getChildTableId(tableId, TableType.PRECONDITION);
						postId = dao.getChildTableId(tableId, TableType.POSTCONDITION);
					}
					generalSelected = " sheet-tab-selected";

					String warning = "";
					if (table.isShowWarning()) {
						warning = "checked=\"checked\"";
					}
					showWarning += "<div id=\"btn-show-warning\" class=\"checkbox-option\">";
					showWarning += "<input id=\"cbx-show-warning\" type=\"checkbox\" " + warning + " />";
					showWarning += "<span class=\"icon-button-text\">" + Lang.get("showduplicates") + "</span></div>";
					break;

				case PRECONDITION:
					preId = Integer.parseInt(request.getParameter("tableid"));
					if (isJson()) {
						JsonDao dao = new JsonDao();
						tableId = dao.getParentTableId(preId, productId, TableType.PRECONDITION);
						postId = dao.getChildTableId(tableId, productId, TableType.POSTCONDITION);
					} else {
						PostgresDao dao = new PostgresDao();
						Table preTable = dao.getTable(preId);
						tableId = preTable.getParentId();
						postId = dao.getChildTableId(tableId, TableType.POSTCONDITION);
					}
					preSelected = " sheet-tab-selected";
					editButtonEnable = "button-disabled";
					break;

				case POSTCONDITION:
					postId = Integer.parseInt(request.getParameter("tableid"));
					Table postTable = null;
					if (isJson()) {
						JsonDao dao = new JsonDao();
						tableId = dao.getParentTableId(postId, productId, TableType.POSTCONDITION);
						preId = dao.getChildTableId(tableId, productId, TableType.PRECONDITION);
					} else {
						PostgresDao dao = new PostgresDao();
						postTable = dao.getTable(postId);
						tableId = postTable.getParentId();
						preId = dao.getChildTableId(tableId, TableType.PRECONDITION);
					}
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
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("save") + "</span></div>");

				responseHtml.append("<div id=\"btn-edit-data-item\" class=\"icon-button ");
				responseHtml.append(editButtonEnable).append("\">");
				responseHtml.append("<img src=\"../img/edit-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/edit-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("edit") + "</span></div>");

				responseHtml.append("<div id=\"btn-delete-data-item\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/delete-icon-disabled.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("del") + "</span></div>");

				responseHtml.append("<div id=\"btn-more\" class=\"icon-button button-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-enabled\">");
				responseHtml.append("<img src=\"../img/more.png\" class=\"icon-disabled\">");
				responseHtml.append("<span class=\"icon-button-text\"> " + Lang.get("more") + "</span>");

				responseHtml.append("<div id=\"data-item-options\">");

				responseHtml.append(showWarning);

				responseHtml.append("<div id=\"btn-export-data-item\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/export-icon.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("exporttoexcel") + "</span></div>");

				responseHtml.append("<div id=\"btn-help\" class=\"checkbox-option\">");
				responseHtml.append("<img src=\"../img/info-icon-mini.png\">");
				responseHtml.append("<span class=\"icon-button-text\">" + Lang.get("help") + "</span></div>");

				responseHtml.append("</div></div>");

				responseHtml.append("</div>");

				responseHtml.append("<div id=\"table-tabs\">");
				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(generalSelected).append("\">");
				responseHtml.append("<div class=\"sheet-tab-top-border\"></div>");
				responseHtml.append("<div class=\"sheet-tab-top\"></div>");
				responseHtml.append("<div id=\"").append(tableId).append("\" class=\"sheet-tab\"");
				responseHtml.append(" label=\"table\">" + Lang.get("general") + "</div>");
				responseHtml.append("</div>");

				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(preSelected).append("\">");
				if (preId != null) {
					responseHtml.append("<div class=\"sheet-tab-top-border\"></div>");
					responseHtml.append("<div class=\"sheet-tab-top\"></div>");
					responseHtml.append("<div id=\"").append(preId)
							.append("\" class=\"sheet-tab\" label=\"precondition\">" + Lang.get("preconditions") + "</div>");
				} else {
					responseHtml.append("<div class=\"add-tab-top\"></div>");
					responseHtml.append("<div id=\"btn-add-preconditions\" class=\"add-tab\">" + Lang.get("addpreconditions") + "</div>");
				}
				responseHtml.append("</div>");

				responseHtml.append("<div class=\"sheet-tab-container");
				responseHtml.append(postSelected).append("\">");
				if (postId != null) {
					responseHtml.append("<div class=\"sheet-tab-top-border\"></div>");
					responseHtml.append("<div class=\"sheet-tab-top\"></div>");
					responseHtml.append("<div id=\"").append(postId)
							.append("\" class=\"sheet-tab\" label=\"postcondition\">" + Lang.get("postconditions") + "</div>");
				} else {
					responseHtml.append("<div class=\"add-tab-top\"></div>");
					responseHtml
							.append("<div id=\"btn-add-postconditions\" class=\"add-tab\">" + Lang.get("addpostconditions") + "</div>");
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

	private boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}

}