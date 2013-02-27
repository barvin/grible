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
@WebServlet("/DataStorageImport")
public class DataStorageImport extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DataStorageImport() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			String calssName = request.getParameter("class");
			String message = "";

			if (!calssName.endsWith("Info")) {
				message = "ERROR: class name does not end with 'Info'.";
			} else {
				Part filePart = request.getPart("file");
				String fileName = ServletHelper.getFilename(filePart);
				InputStream filecontent = filePart.getInputStream();
				ExcelFile excelFile = new ExcelFile(filecontent, ServletHelper.isXlsx(fileName));
				SQLHelper sqlHelper = new SQLHelper();
				
				String storageName = fileName.substring(0, fileName.lastIndexOf(".xls"));
				int categoryId = Integer.parseInt(request.getParameter("category"));
				int storageId = sqlHelper.insertDataStorage(storageName, categoryId, calssName);
				
				List<Integer> keyIds = sqlHelper.insertDataStorageKeys(storageId, excelFile.getKeys());
				ArrayList<ArrayList<String>> values = excelFile.getValues();
				List<Integer> rowIds = sqlHelper.insertDataStorageRows(storageId, values.size());
				sqlHelper.insertDataStorageValues(rowIds, keyIds, values);

				message = "'" + storageName + "' storage was successfully imported.";
			}

			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			TempVars.setDataStorageImportResult(message);
			response.sendRedirect(destination);
		} catch (Exception e) {
			int productId = Integer.parseInt(request.getParameter("product"));
			String destination = "/pine/import/?product=" + productId;
			String message = "ERROR: " + e.getMessage();
			TempVars.setDataStorageImportResult(message);
			response.sendRedirect(destination);
		}
	}
}
