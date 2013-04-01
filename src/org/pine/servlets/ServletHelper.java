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

import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.pine.model.Product;
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

		responseHtml.append("<div class=\"table-row row-above-table\">");
		responseHtml.append("<div class=\"table-cell top-left-cell\">");
		responseHtml.append("<div class=\"icon-button button-enabled\"");
		responseHtml.append("id=\"btn-add-category\"><img src=\"../img/add-icon.png\">");
		responseHtml.append("<span class=\"icon-button-text\"> Add category</span></div>");
		responseHtml.append("</div>");
		responseHtml.append("<div class=\"table-cell\">");
		responseHtml.append("<div class=\"top-panel\"></div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");

		responseHtml.append("<div class=\"table-row categories-and-table-row\">");
		responseHtml.append("<div class=\"table-cell left-panel\">");
		responseHtml.append("<div id=\"entities-list\">");
		responseHtml.append("<div id=\"category-container\"></div>");
		responseHtml.append("</div>");
		responseHtml.append("</div>");
		responseHtml.append("<div id=\"waiting\" class=\"table-cell\">");
		responseHtml.append("<img src=\"../img/ajax-loader.gif\" class=\"waiting-gif\" />");
		responseHtml.append("<div id=\"table-container\">");
		responseHtml.append("<div class=\"table entities-values\"></div>");
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

		responseHtml.append("<div id=\"breadcrump\">");
		responseHtml.append("<a href=\"/pine\"><span id=\"home\" class=\"header-text\">Home</span></a>");
		responseHtml.append("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a href=\"/pine/?product=").append(product.getId()).append("\">");
		responseHtml.append("<span id=\"product-name\" class=\"header-text\">");
		responseHtml.append(productName).append("</span></a>");
		responseHtml.append("<span id=\"extends-symbol\">&nbsp;&gt;&nbsp;</span>");
		responseHtml.append("<a href=\"/pine/").append(sectionKey);
		responseHtml.append("/?product=").append(product.getId()).append("\">");
		responseHtml.append("<span id=\"section-name\" class=\"header-text\">");
		responseHtml.append(sectionName).append("</span></a></div>");
		return responseHtml.toString();
	}

}
