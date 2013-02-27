package org.pine.servlets.create;

import java.io.IOException;
import java.util.HashMap;

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
@WebServlet("/AddPreconditions")
public class AddPreconditions extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddPreconditions() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		int fileId = Integer.parseInt(request.getParameter("id"));
		SQLHelper sqlHelper = new SQLHelper();

		DataFile dataFile = sqlHelper.getDataFile(fileId);
		dataFile.setHasPreconditions(true);
		sqlHelper.updateDataFile(dataFile);
		
		HashMap<String, String> empty = new HashMap<>();
		empty.put("edit-me", "edit-me");
		sqlHelper.insertPreconditions(fileId, empty);

	}
}
