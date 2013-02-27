package org.pine.servlets.imp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.pine.excel.ExcelFile;
import org.pine.servlets.ServletHelper;
import org.pine.sql.SQLHelper;
import org.pine.web.TempVars;


/**
 * Servlet implementation class DataFileImport
 */
@MultipartConfig
@WebServlet("/DataFileImport")
public class DataFileImport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataFileImport() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Part filePart = request.getPart("file");
			String fileName = ServletHelper.getFilename(filePart);
			InputStream filecontent = filePart.getInputStream();
			ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));
			SQLHelper sqlHelper = new SQLHelper();

			String dataFileName = fileName.substring(0, fileName.lastIndexOf(".xls"));
			int categoryId = Integer.parseInt(request.getParameter("category"));

			int dataFileId = sqlHelper.insertDataFile(dataFileName, categoryId, excelFile.hasPreconditions(),
					excelFile.hasPostconditions());
			List<Integer> keyIds = sqlHelper.insertDataFileKeys(dataFileId, excelFile.getKeys());
			ArrayList<ArrayList<String>> values = excelFile.getValues();
			List<Integer> rowIds = sqlHelper.insertDataFileRows(dataFileId, values.size());
			sqlHelper.insertDataFileValues(rowIds, keyIds, values);

			if (excelFile.hasPreconditions()) {
				sqlHelper.insertPreconditions(dataFileId, excelFile.getPrecondition());
			}

			if (excelFile.hasPostconditions()) {
				sqlHelper.insertPostconditions(dataFileId, excelFile.getPostcondition());
			}

			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			String message = "'" + dataFileName + "' storage was successfully imported.";
			TempVars.setDataFileImportResult(message);
			response.sendRedirect(destination);
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			TempVars.setDataFileImportResult(message);
			response.sendRedirect(destination);
		}
	}
}
