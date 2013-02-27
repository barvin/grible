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

import org.pine.model.files.DataFileKey;
import org.pine.model.files.DataFileRow;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/InsertDataFileKey")
public class InsertDataFileKey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertDataFileKey() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		SQLHelper sqlHelper = new SQLHelper();
		int keyId = Integer.parseInt(request.getParameter("keyid"));
		String sheet = request.getParameter("sheet");

		if (sheet.equals("general")) {
			DataFileKey currentKey = sqlHelper.getDataFileKey(keyId);
			int currentKeyNumber = currentKey.getOrder();
			int dataFileId = currentKey.getFileId();
			List<Integer> keyIds = new ArrayList<>();
			List<Integer> keyNumbers = new ArrayList<>();
			List<DataFileKey> keys = sqlHelper.getDataFileKeys(dataFileId);
			for (int i = 0; i < keys.size(); i++) {
				keyIds.add(keys.get(i).getId());
				if (keys.get(i).getOrder() >= currentKeyNumber) {
					keyNumbers.add(i + 2);
				} else {
					keyNumbers.add(i + 1);
				}
			}
			sqlHelper.updateDataFileKeys(keyIds, keyNumbers);
			currentKey.setOrder(currentKeyNumber);
			int newKeyId = sqlHelper.insertDataFileKeyCopy(currentKey);

			List<DataFileRow> rows = sqlHelper.getDataFileRows(dataFileId);
			sqlHelper.insertDataFileValuesEmptyWithKeyId(newKeyId, rows);
		} else if (sheet.equals("preconditions")) {
			sqlHelper.insertPrecondition(keyId);
		} else if (sheet.equals("postconditions")) {
			sqlHelper.insertPostcondition(keyId);
		}

		out.print("success");
		out.flush();
		out.close();
	}
}
