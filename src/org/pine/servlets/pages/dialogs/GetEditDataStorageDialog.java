package org.pine.servlets.pages.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.storages.DataStorage;
import org.pine.model.storages.StorageCategory;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetEditDataStorageDialog")
public class GetEditDataStorageDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetEditDataStorageDialog() {
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
		DataStorage storage = sqlHelper.getDataStorage(id);
		String name = storage.getName();
		String className = storage.getClassName();
		int categoryId = storage.getCategoryId();

		out.println("<div id=\"edit-data-storage-dialog\" class=\"ui-dialog\">");
		out.println("<div class=\"ui-dialog-title\">Edit data storage</div>");
		out.println("<div class=\"ui-dialog-content\">");
		out.println("<div class=\"table\">");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell dialog-cell\">Name:</div>");
		out.println("<div class=\"table-cell dialog-cell\"><input class=\"data-item-name dialog-edit\" value=\"" + name
				+ "\"></div>");
		out.println("</div>");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell dialog-cell\">Class name:</div>");
		out.println("<div class=\"table-cell dialog-cell\"><input class=\"data-storage-class-name dialog-edit\" value=\""
				+ className + "\"></div>");
		out.println("</div>");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell dialog-cell\">Category:</div>");
		out.println("<div class=\"table-cell dialog-cell\">");
		out.println("<select class=\"categories\" \">");

		List<StorageCategory> categories = sqlHelper.getStorageCategories(sqlHelper.getStorageCategory(categoryId)
				.getProductId());
		for (StorageCategory category : categories) {
			String selected = "";
			if (categoryId == category.getId()) {
				selected = "selected=\"selected\" ";
			}
			out.print("<option value=\"" + category.getId() + "\" " + selected + ">" + category.getName() + "</option>");
		}

		out.print("</select></div>");
		out.println("</div>");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell dialog-cell\">Show rows usage:</div>");
		String usage = "";
		if (storage.isShowUsage()) {
			usage = "checked=\"checked\"";
		}
		out.println("<div class=\"table-cell dialog-cell\"><input class=\"usage\" type=\"checkbox\" " + usage
				+ "></div>");
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
