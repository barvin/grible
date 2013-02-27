package org.pine.servlets.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.storages.DataStorageRow;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/AddDataStorage")
public class AddDataStorage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddDataStorage() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		int categoryId = Integer.parseInt(request.getParameter("categoryid"));
		String name = request.getParameter("name");
		String className = request.getParameter("classname");
		SQLHelper sqlHelper = new SQLHelper();

		int dataStorageId = sqlHelper.insertDataStorage(name, categoryId, className);
		List<String> keys = new ArrayList<>();
		keys.add("edit-me");
		int keyId = sqlHelper.insertDataStorageKeys(dataStorageId, keys).get(0);
		sqlHelper.insertDataStorageRows(dataStorageId, 1);
		List<DataStorageRow> rows = sqlHelper.getDataStorageRows(dataStorageId); 
		sqlHelper.insertDataStorageValuesEmptyWithKeyId(keyId, rows);
	}
}
