package org.pine.servlets.delete;

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
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteDataFileRow")
public class DeleteDataFileRow extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteDataFileRow() {
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
		int dataFileId = sqlHelper.getDataFileRow(rowId).getFileId();

		if (sqlHelper.deleteDataFileRow(rowId)) {
			List<Integer> rowIds = new ArrayList<>();
			List<Integer> rowNumbers = new ArrayList<>();
			List<DataFileRow> rows = sqlHelper.getDataFileRows(dataFileId);
			for (int i = 0; i < rows.size(); i++) {
				rowIds.add(rows.get(i).getId());
				rowNumbers.add(i + 1);
			}
			sqlHelper.updateDataFileRows(rowIds, rowNumbers);
			out.print("success");
		} else {
			out.print("Could not delete the row. See server log for detail.");
		}

		out.flush();
		out.close();
	}
}
