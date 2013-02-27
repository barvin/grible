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
@WebServlet("/DeleteStorageCategory")
public class DeleteStorageCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteStorageCategory() {
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
		int categoryId = Integer.parseInt(request.getParameter("id"));

		List<DataStorage> storages = sqlHelper.getDataStoragesByCategoryId(categoryId);

		boolean isUsedByStorages = false;
		boolean isUsedByFiles = false;
		String error = "";

		for (DataStorage storage : storages) {
			List<DataStorage> storagesUsingThisStorage = sqlHelper.getDataStoragesUsingStorage(storage.getId());
			List<DataFile> dataFilesUsingThisStorage = sqlHelper.getDataFilesUsingStorage(storage.getId());

			if (!storagesUsingThisStorage.isEmpty()) {
				isUsedByStorages = true;
				if (!"".equals(error)) {
					error += "\n";
				}
				error += "ERROR: '" + storage.getName() + "' data storage is used by storages:";
				for (DataStorage storageUsingThisStorage : storagesUsingThisStorage) {
					error += "\n- " + storageUsingThisStorage.getName() + ";";
				}
			}

			if (!dataFilesUsingThisStorage.isEmpty()) {
				isUsedByFiles = true;
				if (!"".equals(error)) {
					error += "\n";
				}
				error = "ERROR: '" + storage.getName() + "' data storage is used by data tables:";
				for (DataFile dataFile : dataFilesUsingThisStorage) {
					error += "\n- " + dataFile.getName() + ";";
				}
			}
		}

		if (isUsedByStorages || isUsedByFiles) {
			out.print(error);
		} else {
			boolean deleted = sqlHelper.deleteStorageCategory(categoryId);
			if (deleted) {
				out.print("success");
			} else {
				out.print("ERROR: Category was not deleted. See server logs for details.");
			}
		}

		out.flush();
		out.close();
	}
}
