package org.grible.dbmigrate;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.grible.settings.GlobalSettings;

public class Migrations {
	private static ArrayList<Migration> migrations;

	public static ArrayList<Migration> getAllMigrations() {
		if (migrations == null) {
			migrations = new ArrayList<Migration>();
			migrations.add(new Migration(0, "0.8.x", "migration_0_8_x.sql"));
			migrations.add(new Migration(1, "0.9.0", "migration_0_9_0.sql"));
		}
		return migrations;
	}

	public static List<Migration> getMigrationsSinceVersion(String version) {
		List<Migration> result = new ArrayList<Migration>();
		boolean isVersionFound = false;
		for (Migration migration : getAllMigrations()) {
			if (isVersionFound) {
				result.add(migration);
			} else if (migration.getVersion().equals(version)) {
				isVersionFound = true;
			}
		}
		return result;
	}
	
	public static String getSQLQuery(HttpServletRequest request, String fileName) throws Exception {
		FileReader fr = new FileReader(new File(request.getServletContext().getRealPath("") + "/WEB-INF/sql/" + fileName));
		List<String> lines = IOUtils.readLines(fr);
		StringBuilder content = new StringBuilder();

		for (String line : lines) {
			content.append(line.replace("postgres", GlobalSettings.getInstance().getDbLogin()));
			content.append("\n");
		}
		return content.toString();
	}
}
