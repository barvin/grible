package org.pine.servlets.pages;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pine.model.Product;
import org.pine.model.users.User;
import org.pine.servlets.ServletHelper;
import org.pine.sql.SQLHelper;
import org.pine.web.Sections;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/tables/")
public class Tables extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Tables() {
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
		} else if ((request.getParameter("product") == null) && (request.getParameter("id") == null)) {
			response.sendRedirect("/pine");
		} else {

			out.print("<!DOCTYPE html>");
			out.print("<html>");
			out.print("<head>");
			out.print("<title>Data Tables - Pine</title>");
			out.print("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
			out.print("<link href=\"../css/style.css\" rel=\"stylesheet\" type=\"text/css\" />");
			out.print("<link href=\"../css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
			out.print("<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-latest.min.js\"></script>");
			out.print("<script type=\"text/javascript\" src=\"http://code.jquery.com/ui/1.9.1/jquery-ui.js\"></script>");
			out.print("<script type=\"text/javascript\" src=\"../js/jquery.contextMenu.js\"></script>");

			String userName = (String) request.getSession(false).getAttribute("userName");
			User user = sqlHelper.getUserByName(userName);

			int productId;
			int dataTypeId;
			if (request.getParameter("id") != null) {
				dataTypeId = Integer.parseInt(request.getParameter("id"));
				productId = sqlHelper.getProductIdByDataFileId(dataTypeId);
			} else {
				productId = Integer.parseInt(request.getParameter("product"));
				dataTypeId = 0;
			}

			if (!user.hasAccessToProduct(productId)) {
				out.print("<a href=\".\"><span id=\"title\" class=\"header-text\">Pine</span></a>");
				out.print("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
			} else {

				out.print("<script type=\"text/javascript\">");
				out.print("var productId = \"" + productId + "\";");
				out.print("var dataTypeId = \"" + dataTypeId + "\";");
				out.print("var dataType = \"table\";");
				out.print("</script>");
				out.print("<script type=\"text/javascript\" src=\"../js/dataCenter.js\"></script>");

				out.print("</head>");
				out.print("<body>");
				out.print(ServletHelper.getUserPanel(user));
				includeHeader(out, "tables", sqlHelper.getProduct(productId));

				out.print("<div id=\"main\" class=\"table\">");
				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell entities-list\">");
				out.print("<div id=\"category-container\"></div>");
				out.print("</div>");
				out.print("<div id=\"waiting\" class=\"table-cell\">");
				out.print("<img src=\"../img/ajax-loader.gif\" class=\"waiting-gif\" />");
				out.print("<div class=\"table top-panel\"></div>");
				out.print("<div class=\"table entities-values\" style=\"width: auto;\"></div>");
				out.print("</div>");
				out.print("</div>");
				out.print("</div>");
				out.print(ServletHelper.getContextMenus("table"));
				out.print(ServletHelper.getLoadingGif());
			}
			out.print(ServletHelper.getFooter(getServletContext().getRealPath(".")));
			out.print("</body>");
			out.print("</html>");

			out.flush();
			out.close();
		}
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
