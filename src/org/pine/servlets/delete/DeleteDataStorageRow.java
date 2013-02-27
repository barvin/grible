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

import org.pine.model.files.DataFile;
import org.pine.model.storages.DataStorage;
import org.pine.model.storages.DataStorageRow;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteDataStorageRow")
public class DeleteDataStorageRow extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteDataStorageRow() {
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
		int rowId = Integer.parseInt(request.getParameter("rowid"));
		DataStorageRow row = sqlHelper.getDataStorageRow(rowId);
		int storageId = row.getStorageId();

		List<DataStorage> storagesUsingThisRow = sqlHelper.getDataStoragesUsingRow(rowId);
		List<DataFile> dataFilesUsingThisRow = sqlHelper.getDataFilesUsingRow(rowId);
		boolean isUsedByStorages = true;
		boolean isUsedByFiles = true;
		String error = "";

		if (storagesUsingThisRow.isEmpty()) {
			isUsedByStorages = false;
		} else {
			error += "ERROR: This row is used by storages:";
			for (DataStorage storage : storagesUsingThisRow) {
				error += "\n- " + storage.getName() + ";";
			}
		}

		if (dataFilesUsingThisRow.isEmpty()) {
			isUsedByFiles = false;
		} else {
			if (!"".equals(error)) {
				error += "\n";
			}
			error = "ERROR: This row is used by data tables:";
			for (DataFile dataFile : dataFilesUsingThisRow) {
				error += "\n- " + dataFile.getName() + ";";
			}
		}

		if (isUsedByStorages || isUsedByFiles) {
			out.print(error);
		} else {
			if (sqlHelper.deleteDataStorageRow(rowId)) {
				List<Integer> rowIds = new ArrayList<>();
				List<Integer> oldRowNumbers = new ArrayList<>();
				List<Integer> rowNumbers = new ArrayList<>();
				List<DataStorageRow> rows = sqlHelper.getDataStorageRows(storageId);
				for (int i = 0; i < rows.size(); i++) {
					rowIds.add(rows.get(i).getId());
					oldRowNumbers.add(rows.get(i).getOrder());
					rowNumbers.add(i + 1);
				}
				sqlHelper.updateDataStorageRows(rowIds, oldRowNumbers, rowNumbers);
				out.print("success");
			} else {
				out.print("Could not delete the row. See server log for detail.");
			}
		}
		out.flush();
		out.close();
	}
}
