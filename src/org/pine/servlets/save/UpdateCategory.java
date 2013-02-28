package org.pine.servlets.save;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.files.Category;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/UpdateCategory")
public class UpdateCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateCategory() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		SQLHelper sqlHelper = new SQLHelper();
		String name = request.getParameter("name");

		if ("".equals(name)) {
			out.print("ERROR: Category name cannot be empty.");
		} else {
			int categoryId = Integer.parseInt(request.getParameter("id"));
			Category category = sqlHelper.getCategory(categoryId);
			category.setName(name);
			sqlHelper.updateCategory(category);
			out.print("success");
		}

		out.flush();
		out.close();
	}
}