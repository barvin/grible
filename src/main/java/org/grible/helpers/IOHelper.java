package org.grible.helpers;

import java.io.File;
import java.util.List;

public class IOHelper {

	public static File searchFile(File dir, String fileName) {
		for (File temp : dir.listFiles()) {
			if (temp.isDirectory()) {
				File result = searchFile(temp, fileName);
				if (result != null) {
					return result;
				}
			} else if (fileName.equalsIgnoreCase(temp.getName())) {
				return temp;
			}
		}
		return null;
	}
	
	public static void searchAllFiles(List<File> fileList, File dir, String fileExtention) {
		for (File temp : dir.listFiles()) {
			if (temp.isDirectory()) {
				searchAllFiles(fileList, temp, fileExtention);
			} else if (temp.getName().endsWith(fileExtention)) {
				fileList.add(temp);
			}
		}
	}
}
