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
import org.pine.model.files.DataFileKey;
import org.pine.model.files.DataFileValue;
import org.pine.model.files.PostconditionValue;
import org.pine.model.files.PreconditionValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/ApplyDataFileParameterType")
public class ApplyDataFileParameterType extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ApplyDataFileParameterType() {
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

		int keyId = Integer.parseInt(request.getParameter("keyId"));
		int refStorageId = Integer.parseInt(request.getParameter("storageId"));
		String type = request.getParameter("type"); // "cbx-text" "cbx-storage"
		String sheet = request.getParameter("sheet"); // "general" "preconditions" "postconditions"

		if ("general".equals(sheet)) {

			DataFileKey key = sqlHelper.getDataFileKey(keyId);
			if (((key.getReferenceStorageId() == 0) && ("cbx-text".equals(type)))
					|| ((key.getReferenceStorageId() == refStorageId) && ("cbx-storage".equals(type)))) {
				out.print("not-changed");
			} else if ("cbx-text".equals(type)) {
				key.setReferenceStorageId(0);
				sqlHelper.updateDataFileKey(key);
				sqlHelper.updateDataFileValuesTypes(keyId, false, "NULL");
				out.print("success");
			} else {
				key.setReferenceStorageId(refStorageId);
				ArrayList<DataFileValue> values = sqlHelper.getDataFileValues(key);
				for (DataFileValue value : values) {
					String[] strRows = value.getValue().split(";");
					for (int i = 0; i < strRows.length; i++) {
						if (!StringUtils.isNumeric(strRows[i])) {
							out.print("ERROR: One of indexes is not numeric. Row: "
									+ sqlHelper.getDataFileRow(value.getRowId()).getOrder()
									+ ".\nIf you want to set no index, set '0'.");
							out.flush();
							out.close();
							return;
						} else if ((!strRows[i].equals("0"))
								&& (sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
							out.print("ERROR: Data storage '" + sqlHelper.getDataStorage(refStorageId).getName()
									+ "' does not contain row number " + strRows[i] + ".\nYou specified it in row: "
									+ sqlHelper.getDataFileRow(value.getRowId()).getOrder()
									+ ".\nYou must first create this row in specified data storage.");
							out.flush();
							out.close();
							return;
						}
					}
				}
				for (DataFileValue value : values) {
					if ("0".equals(value.getValue())) {
						value.setStorageIds(null);
					} else {
						String[] strRows = value.getValue().split(";");
						Integer[] intRows = new Integer[strRows.length];
						for (int i = 0; i < strRows.length; i++) {
							intRows[i] = sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i]))
									.getId();
						}
						value.setStorageIds(intRows);
					}
					value.setIsStorage(true);
					sqlHelper.updateDataFileValue(value);
				}
				sqlHelper.updateDataFileKey(key);
				out.print("success");
			}
		} else if ("preconditions".equals(sheet)) {
			PreconditionValue key = sqlHelper.getPreconditionValue(keyId);
			if (((key.getReferenceStorageId() == 0) && ("cbx-text".equals(type)))
					|| ((key.getReferenceStorageId() == refStorageId) && ("cbx-storage".equals(type)))) {
				out.print("not-changed");
			} else if ("cbx-text".equals(type)) {
				key.setReferenceStorageId(0);
				key.setIsStorage(false);
				key.setStorageIds(null);
				sqlHelper.updatePreconditionValue(key);
				out.print("success");
			} else {
				key.setReferenceStorageId(refStorageId);
				String[] strRows = key.getValue().split(";");
				for (int i = 0; i < strRows.length; i++) {
					if (!StringUtils.isNumeric(strRows[i])) {
						out.print("ERROR: One of indexes is not numeric.\nIf you want to set no index, set '0'.");
						out.flush();
						out.close();
						return;
					} else if ((!strRows[i].equals("0"))
							&& (sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
						out.print("ERROR: Data storage '" + sqlHelper.getDataStorage(refStorageId).getName()
								+ "' does not contain row number " + strRows[i]
								+ ".\nYou must first create this row in specified data storage.");
						out.flush();
						out.close();
						return;
					}
				}
				if ("0".equals(key.getValue())) {
					key.setStorageIds(null);
				} else {
					Integer[] intRows = new Integer[strRows.length];
					for (int i = 0; i < strRows.length; i++) {
						intRows[i] = sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i])).getId();
					}
					key.setStorageIds(intRows);
				}
				key.setIsStorage(true);
				sqlHelper.updatePreconditionValue(key);
				out.print("success");
			}
		} else {
			PostconditionValue key = sqlHelper.getPostconditionValue(keyId);
			if (((key.getReferenceStorageId() == 0) && ("cbx-text".equals(type)))
					|| ((key.getReferenceStorageId() == refStorageId) && ("cbx-storage".equals(type)))) {
				out.print("not-changed");
			} else if ("cbx-text".equals(type)) {
				key.setReferenceStorageId(0);
				key.setIsStorage(false);
				key.setStorageIds(null);
				sqlHelper.updatePostconditionValue(key);
				out.print("success");
			} else {
				key.setReferenceStorageId(refStorageId);
				String[] strRows = key.getValue().split(";");
				for (int i = 0; i < strRows.length; i++) {
					if (!StringUtils.isNumeric(strRows[i])) {
						out.print("ERROR: One of indexes is not numeric.\nIf you want to set no index, set '0'.");
						out.flush();
						out.close();
						return;
					} else if ((!strRows[i].equals("0"))
							&& (sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i]))) == null) {
						out.print("ERROR: Data storage '" + sqlHelper.getDataStorage(refStorageId).getName()
								+ "' does not contain row number " + strRows[i]
								+ ".\nYou must first create this row in specified data storage.");
						out.flush();
						out.close();
						return;
					}
				}
				if ("0".equals(key.getValue())) {
					key.setStorageIds(null);
				} else {
					Integer[] intRows = new Integer[strRows.length];
					for (int i = 0; i < strRows.length; i++) {
						intRows[i] = sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i])).getId();
					}
					key.setStorageIds(intRows);
				}
				key.setIsStorage(true);
				sqlHelper.updatePostconditionValue(key);
				out.print("success");
			}
		}

		out.flush();
		out.close();
	}

}
