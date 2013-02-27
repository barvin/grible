package org.pine.servlets.save;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.storages.DataStorage;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/EditDataStorage")
public class EditDataStorage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditDataStorage() {
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
		String className = request.getParameter("classname");

		if ("".equals(name)) {
			out.print("ERROR: Name cannot be empty.");
		} else if (!className.endsWith("Info")) {
			out.print("ERROR: Class name must end with 'Info'.");
		} else {
			int id = Integer.parseInt(request.getParameter("id"));
			int categoryId = Integer.parseInt(request.getParameter("categoryid"));
			boolean usage = Boolean.parseBoolean(request.getParameter("usage"));
			DataStorage storage = sqlHelper.getDataStorage(id);
			storage.setCategoryId(categoryId);
			storage.setName(name);
			storage.setClassName(className);
			storage.setShowUsage(usage);
			sqlHelper.updateDataStorage(storage);
			out.print("success");
		}

		out.flush();
		out.close();
	}
}
