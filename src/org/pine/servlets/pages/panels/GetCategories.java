package org.pine.servlets.pages.panels;

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
import org.pine.model.storages.DataStorage;
import org.pine.model.storages.StorageCategory;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetCategories")
public class GetCategories extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetCategories() {
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
		int productId = Integer.parseInt(request.getParameter("productId"));
		int dataTypeId = Integer.parseInt(request.getParameter("dataTypeId"));
		String dataType = request.getParameter("dataType");
		SQLHelper sqlHelper = new SQLHelper();

		if (dataType.equals("storage")) {
			List<StorageCategory> categories = sqlHelper.getStorageCategories(productId);
			for (StorageCategory category : categories) {
				List<DataStorage> dataSotages = sqlHelper.getDataStoragesByCategoryId(category.getId());
				boolean categorySelected = false;
				for (DataStorage dataSotage : dataSotages) {
					if (dataSotage.getId() == dataTypeId) {
						categorySelected = true;
						break;
					}
				}
				String categorySelectedClass = "";
				if (categorySelected) {
					categorySelectedClass = " category-item-selected";
				}
				out.print("<h3 id=\"" + category.getId() + "\" class=\"category-item" + categorySelectedClass + "\">" + category.getName()
						+ "</h3><div>");
				for (DataStorage dataSotage : dataSotages) {
					String selected = (dataSotage.getId() == dataTypeId) ? " data-item-selected" : "";
					out.print("<div id=\"" + dataSotage.getId() + "\" class=\"data-item" + selected + "\">"
							+ dataSotage.getName() + "</div>");
				}
				out.print("</div>");
			}
		} else {
			List<Category> categories = sqlHelper.getCategories(productId);
			for (Category category : categories) {
				List<DataFile> dataFiles = sqlHelper.getDataFiles(category.getId());
				boolean categorySelected = false;
				for (DataFile dataFile : dataFiles) {
					if (dataFile.getId() == dataTypeId) {
						categorySelected = true;
						break;
					}
				}
				String categorySelectedClass = "";
				if (categorySelected) {
					categorySelectedClass = " category-item-selected";
				}
				out.print("<h3 id=\"" + category.getId() + "\" class=\"category-item" + categorySelectedClass + "\">"
						+ category.getName() + "</h3><div>");
				for (DataFile dataFile : dataFiles) {
					String selected = (dataFile.getId() == dataTypeId) ? " data-item-selected" : "";
					out.print("<div id=\"" + dataFile.getId() + "\"	 class=\"data-item" + selected + "\">"
							+ dataFile.getName() + "</div>");
				}
				out.print("</div>");
			}
		}

		out.flush();
		out.close();
	}
}
