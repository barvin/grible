package org.pine.servlets.save;

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
import org.apache.commons.lang3.StringUtils;
import org.pine.model.storages.DataStorageKey;
import org.pine.model.storages.DataStorageValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/SaveStorage")
public class SaveStorage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveStorage() {
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

		if (request.getParameterValues("keyids[]") != null) {
			String[] keyIds = request.getParameterValues("keyids[]");
			String[] keyValues = request.getParameterValues("keyvalues[]");
			sqlHelper.updateDataStorageKeys(keyIds, keyValues);
		}

		if (request.getParameterValues("rowids[]") != null) {

			String[] strRowIds = request.getParameterValues("rowids[]");
			String[] strRowNumbers = request.getParameterValues("rownumbers[]");
			int[] rowNumbers = new int[strRowNumbers.length];
			for (int i = 0; i < strRowNumbers.length; i++) {
				rowNumbers[i] = Integer.parseInt(strRowNumbers[i]);
			}

			List<Integer> modifiedRowIds = new ArrayList<>();
			List<Integer> oldRowNumbers = new ArrayList<>();
			List<Integer> modifiedRowNumbers = new ArrayList<>();
			for (int i = 0; i < rowNumbers.length; i++) {
				if (rowNumbers[i] != i + 1) {
					modifiedRowIds.add(Integer.parseInt(strRowIds[i]));
					oldRowNumbers.add(rowNumbers[i]);
					modifiedRowNumbers.add(i + 1);
				}
			}

			sqlHelper.updateDataStorageRows(modifiedRowIds, oldRowNumbers, modifiedRowNumbers);
		}

		if (request.getParameterValues("modkeyids[]") != null) {

			String[] strKeyIds = request.getParameterValues("modkeyids[]");
			String[] strKeyNumbers = request.getParameterValues("modkeynumbers[]");
			int[] keyIds = new int[strKeyIds.length];
			for (int i = 0; i < strKeyIds.length; i++) {
				keyIds[i] = Integer.parseInt(strKeyIds[i]);
			}
			int[] keyNumbers = new int[strKeyNumbers.length];
			for (int i = 0; i < strKeyNumbers.length; i++) {
				keyNumbers[i] = Integer.parseInt(strKeyNumbers[i]);
			}
			sqlHelper.updateDataStorageKeysOrder(keyIds, keyNumbers);
		}

		if (request.getParameterValues("ids[]") != null) {
			String[] strIds = request.getParameterValues("ids[]");
			int[] ids = new int[strIds.length];
			for (int i = 0; i < strIds.length; i++) {
				ids[i] = Integer.parseInt(strIds[i]);
			}
			String[] strValues = request.getParameterValues("values[]");

			ArrayList<DataStorageValue> values = new ArrayList<>();
			for (int i = 0; i < ids.length; i++) {
				DataStorageValue value = sqlHelper.getDataStorageValue(ids[i]);
				value.setValue(StringEscapeUtils.unescapeHtml4(strValues[i]));
				values.add(value);
			}

			for (DataStorageValue value : values) {
				if (value.isStorage()) {
					String[] strRows = value.getValue().split(";");
					DataStorageKey key = sqlHelper.getDataStorageKey(value);
					int refStorageId = key.getReferenceStorageId();
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
					if ("0".equals(value.getValue())) {
						value.setStorageIds(null);
					} else {
						Integer[] intRows = new Integer[strRows.length];
						for (int i = 0; i < strRows.length; i++) {
							intRows[i] = sqlHelper.getDataStorageRow(refStorageId, Integer.parseInt(strRows[i]))
									.getId();
						}
						value.setStorageIds(intRows);
					}
					value.setIsStorage(true);
				}
			}
			sqlHelper.updateDataStorageValues(values);
		}
		out.print("success");

		out.flush();
		out.close();
	}
}
