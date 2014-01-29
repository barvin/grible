package org.grible.helpers;

import java.io.File;

public class IOHelper {

	public static File searchForFile(File dir, String fileName) {
		for (File temp : dir.listFiles()) {
			if (temp.isDirectory()) {
				searchForFile(temp, fileName);
			} else if (fileName.equalsIgnoreCase(temp.getName())) {
				return temp;
			}
		}
		return null;
	}
}
