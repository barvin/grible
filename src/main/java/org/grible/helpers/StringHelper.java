package org.grible.helpers;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.grible.dao.DataManager;
import org.grible.model.Product;
import org.grible.model.Table;
import org.grible.model.TableType;

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

	public static String getCategoryPathFromTable(Table table, Integer productId, TableType type) throws Exception {
		Product product = DataManager.getInstance().getDao().getProduct(productId);
		String result = StringUtils.substringAfter(table.getFile().getAbsolutePath(), product.getPath()
				+ File.separator + type.getSection().getDirName() + File.separator);
		result = StringUtils.substringBeforeLast(result, File.separator);
		return result;
	}
	
	public static String removeForbiddenCharactersForFolder(String str) {
		return StringUtils.removePattern(str, "[<|>|:|\"|\\\\|/|\\||\\?|\\*]");
	}
}
