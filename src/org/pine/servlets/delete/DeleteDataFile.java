package org.pine.servlets.delete;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteDataFile")
public class DeleteDataFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteDataFile() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		int fileId = Integer.parseInt(request.getParameter("id"));
		SQLHelper sqlHelper = new SQLHelper();
		PrintWriter out = response.getWriter();

		boolean deleted = sqlHelper.deleteDataFile(fileId);
		if (deleted) {
			out.print("success");
		} else {
			out.print("ERROR: Table was not deleted. See server logs for details.");
		}
	}
}
