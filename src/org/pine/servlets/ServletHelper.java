/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.pine.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.pine.dao.Dao;
import org.pine.model.Product;
import org.pine.model.Table;
import org.pine.model.TableType;
import org.pine.model.User;
import org.pine.uimodel.Sections;

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

	public static String getFooter(String realPath, String pathToImg) {
		return "<div id=\"footer\" class=\"page-bottom\"><hr><img src=\"" + pathToImg
				+ "/pine_logo_mini.png\"><span class=\"build\">Build: " + getContents(realPath + "/VERSION.txt")
				+ "</span></div>";
	}

	private static String getContents(String path) {
		String content = "";
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(path)));
			try {
				content = IOUtils.toString(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static String getLoadingGif() {
		return "<span id=\"waiting-bg\" class=\"loading\">"
				+ "<img class=\"waiting-gif\" src=\"../img/ajax-loader.gif\" /></span>";
	}

	public static String getUserPanel(User user) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"user-panel\"><span id=\"lbl-user\">User: </span><span id=\"userName\">"
				+ user.getName() + "</span><span id=\"lnk-admin-page\">");
		if (user.isAdmin()) {
			builder.append(" | <a href=\"/pine/admin/\">Admin page</a>");
		}
		builder.append("</span><span id=\"lnk-logout\"> | <a href=\"/pine/logout\">Log out</a></span></div>");

		return builder.toString();
	}

	public static String getContextMenus(String tableType) {
		StringBuilder responseHtml = new StringBuilder();
		responseHtml.append("<ul id=\"categoryMenu\" class=\"contextMenu\">");
		responseHtml.append("<li class=\"add\"><a href=\"#add\">Add data " + tableType + "</a></li>");
		responseHtml.append("<li class=\"add\"><a href=\"#add-category\">Add category</a></li>");
		responseHtml.append("<li class=\"edit\"><a href=\"#edit\">Edit category</a></li>");
		responseHtml.append("<li class=\"delete\"><a href=\"#delete\">Delete category</a></li>");
		responseHtml.append("</ul>");

		responseHtml.append("<ul id=\"keyMenu\" class=\"contextMenu\">");
		responseHtml.append("<li class=\"add\"><a href=\"#add\">Insert column</a></li>");
		responseHtml.append("<li class=\"copy\"><a href=\"#copy\">Duplicate column</a></li>");
		responseHtml.append("<li class=\"fill\"><a href=\"#fill\">Fill column with...</a></li>");
		responseHtml.append("<li class=\"delete\"><a href=\"#delete\">Delete column</a></li>");
		responseHtml.append("</ul>");

		responseHtml.append("<ul id=\"rowMenu\" class=\"contextMenu\">");
		responseHtml.append("<li class=\"add\"><a href=\"#add\">Insert row</a></li>");
		responseHtml.append("<li class=\"copy\"><a href=\"#copy\">Duplicate row</a></li>");
		responseHtml.append("<li class=\"delete\"><a href=\"#delete\">Delete row</a></li>");
		responseHtml.append("</ul>");
		return responseHtml.toString();
	}

	public static String getMain() {
		StringBuilder responseHtml = new StringBuilder();
		responseHtml.append("<div id=\"main\" class=\"table\">");
		responseHtml.append("<div class=\"table-row\">");

		responseHtml.append("<div class=\"table-cell\">");
		responseHtml.append("<div class=\"top-left-cell\">");
		responseHtml.append("<div class=\"icon-button button-enabled\"");
		responseHtml.append("id=\"btn-add-category\"><img src=\"../img/add-icon.png\" class=\"icon-enabled\">");
		responseHtml.append("<span class=\"icon-button-text\"> Add category</span></div>");
		responseHtml.append("</div>");
		responseHtml.append("<div class=\"left-panel\">");
		responseHtml.append("<div id=\"entities-list\">");
		responseHtml.append("<div id=\"category-container\" class=\"categories\"></div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");

		responseHtml.append("<div id=\"delimiter\" class=\"table-cell\">&nbsp;&nbsp;&nbsp;</div>");

		responseHtml.append("<div class=\"table-cell\">");
		responseHtml.append("<div class=\"top-panel\"></div>");
		responseHtml.append("<div id=\"waiting\" class=\"table-cell\">");
		responseHtml.append("<img src=\"../img/ajax-loader.gif\" class=\"waiting-gif\" />");
		responseHtml.append("<div id=\"table-container\">");
		responseHtml.append("<div class=\"table entities-values\"></div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");

		responseHtml.append("</div>");
		responseHtml.append("</div>");
		return responseHtml.toString();
	}

	public static String getBreadCrump(String sectionKey, Product product) {
		StringBuilder responseHtml = new StringBuilder();
		String productName = product.getName();
		String sectionName = Sections.getNameByKey(sectionKey);

		responseHtml.append("<div id=\"breadcrump\" class=\"header-text\">");
		responseHtml.append("<a href=\"/pine\"><span id=\"home\" class=\"link-infront\">Home</span></a>");
		responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a href=\"/pine/?product=").append(product.getId()).append("\">");
		responseHtml.append("<span id=\"product-name\" class=\"link-infront\">");
		responseHtml.append(productName).append("</span></a>");
		responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a href=\"/pine/").append(sectionKey);
		responseHtml.append("/?product=").append(product.getId()).append("\">");
		responseHtml.append("<span id=\"section-name\">");
		responseHtml.append(sectionName).append("</span></a></div>");
		return responseHtml.toString();
	}

	public static String getIncludes() {
		StringBuilder responseHtml = new StringBuilder();
		responseHtml.append("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
		responseHtml.append("<link href=\"../css/style.css\" rel=\"stylesheet\" type=\"text/css\" />");
		responseHtml.append("<link href=\"../css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-1.9.1.min.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-ui-1.10.2.custom.min.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery.contextMenu.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/jquery.noty.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/top.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/defaultVars.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/default.js\"></script>");
		return responseHtml.toString();
	}

	public static String deleteTable(int tableId) throws SQLException {
		Table currentTable = Dao.getTable(tableId);

		boolean isUsedByTables = false;
		String error = "";
		if (currentTable.getType() == TableType.STORAGE) {
			List<Table> tablesUsingThisStorage = Dao.getTablesUsingStorage(tableId);

			if (!tablesUsingThisStorage.isEmpty()) {
				isUsedByTables = true;
				error = "ERROR: Storage '" + currentTable.getName() + "' is used by:";
				for (Table table : tablesUsingThisStorage) {
					error += "<br>- " + table.getName() + " (" + table.getType().toString().toLowerCase() + ");";
				}
			}

		}
		if (isUsedByTables) {
			return error;
		}
		boolean deleted = Dao.deleteTable(tableId);
		if (deleted) {
			switch (currentTable.getType()) {
			case TABLE:
			case STORAGE:
				return "success";

			case PRECONDITION:
			case POSTCONDITION:
				return String.valueOf(currentTable.getParentId());

			default:
				return "success";
			}
		}

		return "ERROR: " + currentTable.getType().toString().toLowerCase()
				+ " was not deleted. See server logs for details.";
	}

}
