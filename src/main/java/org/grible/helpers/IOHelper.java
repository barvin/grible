package org.grible.helpers;

import java.io.File;
import java.util.List;

public class IOHelper {

	public static File searchFile(File dir, String fileName) {
		for (File temp : dir.listFiles()) {
			if (temp.isDirectory()) {
				searchFile(temp, fileName);
			} else if (fileName.equalsIgnoreCase(temp.getName())) {
				return temp;
			}
		}
		return null;
	}
	
	public static void searchAllFiles(List<File> fileList, File dir, String fileNamePart) {
		for (File temp : dir.listFiles()) {
			if (temp.isDirectory()) {
				searchAllFiles(fileList, temp, fileNamePart);
			} else if (temp.getName().contains(fileNamePart)) {
				fileList.add(temp);
			}
		}
	}
}
