package org.pine.servlets.create;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageRow;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/InsertDataStorageKey")
public class InsertDataStorageKey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertDataStorageKey() {
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
		int keyId = Integer.parseInt(request.getParameter("keyid"));

		DataStorageKey currentKey = sqlHelper.getDataStorageKey(keyId);
		int currentKeyNumber = currentKey.getOrder();
		int dataStorageId = currentKey.getStorageId();
		List<Integer> keyIds = new ArrayList<>();
		List<Integer> keyNumbers = new ArrayList<>();
		List<DataStorageKey> keys = sqlHelper.getDataStorageKeys(dataStorageId);
		for (int i = 0; i < keys.size(); i++) {
			keyIds.add(keys.get(i).getId());
			if (keys.get(i).getOrder() >= currentKeyNumber) {
				keyNumbers.add(i + 2);
			} else {
				keyNumbers.add(i + 1);
			}
		}
		sqlHelper.updateDataStorageKeys(keyIds, keyNumbers);
		currentKey.setOrder(currentKeyNumber);
		int newKeyId = sqlHelper.insertDataStorageKeyCopy(currentKey);

		List<DataStorageRow> rows = sqlHelper.getDataStorageRows(dataStorageId);
		sqlHelper.insertDataStorageValuesEmptyWithKeyId(newKeyId, rows);

		out.print("success");
		out.flush();
		out.close();
	}
}
