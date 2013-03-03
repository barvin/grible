package org.pine.servlets.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.Product;
import org.pine.model.files.Category;
import org.pine.model.storages.StorageCategory;
import org.pine.model.users.User;
import org.pine.servlets.ServletHelper;
import org.pine.sql.SQLHelper;
import org.pine.web.Sections;
import org.pine.web.TempVars;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/import/")
public class Import extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Import() {
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

		if (request.getSession(false) == null) {
			response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
		} else if (request.getSession(false).getAttribute("userName") == null) {
			response.sendRedirect("/pine/?url=" + request.getRequestURI() + "?" + request.getQueryString());
		} else if (request.getParameter("product") == null) {
			response.sendRedirect("/pine");
		} else {
			out.print("<!DOCTYPE html>");
			out.print("<html>");
			out.print("<head>");
			out.print("<title>Import - Pine</title>");
			out.print("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
			out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
			out.print("</head>");
			out.print("<body>");

			String userName = (String) request.getSession(false).getAttribute("userName");
			User user = sqlHelper.getUserByName(userName);
			int productId = Integer.parseInt(request.getParameter("product"));

			if (!user.hasAccessToProduct(productId)) {
				out.print("<a href=\".\"><span id=\"title\" class=\"header-text\">Pine</span></a>");
				out.print("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
			} else {
				out.print(ServletHelper.getUserPanel(user));
				includeHeader(out, "import", sqlHelper.getProduct(productId));

				out.print("<br />");
				out.print("<span class=\"medium-header\">Data Table Import</span>");
				out.print("<br />");
				out.print("<br />Only .XLS or .XLSX files are acceptable.");
				out.print("<br />First sheet will be processed as the General data sheet.");
				out.print("<br />If \"Preconditions\" sheet is present, it will be processed as Preconditions (1st row - the row of keys, 2nd - the row of values).");
				out.print("<br />If \"Postconditions\" sheet is present, it will be processed as Postconditions (1st row - the row of keys, 2nd - the row of values).");
				out.print("<br />Make sure \"Index\" column or any other help data is absent. Data file name will be taken from the Excel file name.");
				out.print("<br /><br />");

				out.print("<form action=\"../DataFileImport?product=" + productId + "\" method=\"post\" ");
				out.print("enctype=\"multipart/form-data\">");
				out.print("Select category: &nbsp;<select name=\"category\">");

				List<Category> categories = sqlHelper.getCategories(productId);
				for (Category category : categories) {
					out.print("<option value=\"" + category.getId() + "\">" + category.getName() + "</option>");
				}

				out.print("</select> <br /> <br /> <input type=\"file\" name=\"file\" size=\"50\" /> <input type=\"submit\" value=\"Import\" />");
				out.print("</form>");
				out.print("<br />");

				String dataFileImportResult = TempVars.getDataFileImportResult();
				if (!"".equals(dataFileImportResult)) {
					out.print("<div id=\"datafile-import-response\">" + dataFileImportResult + "</div>");
				}

				out.print("<br /><br /><br /><br />");
				out.print("<span class=\"medium-header\">Data Storage Import</span>");
				out.print("<br />");
				out.print("<br />Only .XLS or .XLSX files are acceptable. Only first sheet will be processed.");
				out.print("<br />Make sure \"Index\" column or any other help data is absent. File name would be storage name.");
				out.print("<br /><br />");
				out.print("<form action=\"../DataStorageImport?product=" + productId + "\" method=\"post\" "
						+ "enctype=\"multipart/form-data\">");
				out.print("Select category: &nbsp;<select name=\"category\">");

				List<StorageCategory> storageCategories = sqlHelper.getStorageCategories(productId);
				for (StorageCategory category : storageCategories) {
					out.print("<option value=\"" + category.getId() + "\">" + category.getName() + "</option>");
				}

				out.print("</select> <br /> <br /> Class name: &nbsp;<input name=\"class\" size=\"36\" />"
						+ "<br /> <br /> <input type=\"file\" name=\"file\"	size=\"50\" /> <input type=\"submit\" value=\"Import\" />");
				out.print("</form>");
				out.print("<br />");

				String dataStorageImportResult = TempVars.getDataStorageImportResult();
				if (!"".equals(dataStorageImportResult)) {
					out.print("<div id=\"datastorage-import-response\">" + dataStorageImportResult + "</div>");
				}
			}
			out.print(ServletHelper.getFooter(getServletContext().getRealPath(".")));
			out.print("</body>");
			out.print("</html>");
		}

		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void includeHeader(PrintWriter out, String sectionKey, Product product) {

		String productName = product.getName();
		String sectionName = Sections.getNameByKey(sectionKey);

		out.print("<a href=\"/pine\"><span id=\"title\" class=\"header-text\">Pine</span></a>");
		out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		out.print("<a href=\"/pine/?product=" + product.getId() + "\">");
		out.print("<span id=\"product-name\" class=\"header-text\">" + productName + "</span></a>");
		out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		out.print("<a href=\"/pine/" + sectionKey + "/?product=" + product.getId() + "\">");
		out.print("<span id=\"section-name\" class=\"header-text\">" + sectionName + "</span></a>");
		out.print("<br /><br />");

	}
}
