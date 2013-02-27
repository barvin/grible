package org.pine.servlets.delete;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.files.DataFile;
import org.pine.model.storages.DataStorage;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteStorage")
public class DeleteStorage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteStorage() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		SQLHelper sqlHelper = new SQLHelper();
		int storageId = Integer.parseInt(request.getParameter("id"));

		List<DataStorage> storagesUsingThisStorage = sqlHelper.getDataStoragesUsingStorage(storageId);
		List<DataFile> dataFilesUsingThisStorage = sqlHelper.getDataFilesUsingStorage(storageId);
		boolean isUsedByStorages = true;
		boolean isUsedByFiles = true;
		String error = "";

		if (storagesUsingThisStorage.isEmpty()) {
			isUsedByStorages = false;
		} else {
			error += "ERROR: This data storage is used by storages:";
			for (DataStorage storage : storagesUsingThisStorage) {
				error += "\n- " + storage.getName() + ";";
			}
		}

		if (dataFilesUsingThisStorage.isEmpty()) {
			isUsedByFiles = false;
		} else {
			if (!"".equals(error)) {
				error += "\n";
			}
			error = "ERROR: This data storage is used by data tables:";
			for (DataFile dataFile : dataFilesUsingThisStorage) {
				error += "\n- " + dataFile.getName() + ";";
			}
		}

		if (isUsedByStorages || isUsedByFiles) {
			out.print(error);
		} else {
			boolean deleted = sqlHelper.deleteStorage(storageId);
			if (deleted) {
				out.print("success");
			} else {
				out.print("ERROR: Storage was not deleted. See server logs for details.");
			}
		}

		out.flush();
		out.close();
	}
}
