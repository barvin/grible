package org.pine.servlets.pages.panels;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pine.model.files.PostconditionValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetPostconditions")
public class GetPostconditions extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetPostconditions() {
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
		List<PostconditionValue> prValues = sqlHelper.getPostconditionValues(dataFileId);
		
		writeKeys(out, prValues);
		writeValues(out, prValues);

		out.flush();
		out.close();
	}

	private void writeKeys(PrintWriter out, List<PostconditionValue> values) {
		out.print("<div class=\"table-row key-row\">");
		for (PostconditionValue value : values) {
			out.print("<div id=\"" + value.getId() + "\" class=\"table-cell ui-cell key-cell\">" + value.getKey() + "</div>");
		}
		out.println("</div>");
	}

	private void writeValues(PrintWriter out, List<PostconditionValue> values) {
		out.print("<div class=\"table-row value-row\">");
		for (PostconditionValue value : values) {
			String storageCell = (value.isStorage()) ? " storage-cell" : "";
			out.print("<div id=\"" + value.getId() + "\" class=\"table-cell ui-cell value-cell" + storageCell + "\">"
					+ StringEscapeUtils.escapeHtml4(value.getValue()) + "</div>");
		}
		out.println("</div>");
		
	}
}
