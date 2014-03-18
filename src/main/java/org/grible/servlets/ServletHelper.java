/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.dao.JsonDao;
import org.grible.dao.PostgresDao;
import org.grible.excel.ExcelFile;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.User;
import org.grible.settings.AppTypes;
import org.grible.settings.GlobalSettings;
import org.grible.uimodel.Sections;

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

	public static String getFooter(String realPath) {
		return "<div id=\"footer\"><span class=\"build\">Version: " + getVersion(realPath) + "</span></div>";
	}

	// <img src=\"" + pathToImg + "/grible_logo_mini.png\">

	public static String getVersion(String realPath) {
		return getContents(realPath + "/VERSION.txt");
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

	public static String getUserPanel() {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"user-panel\"><span id=\"lnk-admin-page\">");
		builder.append("<a class=\"confirm-needed\" href=\"/admin/\">Admin page</a>");
		builder.append("</span><span id=\"lnk-settings\"> | <a class=\"confirm-needed\" ");
		builder.append("href=\"/settings/\">Settings</a></span>");
		builder.append("<span id=\"lnk-logout\"> | <a class=\"confirm-needed\" ");
		builder.append("href=\"/logout\">Log out</a></span></div>");
		return builder.toString();
	}

	public static String getUserPanel(User user) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"user-panel\"><span id=\"lbl-user\">User: </span><span id=\"userName\">"
				+ user.getName() + "</span><span id=\"lnk-admin-page\">");
		if (user.isAdmin()) {
			builder.append(" | <a class=\"confirm-needed\" href=\"/admin/\">Admin page</a>");
		}
		builder.append(
				"</span><span id=\"lnk-settings\"> | <a class=\"confirm-needed\" href=\"/settings/\">Settings</a></span>")
				.append("<span id=\"lnk-logout\"> | <a class=\"confirm-needed\" href=\"/logout\">Log out</a></span></div>");

		return builder.toString();
	}

	// public static String getContextMenus(String tableType) {
	// StringBuilder responseHtml = new StringBuilder();
	// responseHtml.append("<ul id=\"categoryMenu\" class=\"contextMenu\">");
	// responseHtml.append("<li class=\"add\"><a href=\"#add\">Add " + tableType
	// + "</a></li>");
	// responseHtml.append("<li class=\"import\"><a href=\"#import\">Import " +
	// tableType + "</a></li>");
	// responseHtml.append("<li class=\"add separator\"><a href=\"#add-category\">Add subcategory</a></li>");
	// responseHtml.append("<li class=\"edit\"><a href=\"#edit\">Edit category</a></li>");
	// responseHtml.append("<li class=\"delete\"><a href=\"#delete\">Delete category</a></li>");
	// responseHtml.append("</ul>");
	//
	// if (!"enumeration".equals(tableType)) {
	// responseHtml.append("<ul id=\"keyMenu\" class=\"contextMenu\">");
	// responseHtml.append("<li class=\"add\"><a href=\"#add\">Insert column</a></li>");
	// responseHtml.append("<li class=\"copy\"><a href=\"#copy\">Duplicate column</a></li>");
	// responseHtml.append("<li class=\"fill\"><a href=\"#fill\">Fill column with...</a></li>");
	// responseHtml.append("<li class=\"parameter\"><a href=\"#parameter\">Change parameter type</a></li>");
	// responseHtml.append("<li class=\"delete\"><a href=\"#delete\">Delete column</a></li>");
	// responseHtml.append("</ul>");
	// }
	//
	// responseHtml.append("<ul id=\"rowMenu\" class=\"contextMenu\">");
	// responseHtml.append("<li class=\"add\"><a href=\"#add\">Insert row</a></li>");
	// responseHtml.append("<li class=\"copy\"><a href=\"#copy\">Duplicate row</a></li>");
	// responseHtml.append("<li class=\"delete\"><a href=\"#delete\">Delete row</a></li>");
	// responseHtml.append("</ul>");
	// return responseHtml.toString();
	// }

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
		responseHtml.append("<div id=\"table-container\">");
		responseHtml.append("</div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");

		responseHtml.append("</div>");
		responseHtml.append("</div>");
		return responseHtml.toString();
	}

	public static String getBreadCrumb(String sectionKey, Product product, String pathToImg) {
		StringBuilder responseHtml = new StringBuilder();
		String productName = product.getName();
		String sectionName = Sections.getNameByKey(sectionKey);

		responseHtml.append("<div id=\"breadcrumb\" class=\"header-text\">");
		responseHtml.append("<span id=\"home-image\"><img src=\"");
		responseHtml.append(pathToImg);
		responseHtml.append("/grible_logo_mini.png\"></span>");
		responseHtml.append("<a class=\"confirm-needed\" ");
		responseHtml.append("href=\"/\"><span id=\"home\" class=\"link-infront\">Home</span></a>");
		responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a class=\"confirm-needed\" href=\"/?product=");
		responseHtml.append(product.getId()).append("\">");
		responseHtml.append("<span id=\"product-name\" class=\"link-infront\">");
		responseHtml.append(productName).append("</span></a>");
		responseHtml.append("<span class=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a class=\"confirm-needed\" href=\"/").append(sectionKey);
		responseHtml.append("/?product=").append(product.getId()).append("\">");
		responseHtml.append("<span id=\"section-name\">");
		responseHtml.append(sectionName).append("</span></a></div>");
		return responseHtml.toString();
	}

	public static String getIncludes() {
		StringBuilder responseHtml = new StringBuilder();
		responseHtml.append("<link rel=\"shortcut icon\" href=\"../img/favicon.ico\" >");
		responseHtml.append("<link href=\"../css/jquery.contextMenu.css\" rel=\"stylesheet\" type=\"text/css\" />");
		responseHtml
				.append("<link href=\"../css/jquery.handsontable.full.css\" rel=\"stylesheet\" type=\"text/css\" />");
		responseHtml.append("<link href=\"../css/style.css\" rel=\"stylesheet\" type=\"text/css\" />");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-1.11.0.min.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery-ui.min.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery.handsontable.full.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/jquery.contextMenu.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/jquery.noty.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/top.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/defaultVars.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/noty/default.js\"></script>");
		responseHtml.append("<script type=\"text/javascript\" src=\"../js/handlebars.js\"></script>");
		return responseHtml.toString();
	}

	public static String deleteTable(int tableId, int productId) throws Exception {
		Table currentTable = null;
		String parentId = null;
		if (isJson()) {
			JsonDao dao = new JsonDao();
			currentTable = dao.getTable(tableId, productId);
			if (currentTable.getType() == TableType.PRECONDITION || currentTable.getType() == TableType.POSTCONDITION) {
				parentId = String.valueOf(dao.getParentTableId(tableId, productId, currentTable.getType()));
			}
		} else {
			currentTable = new PostgresDao().getTable(tableId);
		}

		boolean isUsedByTables = false;
		String error = "";
		if ((currentTable.getType() == TableType.STORAGE) || (currentTable.getType() == TableType.ENUMERATION)) {
			List<Table> tablesUsingThisStorage = DataManager.getInstance().getDao()
					.getTablesUsingStorage(currentTable, productId);
			if (!tablesUsingThisStorage.isEmpty()) {
				isUsedByTables = true;
				error = "ERROR: " + StringUtils.capitalize(currentTable.getType().toString().toLowerCase()) + " '"
						+ currentTable.getName() + "' is used by:";
				for (Table table : tablesUsingThisStorage) {
					error += "<br>- " + table.getName() + " (" + table.getType().toString().toLowerCase() + ");";
				}
			}
		}
		if (isUsedByTables) {
			return error;
		}
		boolean deleted = DataManager.getInstance().getDao().deleteTable(currentTable, productId);
		if (deleted) {
			switch (currentTable.getType()) {
			case TABLE:
			case STORAGE:
				return "success";

			case PRECONDITION:
			case POSTCONDITION:
				if (isJson()) {
					return parentId;
				}
				return String.valueOf(currentTable.getParentId());

			default:
				return "success";
			}
		}

		return "ERROR: " + currentTable.getType().toString().toLowerCase()
				+ " was not deleted. See server logs for details.";
	}

	public static boolean isJson() throws Exception {
		return GlobalSettings.getInstance().getAppType() == AppTypes.JSON;
	}

	public static void showAdvancedImportDialog(HttpServletRequest request, StringBuilder responseHtml)
			throws Exception {
		if ((request.getSession(false) != null) && (request.getSession(false).getAttribute("importedTable") != null)) {
			Table currTable = (Table) request.getSession(false).getAttribute("importedTable");
			ExcelFile excelFile = (ExcelFile) request.getSession(false).getAttribute("importedFile");
			responseHtml.append("<script type=\"text/javascript\">");
			int rowsCount = 0;
			if (isJson()) {
				rowsCount = currTable.getTableJson().getValues().length;
			} else {
				rowsCount = new PostgresDao().getOldRows(currTable.getId()).size();
			}
			responseHtml.append("$(window).on('load', function() { showAdvancedImportDialog(" + rowsCount + ", "
					+ excelFile.getValues().length + "); });");
			responseHtml.append("</script>");
		}
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("win"));
	}

}
