package org.pine.servlets.pages.panels;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.files.DataFile;
import org.pine.sql.SQLHelper;


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
		SQLHelper sqlHelper = new SQLHelper();
		if (request.getParameter("datafileid") == null) {
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

			int dataFileId = Integer.parseInt(request.getParameter("datafileid"));

			out.print("<div class=\"table-row\">");
			out.print("<div class=\"table-cell sheet-tab-container left\">");
			out.print("<div class=\"sheet-tab sheet-tab-selected\" label=\"general\">General</div>");
			out.print("</div>");

			DataFile dataFile = sqlHelper.getDataFile(dataFileId);
			out.print("<div class=\"table-cell sheet-tab-container left\">");
			if (dataFile.isHasPreconditions()) {
				out.print("<div class=\"sheet-tab\" label=\"preconditions\">Preconditions</div>");
			} else {
				out.println("<span id=\"btn-add-preconditions\" class=\"add-tab-button\">Add preconditions</span>");
			}
			out.print("</div>");

			out.print("<div class=\"table-cell sheet-tab-container left\">");
			if (dataFile.isHasPostconditions()) {
				out.print("<div class=\"sheet-tab\" label=\"postconditions\">Postconditions</div>");
			} else {
				out.println("<span id=\"btn-add-postconditions\" class=\"add-tab-button\">Add postconditions</span>");
			}
			out.print("</div>");
			
			out.print("<div class=\"table-cell right\">");
			out.println("<span id=\"btn-sort-keys\" class=\"top-panel-button button-disabled\"><input id=\"cbx-sort-keys\" type=\"checkbox\" /> Enable keys ordering</span>");
			out.println("<span id=\"btn-save-data-item\" class=\"top-panel-button button-disabled\"><img class=\"top-panel-icon\" src=\"../img/save-icon.png\"> Save</span>");
			out.println("<span id=\"btn-edit-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/edit-icon.png\"> Edit</span>");
			out.println("<span id=\"btn-delete-data-item\" class=\"top-panel-button button-enabled\"><img class=\"top-panel-icon\" src=\"../img/delete-icon.png\"> Delete</span>");
			out.print("</div></div>");
		}

		out.flush();
		out.close();
	}
}
