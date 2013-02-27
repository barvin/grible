package org.pine.servlets.pages.panels;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pine.model.files.DataFile;
import org.pine.model.storages.DataStorage;
import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageRow;
import org.pine.model.storages.DataStorageValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetStorageValues")
public class GetStorageValues extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SQLHelper sqlHelper;
	private boolean showUsage;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetStorageValues() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int sotageId = Integer.parseInt(request.getParameter("id"));
		sqlHelper = new SQLHelper();

		showUsage = sqlHelper.getDataStorage(sotageId).isShowUsage();
		List<DataStorageKey> keys = sqlHelper.getDataStorageKeys(sotageId);
		writeKeys(out, keys);

		List<DataStorageRow> rows = sqlHelper.getDataStorageRows(sotageId);
		ArrayList<ArrayList<DataStorageValue>> values = new ArrayList<ArrayList<DataStorageValue>>();
		for (DataStorageRow row : rows) {
			values.add(sqlHelper.getDataStorageValues(row));
		}

		writeValues(out, values);

		out.flush();
		out.close();
	}

	private void writeKeys(PrintWriter out, List<DataStorageKey> keys) {
		out.println("<div class=\"table-row key-row\">");
		out.println("<div class=\"table-cell ui-cell index-header-cell\">Index</div>");
		for (DataStorageKey key : keys) {
			out.println("<div id=\"" + key.getId() + "\" class=\"table-cell ui-cell key-cell\">" + key.getName()
					+ "</div>");
		}
		if (showUsage) {
			out.println("<div class=\"table-cell ui-cell info-key-cell\">Used in tables</div>");
			out.println("<div class=\"table-cell ui-cell info-key-cell\">Used in storages</div>");
		}
		out.println("</div>");
	}

	private void writeValues(PrintWriter out, ArrayList<ArrayList<DataStorageValue>> values) {
		int i = 1;
		for (ArrayList<DataStorageValue> valuesRow : values) {
			out.println("<div class=\"table-row value-row\">");
			out.println("<div id=\"" + valuesRow.get(0).getRowId() + "\" class=\"table-cell ui-cell index-cell\">"
					+ (i++) + "</div>");
			for (DataStorageValue value : valuesRow) {
				String storageCell = (value.isStorage()) ? " storage-cell" : "";
				out.println("<div id=\"" + value.getId() + "\" class=\"table-cell ui-cell value-cell" + storageCell
						+ "\">" + StringEscapeUtils.escapeHtml4(value.getValue()) + "</div>");
			}
			if (showUsage) {
				if (!valuesRow.isEmpty()) {
					out.println("<div class=\"table-cell ui-cell info-cell\">"
							+ getOccurencesInDataFiles(valuesRow.get(0).getRowId()) + "</div>");
					out.println("<div class=\"table-cell ui-cell info-cell\">"
							+ getOccurencesInDataStorages(valuesRow.get(0).getRowId()) + "</div>");
				}
			}
			out.println("</div>");
		}
	}

	private String getOccurencesInDataFiles(int rowId) {
		List<DataFile> files = sqlHelper.getDataFilesUsingRow(rowId);
		String result = "";
		for (int i = 0; i < files.size(); i++) {
			result += files.get(i).getName();
			if (i < (files.size() - 1)) {
				result += ", ";
			}
		}
		return result;
	}

	private String getOccurencesInDataStorages(int rowId) {
		List<DataStorage> storages = sqlHelper.getDataStoragesUsingRow(rowId);
		String result = "";
		for (int i = 0; i < storages.size(); i++) {
			result += storages.get(i).getName();
			if (i < (storages.size() - 1)) {
				result += ", ";
			}
		}
		return result;
	}
}
