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

import org.pine.model.files.DataFileKey;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/DeleteDataFileKey")
public class DeleteDataFileKey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteDataFileKey() {
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
		String sheet = request.getParameter("sheet"); // "general" "preconditions" "postconditions"
		
		if (sheet.equals("general")) {
			int dataFileId = sqlHelper.getDataFileKey(keyId).getFileId();
			if (sqlHelper.deleteDataFileKey(keyId)) {
				List<Integer> keyIds = new ArrayList<>();
				List<Integer> keyNumbers = new ArrayList<>();
				List<DataFileKey> keys = sqlHelper.getDataFileKeys(dataFileId);
				for (int i = 0; i < keys.size(); i++) {
					keyIds.add(keys.get(i).getId());
					keyNumbers.add(i + 1);
				}
				sqlHelper.updateDataFileKeys(keyIds, keyNumbers);
				out.print("success");
			} else {
				out.print("Could not delete the column. See server log for detail.");
			}
		} else if (sheet.equals("preconditions")) {
			if (sqlHelper.deletePrecondition(keyId)) {
				out.print("success");
			} else {
				out.print("Could not delete the column. See server log for detail.");
			}
		} else if (sheet.equals("postconditions")) {
			if (sqlHelper.deletePostcondition(keyId)) {
				out.print("success");
			} else {
				out.print("Could not delete the column. See server log for detail.");
			}
		}

		out.flush();
		out.close();
	}
}
