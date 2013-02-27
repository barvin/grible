package org.pine.servlets.pages.dialogs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.files.DataFileKey;
import org.pine.model.files.PostconditionValue;
import org.pine.model.files.PreconditionValue;
import org.pine.model.storages.DataStorage;
import org.pine.model.storages.DataStorageKey;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/GetParameterTypeDialog")
public class GetParameterTypeDialog extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetParameterTypeDialog() {
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

		int productId =  Integer.parseInt(request.getParameter("product"));
		int referencedStorageId = 0;
		String content = request.getParameter("content");
		
		if (request.getParameter("storageid") != null) {
			DataStorageKey key = sqlHelper.getDataStorageKey(Integer.parseInt(request.getParameter("storageid")));
			referencedStorageId = key.getReferenceStorageId();
		} else {
			int id = Integer.parseInt(request.getParameter("datafileid"));
			if ("general".equals(request.getParameter("sheet"))) {
				DataFileKey key = sqlHelper.getDataFileKey(id);
				referencedStorageId = key.getReferenceStorageId();
			} else if ("preconditions".equals(request.getParameter("sheet"))) {
				PreconditionValue value = sqlHelper.getPreconditionValue(id);
				referencedStorageId = value.getReferenceStorageId();
			} else {
				PostconditionValue value = sqlHelper.getPostconditionValue(id);
				referencedStorageId = value.getReferenceStorageId();
			}
		}

		getDialog(out, sqlHelper, productId, referencedStorageId, content);
	}

	private void getDialog(PrintWriter out, SQLHelper sqlHelper, int productId, int referencedStorageId, String content) {
		String textChecked = "";
		String storageChecked = "";
		String storageSelectDisabled = "";
		if (referencedStorageId == 0) {
			textChecked = " checked=\"checked\" ";
			storageSelectDisabled = "disabled=\"disabled\" ";
		} else {
			storageChecked = " checked=\"checked\" ";
		}
		out.println("<span class=\"parameter-type-dialog\">");
		out.println("Choose parameter type:");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"cbx-text\" name=\"parameter-type\"" + textChecked + ">Text");
		out.println("<br><br>");
		out.println("<input type=\"radio\" value=\"cbx-storage\" name=\"parameter-type\"" + storageChecked
				+ ">Data Storage: ");
		out.println("<select class=\"select-storage\" " + storageSelectDisabled + ">");

		List<DataStorage> dataSotages = sqlHelper.getDataStoragesByProductId(productId);
		for (DataStorage dataSotage : dataSotages) {
			String selected = "";
			if (referencedStorageId == dataSotage.getId()) {
				selected = "selected=\"selected\" ";
			}
			out.println("<option value=\"" + dataSotage.getId() + "\" " + selected + ">" + dataSotage.getName()
					+ "</option>");
		}

		out.println("</select>");
		out.println("<br><br>");
		out.println("<button class=\"ui-button btn-apply-type\">Apply type</button>");
		out.println("</span>");
		out.println("<input class='changed-value' value='" + content + "' /><span class='old-value' style='display: none;'>"
				+ content + "</span>");

		out.flush();
		out.close();
	}

}
