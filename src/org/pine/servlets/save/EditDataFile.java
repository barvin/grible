package org.pine.servlets.save;

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
@WebServlet("/EditDataFile")
public class EditDataFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditDataFile() {
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
			out.print("ERROR: Name cannot be empty.");
		} else {
			int id = Integer.parseInt(request.getParameter("id"));
			int categoryId = Integer.parseInt(request.getParameter("categoryid"));
			DataFile dataFile = sqlHelper.getDataFile(id);
			dataFile.setCategoryId(categoryId);
			dataFile.setName(name);
			sqlHelper.updateDataFile(dataFile);
			out.print("success");
		}

		out.flush();
		out.close();
	}
}
