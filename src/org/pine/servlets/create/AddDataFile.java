package org.pine.servlets.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.files.DataFileRow;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddDataFile")
public class AddDataFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddDataFile() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		int categoryId = Integer.parseInt(request.getParameter("categoryid"));
		String name = request.getParameter("name");
		SQLHelper sqlHelper = new SQLHelper();

		int dataFileId = sqlHelper.insertDataFile(name, categoryId, false, false);
		List<String> keys = new ArrayList<>();
		keys.add("edit-me");
		int keyId = sqlHelper.insertDataFileKeys(dataFileId, keys).get(0);
		sqlHelper.insertDataFileRows(dataFileId, 1);
		List<DataFileRow> rows = sqlHelper.getDataFileRows(dataFileId); 
		sqlHelper.insertDataFileValuesEmptyWithKeyId(keyId, rows);
	}
}
