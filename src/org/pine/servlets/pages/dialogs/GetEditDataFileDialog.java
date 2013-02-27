package org.pine.servlets.pages.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.files.Category;
import org.pine.model.files.DataFile;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetEditDataFileDialog")
public class GetEditDataFileDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetEditDataFileDialog() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		SQLHelper sqlHelper = new SQLHelper();

		int id = Integer.parseInt(request.getParameter("id"));
		DataFile dataFile = sqlHelper.getDataFile(id);
		String name = dataFile.getName();
		int categoryId = dataFile.getCategoryId();

		out.println("<div id=\"edit-data-table-dialog\" class=\"ui-dialog\">");
		out.println("<div class=\"ui-dialog-title\">Edit data table</div>");
		out.println("<div class=\"ui-dialog-content\">");
		out.println("<div class=\"table\">");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell dialog-cell\">Name:</div>");
		out.println("<div class=\"table-cell dialog-cell\"><input class=\"data-item-name dialog-edit\" value=\"" + name
				+ "\"></div>");
		out.println("</div>");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell dialog-cell\">Category:</div>");
		out.println("<div class=\"table-cell dialog-cell\">");
		out.println("<select class=\"categories\" \">");

		List<Category> categories = sqlHelper.getCategories(sqlHelper.getCategory(categoryId).getProductId());
		for (Category category : categories) {
			String selected = "";
			if (categoryId == category.getId()) {
				selected = "selected=\"selected\" ";
			}
			out.print("<option value=\"" + category.getId() + "\" " + selected + ">" + category.getName() + "</option>");
		}

		out.print("</select></div>");
		out.println("</div>");
		out.println("</div>");
		out.println("<div class=\"dialog-buttons right\">");
		out.println("<button id=\"dialog-btn-edit-data-item\" class=\"ui-button\">Save</button> ");
		out.println("<button class=\"ui-button btn-cancel\">Cancel</button> ");
		out.println("</div></div></div>");

		out.flush();
		out.close();

	}

}
