package org.pine.servlets.pages.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pine.model.files.DataFileValue;
import org.pine.model.files.PostconditionValue;
import org.pine.model.files.PreconditionValue;
import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageRow;
import org.pine.model.storages.DataStorageValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetStorageTooltip")
public class GetStorageTooltip extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SQLHelper sqlHelper;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetStorageTooltip() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String content = request.getParameter("content");

		String[] indexes = content.split(";");
		boolean correctFormat = true;
		for (int i = 0; i < indexes.length; i++) {
			if (!StringUtils.isNumeric(indexes[i])) {
				correctFormat = false;
				break;
			}
			if (("0").equals(indexes[i])) {
				correctFormat = false;
				break;
			}
		}

		if (correctFormat) {
			sqlHelper = new SQLHelper();

			Integer[] storageIds = null;
			if (request.getParameter("storagevalueid") != null) {
				DataStorageValue value = sqlHelper.getDataStorageValue(Integer.parseInt(request
						.getParameter("storagevalueid")));
				storageIds = value.getStorageIds();
			} else {
				int id = Integer.parseInt(request.getParameter("datafilevalueid"));
				if ("general".equals(request.getParameter("sheet"))) {
					DataFileValue value = sqlHelper.getDataFileValue(id);
					storageIds = value.getStorageIds();
				} else if ("preconditions".equals(request.getParameter("sheet"))) {
					PreconditionValue value = sqlHelper.getPreconditionValue(id);
					storageIds = value.getStorageIds();
				} else {
					PostconditionValue value = sqlHelper.getPostconditionValue(id);
					storageIds = value.getStorageIds();
				}
			}

			out.print(content + getStorageTooltip(storageIds));
		} else {
			out.print(content);
		}
		out.flush();
		out.close();

	}

	private String getStorageTooltip(Integer[] integers) {
		if (integers != null) {
			StringBuilder result = new StringBuilder(
					"<div class=\"tooltip\"><div style=\"width: auto;\" class=\"table\">");
			int storageId = sqlHelper.getDataStorageRow(integers[0]).getStorageId();

			List<DataStorageKey> keys = sqlHelper.getDataStorageKeys(storageId);
			result.append("<div class=\"table-row key-row\">");
			for (DataStorageKey key : keys) {
				result.append("<div class=\"table-cell ui-cell-mini key-cell\">").append(key.getName())
						.append("</div>");
			}
			result.append("</div>");

			for (int i = 0; i < integers.length; i++) {
				DataStorageRow row = sqlHelper.getDataStorageRow(integers[i]);
				List<DataStorageValue> values = sqlHelper.getDataStorageValues(row);
				result.append("<div class=\"table-row value-row\">");
				for (DataStorageValue value : values) {
					String storageCell = (value.isStorage()) ? " storage-cell" : "";
					result.append("<div class=\"table-cell ui-cell-mini value-cell ").append(storageCell).append("\">")
							.append(value.getValue()).append("</div>");
				}
				result.append("</div>");
			}
			result.append("</div>");
			result.append("<br><a href=\"/pine/storages/?id=").append(storageId)
					.append("\" target=\"_blank\">Open storage in the new tab</a></div>");
			return result.toString();
		}
		return "";
	}

}
