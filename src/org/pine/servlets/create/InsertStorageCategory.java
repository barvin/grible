package org.pine.servlets.create;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.storages.StorageCategory;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/InsertStorageCategory")
public class InsertStorageCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertStorageCategory() {
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
		int productId = Integer.parseInt(request.getParameter("product"));
		String name = request.getParameter("name");

		if ("".equals(name)) {
			out.print("ERROR: Category name cannot be empty.");
		} else {
			StorageCategory category = sqlHelper.getStorageCategoryByName(name, productId);
			if (category != null) {
				out.print("ERROR: Category with name '" + name + "' already exists.");
			} else {
				sqlHelper.insertStorageCategory(productId, name);
				out.print("success");
			}
		}

		out.flush();
		out.close();
	}
}
