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

import org.pine.model.files.DataFileRow;
import org.pine.model.files.DataFileValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/CopyDataFileRow")
public class CopyDataFileRow extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CopyDataFileRow() {
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

		DataFileRow currentRow = sqlHelper.getDataFileRow(rowId);
		int currentRowNumber = currentRow.getOrder();
		int dataFileId = currentRow.getFileId();
		List<Integer> rowIds = new ArrayList<>();
		List<Integer> rowNumbers = new ArrayList<>();
		List<DataFileRow> rows = sqlHelper.getDataFileRows(dataFileId);
		for (int i = 0; i < rows.size(); i++) {
			rowIds.add(rows.get(i).getId());
			if (rows.get(i).getOrder() > currentRowNumber) {
				rowNumbers.add(i + 2);
			} else {
				rowNumbers.add(i + 1);
			}
		}
		sqlHelper.updateDataFileRows(rowIds, rowNumbers);
		currentRow.setOrder(currentRowNumber + 1);
		int newRowId = sqlHelper.insertDataFileRowCopy(currentRow);

		List<DataFileValue> values = sqlHelper.getDataFileValues(currentRow);
		sqlHelper.insertDataFileValuesWithRowId(newRowId, values);

		out.print("success");
		out.flush();
		out.close();
	}
}