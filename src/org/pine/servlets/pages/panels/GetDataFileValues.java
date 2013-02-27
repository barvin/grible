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
import org.pine.model.files.DataFileKey;
import org.pine.model.files.DataFileRow;
import org.pine.model.files.DataFileValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetDataFileValues")
public class GetDataFileValues extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetDataFileValues() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int dataFileId = Integer.parseInt(request.getParameter("id"));
		SQLHelper sqlHelper = new SQLHelper();

		List<DataFileKey> keys = sqlHelper.getDataFileKeys(dataFileId);
		writeKeys(out, keys);

		List<DataFileRow> rows = sqlHelper.getDataFileRows(dataFileId);
		ArrayList<ArrayList<DataFileValue>> values = new ArrayList<ArrayList<DataFileValue>>();
		for (DataFileRow row : rows) {
			values.add(sqlHelper.getDataFileValues(row));
		}
		writeValues(out, values);

		out.flush();
		out.close();
	}

	private void writeKeys(PrintWriter out, List<DataFileKey> keys) {
		out.print("<div class=\"table-row key-row\">");
		out.print("<div class=\"table-cell ui-cell index-header-cell\">Index</div>");
		for (DataFileKey key : keys) {
			out.print("<div id=\"" + key.getId() + "\" class=\"table-cell ui-cell key-cell\">" + key.getName()
					+ "</div>");
		}
		out.println("</div>");
	}

	private void writeValues(PrintWriter out, ArrayList<ArrayList<DataFileValue>> values) {
		int i = 1;
		for (ArrayList<DataFileValue> valuesRow : values) {
			out.print("<div class=\"table-row value-row\">");
			out.print("<div id=\"" + valuesRow.get(0).getRowId() + "\" class=\"table-cell ui-cell index-cell\">"
					+ (i++) + "</div>");
			for (DataFileValue value : valuesRow) {
				String storageCell = (value.isStorage()) ? " storage-cell" : "";
				out.print("<div id=\"" + value.getId() + "\" class=\"table-cell ui-cell value-cell" + storageCell
						+ "\">" + StringEscapeUtils.escapeHtml4(value.getValue()) + "</div>");
			}
			out.println("</div>");
		}
	}
}
