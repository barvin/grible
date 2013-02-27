package org.pine.web;

import java.util.ArrayList;
import java.util.List;

public class Sections {
	private static ArrayList<Section> sections;

	public static ArrayList<Section> getSections() {
		if (sections == null) {
			sections = new ArrayList<>();
			sections.add(new Section("tables", "Data Tables", "Tables of input test data for test cases. One test case = one data table. The number of the rows in the table defines the number of the test iterations."));
			sections.add(new Section("storages", "Data Storages", "Reusable sets of parameters. Normally, they represent entities from the application. Each storage can be used by data tables or other storages."));
			sections.add(new Section("import", "Import", "Import data tables and storages from Excel files."));
		}
		return sections;
	}
	
	public static String getNameByKey(String key) {
		List<Section> list = getSections();
		for (Section section : list) {
			if (section.getKey().equals(key)) {
				return section.getName(); 
			}
		}
		return null;
	}

}
