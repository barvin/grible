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
import org.pine.model.users.User;
import org.pine.servlets.ServletHelper;
import org.pine.sql.SQLHelper;
import org.pine.web.Section;
import org.pine.web.Sections;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Home() {
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
		out.print("<!DOCTYPE html>");
		out.print("<html>");
		out.print("<head>");
		out.print("<title>Pine</title>");
		out.print("<link rel=\"shortcut icon\" href=\"img/favicon.ico\" >");
		out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
		out.print("</head>");
		out.print("<body>");

		if (request.getSession(false).getAttribute("userName") == null) {
			out.println("<a href=\".\"><span id=\"title\" class=\"header-text\">Pine</span></a>");
			out.println("<div id=\"waiting-bg\">" + "<div id=\"login-dialog\" class=\"ui-dialog\">"
					+ "<div class=\"ui-dialog-title\">Log In</div>" + "<div class=\"ui-dialog-content\">");
			out.println("<form method=\"post\" action=\"Login\">");
			out.println("<div class=\"table\">");
			out.println("<div class=\"table-row\">");
			out.println("<div class=\"table-cell dialog-cell\">Username:</div>");
			out.println("<div class=\"table-cell dialog-cell\"><input class=\"dialog-edit\" name=\"username\"></div>");
			out.println("</div>");
			out.println("<div class=\"table-row\">");
			out.println("<div class=\"table-cell dialog-cell\">Password:</div>");
			out.println("<div class=\"table-cell dialog-cell\"><input type=\"password\" class=\"dialog-edit\" name=\"pass\"></div>");
			out.println("</div>");
			out.println("</div>");
			if (request.getParameter("url") != null) {
				out.println("<input type=\"hidden\" name=\"url\" value=\"" + request.getParameter("url") + "\">");
			}
			out.println("<div class=\"dialog-buttons right\"><input type=\"submit\" value=\"Log in\" class=\"ui-button\"></div></form>");
			out.println("</div>");
			if (request.getSession(false).getAttribute("loginFailed") != null) {
				String message = (String) request.getSession(false).getAttribute("loginFailed");
				out.println("<br><span class=\"dialog-error-message\">" + message + "</span>");
			}
			out.println("</div></div></div>");
		} else {
			String userName = (String) request.getSession(false).getAttribute("userName");
			User user = sqlHelper.getUserByName(userName);

			out.print(ServletHelper.getUserPanel(user));
			out.print("<a href=\".\"><span id=\"title\" class=\"header-text\">Pine</span></a>");

			if (request.getParameter("product") != null) {
				int id = Integer.parseInt(request.getParameter("product"));
				Product product = sqlHelper.getProduct(id);
				if (product != null) {

					out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
					out.print("<a href=\"?product=" + id + "\"><span id=\"product-name\" class=\"header-text\">"
							+ product.getName() + "</span></a>");

					if (!user.hasAccessToProduct(product.getId())) {
						out.print("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
					} else {
						includeSections(out, product);
					}

				} else {
					response.sendRedirect("/pine");
				}
			} else {

				out.println("<div class=\"table\" style=\"width: 300px;\">");
				out.println("<div class=\"table-row\">");
				out.println("<div class=\"table-cell entities-list\">");

				List<Product> products = sqlHelper.getProducts();
				for (Product product : products) {
					if (user.hasAccessToProduct(product.getId())) {
						out.println("<a href=\"?product=" + product.getId() + "\"><div class=\"product\">"
								+ product.getName() + "</div></a>");
					}
				}

				out.println("</div>");
				out.println("</div>");
				out.println("</div>");

			}
		}

		out.println(ServletHelper.getFooter(getServletContext().getRealPath(".")));
		out.println("</body>");
		out.println("</html>");

		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	private void includeSections(PrintWriter out, Product product) {
		out.println("<div class=\"table\">");
		out.println("<div class=\"table-row\">");
		out.println("<div class=\"table-cell entities-list\">");
		List<Section> sections = Sections.getSections();
		for (Section section : sections) {
			out.println("<a href=\"" + section.getKey() + "/?product=" + product.getId() + "\"><div class=\"section\">"
					+ section.getName() + "</div></a>");
		}
		out.println("</div>");
		out.println("<div class=\"table-cell\">");
		for (Section section : sections) {
			out.println("<div class=\"section-desription\">" + section.getDescription() + "</div>");
		}
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
	}
}
