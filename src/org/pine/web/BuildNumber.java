package org.pine.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class BuildNumber {
	private static BuildNumber buildNumber;
	private String number;

	private BuildNumber() {
		File file = new File(
				"webapps/pine/WEB-INF/classes/build-number.txt");
		String content = getContents(file);
		number = content;
	}

	public static BuildNumber getInstrance() {
		if (buildNumber == null) {
			buildNumber = new BuildNumber();
		}
		return buildNumber;
	}

	public String getFromFile() {
		if (number != null) {
			if (!number.equals("")) {
				return "Build: 0.5." + number + ".";
			}
		}
		return "";
	}

	private static String getContents(File file) {
		String content = "";
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				content = IOUtils.toString(input);
			} finally {
				input.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			String realPath = "";
			try {
				realPath = new File(".").getCanonicalPath();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			new IOException("!!! Real path: '" + realPath + "'").printStackTrace();
		}

		return content;
	}
}
