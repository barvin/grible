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
import org.pine.model.files.DataFileKey;
import org.pine.model.files.DataFileValue;
import org.pine.model.files.PostconditionValue;
import org.pine.model.files.PreconditionValue;
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/SaveDataFile")
public class SaveDataFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveDataFile() {
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

		if (request.getParameter("sheet").equals("general")) {
			if (request.getParameterValues("keyids[]") != null) {
				String[] keyIds = request.getParameterValues("keyids[]");
				String[] keyValues = request.getParameterValues("keyvalues[]");
				sqlHelper.updateDataFileKeys(keyIds, keyValues);
			}

			if (request.getParameterValues("rowids[]") != null) {

				String[] strRowIds = request.getParameterValues("rowids[]");
				String[] strRowNumbers = request.getParameterValues("rownumbers[]");
				int[] rowNumbers = new int[strRowNumbers.length];
				for (int i = 0; i < strRowNumbers.length; i++) {
					rowNumbers[i] = Integer.parseInt(strRowNumbers[i]);
				}

				List<Integer> modifiedRowIds = new ArrayList<>();
				List<Integer> modifiedRowNumbers = new ArrayList<>();
				for (int i = 0; i < rowNumbers.length; i++) {
					if (rowNumbers[i] != i + 1) {
						modifiedRowIds.add(Integer.parseInt(strRowIds[i]));
						modifiedRowNumbers.add(i + 1);
					}
				}
				sqlHelper.updateDataFileRows(modifiedRowIds, modifiedRowNumbers);
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
				sqlHelper.updateDataFileKeysOrder(keyIds, keyNumbers);
			}

			if (request.getParameterValues("ids[]") != null) {
				String[] strIds = request.getParameterValues("ids[]");
				int[] ids = new int[strIds.length];
				for (int i = 0; i < strIds.length; i++) {
					ids[i] = Integer.parseInt(strIds[i]);
				}
				String[] strValues = request.getParameterValues("values[]");

				ArrayList<DataFileValue> values = new ArrayList<>();
				for (int i = 0; i < ids.length; i++) {
					DataFileValue value = sqlHelper.getDataFileValue(ids[i]);
					value.setValue(StringEscapeUtils.unescapeHtml4(strValues[i]));
					values.add(value);
				}

				for (DataFileValue value : values) {
					if (value.isStorage()) {
						String[] strRows = value.getValue().split(";");
						DataFileKey key = sqlHelper.getDataFileKey(value);
						int refStorageId = key.getReferenceStorageId();
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
										+ "' does not contain row number " + strRows[i]
										+ ".\nYou specified it in row: "
										+ sqlHelper.getDataFileRow(value.getRowId()).getOrder()
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
				sqlHelper.updateDataFileValues(values);
			}
		} else if (request.getParameter("sheet").equals("preconditions")) {

			if (request.getParameterValues("keyids[]") != null) {
				String[] keyIds = request.getParameterValues("keyids[]");
				String[] keyValues = request.getParameterValues("keyvalues[]");
				List<PreconditionValue> values = sqlHelper.getPreconditionValuesByPreconditionId(keyIds[0]);
				for (PreconditionValue value : values) {
					for (int j = 0; j < keyValues.length; j++) {
						if (value.getKey().equals(keyValues[j])) {
							out.print("ERROR: Parameter with name '" + value.getKey() + "' already exists.");
							out.flush();
							out.close();
							return;
						}
					}
				}
				sqlHelper.updatePreconditionKeys(keyIds, keyValues);
			}

			if (request.getParameterValues("ids[]") != null) {
				String[] strIds = request.getParameterValues("ids[]");
				int[] ids = new int[strIds.length];
				for (int i = 0; i < strIds.length; i++) {
					ids[i] = Integer.parseInt(strIds[i]);
				}
				String[] strValues = request.getParameterValues("values[]");

				ArrayList<PreconditionValue> preconditions = new ArrayList<>();
				for (int i = 0; i < ids.length; i++) {
					PreconditionValue value = sqlHelper.getPreconditionValue(ids[i]);
					value.setValue(StringEscapeUtils.unescapeHtml4(strValues[i]));
					preconditions.add(value);
				}

				for (PreconditionValue value : preconditions) {
					if (value.isStorage()) {
						String[] strRows = value.getValue().split(";");
						int refStorageId = value.getReferenceStorageId();
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
					}
				}
				sqlHelper.updatePreconditionValues(preconditions);
			}
		} else {
			if (request.getParameterValues("keyids[]") != null) {
				String[] keyIds = request.getParameterValues("keyids[]");
				String[] keyValues = request.getParameterValues("keyvalues[]");
				List<PostconditionValue> values = sqlHelper.getPostconditionValuesByPostconditionId(keyIds[0]);
				for (PostconditionValue value : values) {
					for (int j = 0; j < keyValues.length; j++) {
						if (value.getKey().equals(keyValues[j])) {
							out.print("ERROR: Parameter with name '" + value.getKey() + "' already exists.");
							out.flush();
							out.close();
							return;
						}
					}
				}
				sqlHelper.updatePostconditionKeys(keyIds, keyValues);
			}

			if (request.getParameterValues("ids[]") != null) {
				String[] strIds = request.getParameterValues("ids[]");
				int[] ids = new int[strIds.length];
				for (int i = 0; i < strIds.length; i++) {
					ids[i] = Integer.parseInt(strIds[i]);
				}
				String[] strValues = request.getParameterValues("values[]");

				ArrayList<PostconditionValue> postconditions = new ArrayList<>();
				for (int i = 0; i < ids.length; i++) {
					PostconditionValue value = sqlHelper.getPostconditionValue(ids[i]);
					value.setValue(StringEscapeUtils.unescapeHtml4(strValues[i]));
					postconditions.add(value);
				}

				for (PostconditionValue value : postconditions) {
					if (value.isStorage()) {
						String[] strRows = value.getValue().split(";");
						int refStorageId = value.getReferenceStorageId();
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
					}
				}
				sqlHelper.updatePostconditionValues(postconditions);
			}
		}
		out.print("success");

		out.flush();
		out.close();
	}
}
