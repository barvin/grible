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
import org.pine.sql.SQLHelper;


/**
 * Servlet implementation class GetStorageValues
 */
@WebServlet("/admin/")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		SQLHelper sqlHelper = new SQLHelper();
		if (request.getSession(false).getAttribute("userName") == null) {
			response.sendRedirect("/pine");
		} else {
			out.print("<!DOCTYPE html>");
			out.print("<html>");
			out.print("<head>");
			out.print("<title>Admin - Pine</title>");
			out.print("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
			out.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/style.css\" />");
			out.print("<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-latest.min.js\"></script>");
			out.print("<script type=\"text/javascript\" src=\"../js/admin.js\"></script>");
			out.println("<script type=\"text/javascript\">");

			out.println("function getProductsCheckboxes() {");
			StringBuilder builder = new StringBuilder("return '");
			List<Product> products = sqlHelper.getProducts();
			for (Product product : products) {
				builder.append("<input id=\"" + product.getId() + "\" class=\"access-product\" type=\"checkbox\" > "
						+ product.getName() + "<br>");
			}
			builder.append("';}");
			out.println(builder.toString());

			out.println("</script>");
			out.print("</head>");
			out.print("<body>");

			out.print("<a href=\"/pine\"><span id=\"title\" class=\"header-text\">Pine</span></a>");
			out.print("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
			out.print("<span id=\"product-name\" class=\"header-text\">Admin</span>");

			String userName = (String) request.getSession(false).getAttribute("userName");
			User currentUser = sqlHelper.getUserByName(userName);
			if (!currentUser.isAdmin()) {
				out.print("<br/><br/><div class=\"error-message\">You do not have permissions to access this page.</div>");
			} else {
				out.print("<br /><br />");
				out.print("<span class=\"medium-header\">Users</span>");
				out.print("<br /><br />");
				out.print("<div class=\"table users-list\">");
				out.print("<div class=\"table-row users-header-row\">");
				out.print("<div class=\"table-cell users-header-cell\">UserName</div>");
				out.print("<div class=\"table-cell users-header-cell\">Is Admin</div>");
				out.print("<div class=\"table-cell users-header-cell\">Pruducts</div>");
				out.print("<div class=\"table-cell users-header-cell\">Manage</div>");
				out.print("</div>");

				List<User> users = sqlHelper.getUsers();
				for (User user : users) {
					out.print("<div class=\"table-row users-row\">");
					out.print("<div class=\"table-cell users-cell\" userid=\"" + user.getId() + "\">" + user.getName()
							+ "</div>");
					out.print("<div class=\"table-cell users-cell\">" + user.isAdmin() + "</div>");
					out.print("<div class=\"table-cell users-cell\">" + user.getProductsString() + "</div>");
					out.print("<div class=\"table-cell users-cell\">" + "<button userid=\"" + user.getId()
							+ "\" class=\"ui-button btn-edit-user\">Edit</button> " + "<button userid=\""
							+ user.getId() + "\" class=\"ui-button btn-delete-user\">Delete</button></div>");
					out.print("</div>");
				}
				out.print("</div>");

				out.print("<br /><br />");
				out.print("<span class=\"medium-header\">Add user</span>");
				out.print("<br /><br />");
				out.print("<div class=\"table add-user-table\">");
				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell add-user-table-cell\">User Name:</div>");
				out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"username\"></div>");
				out.print("</div>");
				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell add-user-table-cell\">Password:</div>");
				out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"pass\" type=\"password\" ></div>");
				out.print("</div>");
				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell add-user-table-cell\">Retype it:</div>");
				out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"retype-pass\" type=\"password\" ></div>");
				out.print("</div>");
				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell add-user-table-cell\">Is Admin:</div>");
				out.print("<div class=\"table-cell add-user-table-cell\"><input class=\"isadmin\" type=\"checkbox\" ></div>");
				out.print("</div>");
				out.print("<div class=\"table-row\">");
				out.print("<div class=\"table-cell add-user-table-cell\">Products:</div>");
				out.print("<div class=\"table-cell add-user-table-cell\">");

				for (Product product : products) {
					out.print("<input id=\"" + product.getId() + "\" class=\"access-product\" type=\"checkbox\" > "
							+ product.getName() + "<br>");
				}

				out.print("</div>");
				out.print("</div>");
				out.print("</div>");
				out.print("<br><button id=\"add-user\" class=\"ui-button\">Add</button>");

			}
			out.print("<br><div id=\"footer\"><br/><br/>&copy; Brady Automation Team.</div>");
			out.print("</body>");
			out.print("</html>");
		}

		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
