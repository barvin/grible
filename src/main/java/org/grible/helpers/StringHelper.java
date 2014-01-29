package org.grible.helpers;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class StringHelper {

	public static String getFolderPath(String pathWithSemicolon) {
		if (pathWithSemicolon == null) {
			return "";
		}
		String[] folders = pathWithSemicolon.split(";");
		ArrayUtils.reverse(folders);
		return StringUtils.join(folders, File.separator);
	}

	public static String getFolderPathWithoutLastSeparator(String pathWithSemicolon) {
		if (pathWithSemicolon == null) {
			return "";
		}
		String[] folders = StringUtils.substringAfter(pathWithSemicolon, ";").split(";");
		ArrayUtils.reverse(folders);
		return StringUtils.join(folders, File.separator);
	}
}
