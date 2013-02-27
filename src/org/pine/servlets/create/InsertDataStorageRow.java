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
@WebServlet("/InsertDataStorageRow")
public class InsertDataStorageRow extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertDataStorageRow() {
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
		int rowId = Integer.parseInt(request.getParameter("rowid"));

		DataStorageRow currentRow = sqlHelper.getDataStorageRow(rowId);
		int currentRowNumber = currentRow.getOrder();
		int storageId = currentRow.getStorageId();
		List<Integer> rowIds = new ArrayList<>();
		List<Integer> rowNumbers = new ArrayList<>();
		List<Integer> oldRowNumbers = new ArrayList<>();
		List<DataStorageRow> rows = sqlHelper.getDataStorageRows(storageId);
		for (int i = 0; i < rows.size(); i++) {
			rowIds.add(rows.get(i).getId());
			if (rows.get(i).getOrder() >= currentRowNumber) {
				rowNumbers.add(i + 2);
			} else {
				rowNumbers.add(i + 1);
			}
			oldRowNumbers.add(i + 1);
		}
		sqlHelper.updateDataStorageRows(rowIds, oldRowNumbers, rowNumbers);
		currentRow.setOrder(currentRowNumber);
		int newRowId = sqlHelper.insertDataStorageRowCopy(currentRow);

		List<DataStorageKey> keys = sqlHelper.getDataStorageKeys(storageId);
		sqlHelper.insertDataStorageValuesEmptyWithRowId(newRowId, keys);

		out.print("success");
		out.flush();
		out.close();
	}
}
