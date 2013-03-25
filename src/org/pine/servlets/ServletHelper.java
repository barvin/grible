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
import org.pine.model.User;

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
		StringBuilder builder = new StringBuilder();
		builder.append("<ul id=\"categoryMenu\" class=\"contextMenu\">");
		builder.append("<li class=\"add\"><a href=\"#add\">Add data " + tableType + "</a></li>");
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
