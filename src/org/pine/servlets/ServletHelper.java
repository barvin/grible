package org.pine.servlets;

import java.io.File;

import javax.servlet.http.Part;

import org.pine.model.users.User;
import org.pine.web.BuildNumber;


public class ServletHelper {
	public static boolean isXlsx(String fileName) {
		if (fileName.endsWith(".xls")) {
			return false;
		}
		return true;
	}

	public static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf(File.separator) + 1);
			}
		}
		return null;
	}

	public static String getFooter() {
		return "<div id=\"footer\" class=\"page-bottom\">"
				+ "<br><br><span class=\"build\">Pine Project.&nbsp;&nbsp;" + BuildNumber.getInstrance().getFromFile() + "</span></div>";
	}

	public static String getLoadingGif() {
		return "<span id=\"waiting-bg\" class=\"loading\">"
				+ "<img class=\"waiting-gif\" src=\"../img/ajax-loader.gif\" /></span>";
	}

	public static String getUserPanel(User user) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"user-panel\">User: " + user.getName());
		if (user.isAdmin()) {
			builder.append(" | <a href=\"/pine/admin/\">Admin page</a>");
		}
		builder.append(" | <a href=\"/pine/logout\">Log out</a></div>");

		return builder.toString();
	}

	public static String getContextMenus(String dataType) {
		StringBuilder builder = new StringBuilder();
		builder.append("<ul id=\"categoryMenu\" class=\"contextMenu\">");
		builder.append("<li class=\"add\"><a href=\"#add\">Add data " + dataType + "</a></li>");
		builder.append("<li class=\"edit\"><a href=\"#edit\">Edit category</a></li>");
		builder.append("<li class=\"delete\"><a href=\"#delete\">Delete category</a></li>");
		builder.append("</ul>");

		builder.append("<ul id=\"keyMenu\" class=\"contextMenu\">");
		builder.append("<li class=\"add\"><a href=\"#add\">Insert column</a></li>");
		builder.append("<li class=\"copy\"><a href=\"#copy\">Duplicate column</a></li>");
		builder.append("<li class=\"fill\"><a href=\"#fill\">Fill column with...</a></li>");
		builder.append("<li class=\"delete\"><a href=\"#delete\">Delete column</a></li>");
		builder.append("</ul>");

		builder.append("<ul id=\"rowMenu\" class=\"contextMenu\">");
		builder.append("<li class=\"add\"><a href=\"#add\">Insert row</a></li>");
		builder.append("<li class=\"copy\"><a href=\"#copy\">Duplicate row</a></li>");
		builder.append("<li class=\"delete\"><a href=\"#delete\">Delete row</a></li>");
		builder.append("</ul>");
		return builder.toString();
	}
}
