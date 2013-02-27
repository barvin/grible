package org.pine.servlets.save;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/ApplyStorageParameterType")
public class ApplyStorageParameterType extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApplyStorageParameterType() {
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

		int keyId = Integer.parseInt(request.getParameter("keyId"));
		int refStorageId = Integer.parseInt(request.getParameter("storageId"));
		String type = request.getParameter("type"); // "cbx-text" "cbx-storage"

		DataStorageKey key = sqlHelper.getDataStorageKey(keyId);
		if (((key.getReferenceStorageId() == 0) && ("cbx-text".equals(type)))
				|| ((key.getReferenceStorageId() == refStorageId) && ("cbx-storage".equals(type)))) {
			out.print("not-changed");
		} else if ("cbx-text".equals(type)) {
			key.setReferenceStorageId(0);
			sqlHelper.updateDataStorageKey(key);
			sqlHelper.updateDataStorageValuesTypes(keyId, false, "NULL");
			out.print("success");
		} else {
			key.setReferenceStorageId(refStorageId);
			ArrayList<DataStorageValue> values = sqlHelper.getDataStorageValues(key);
			for (DataStorageValue value : values) {
				String[] strRows = value.getValue().split(";");
				for (int i = 0; i < strRows.length; i++) {
					if (!StringUtils.isNumeric(strRows[i])) {
						out.print("ERROR: One of indexes is not numeric. Row: "
								+ sqlHelper.getDataStorageRow(value.getRowId()).getOrder()
								+ ".\nIf you want to set no index, set '0'.");
						out.flush();
						out.close();
						return;
					} else if ((!strRows[i].equals("0"))
							&& (sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
						out.print("ERROR: Data storage '" + sqlHelper.getDataStorage(refStorageId).getName()
								+ "' does not contain row number " + strRows[i] + ".\nYou specified it in row: "
								+ sqlHelper.getDataStorageRow(value.getRowId()).getOrder()
								+ ".\nYou must first create this row in specified data storage.");
						out.flush();
						out.close();
						return;
					}
				}
			}
			for (DataStorageValue value : values) {
				if ("0".equals(value.getValue())) {
					value.setStorageIds(null);
				} else {
					String[] strRows = value.getValue().split(";");
					Integer[] intRows = new Integer[strRows.length];
					for (int i = 0; i < strRows.length; i++) {
						intRows[i] = sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i])).getId();
					}
					value.setStorageIds(intRows);
				}
				value.setIsStorage(true);
				sqlHelper.updateDataStorageValue(value);
			}
			sqlHelper.updateDataStorageKey(key);
			out.print("success");
		}
		out.flush();
		out.close();
	}

}
