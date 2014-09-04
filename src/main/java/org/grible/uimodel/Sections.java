/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.uimodel;

import java.util.ArrayList;
import java.util.List;

public class Sections {
	private static ArrayList<Section> sections;

	public static ArrayList<Section> getSections() {
		if (sections == null) {
			sections = new ArrayList<Section>();
			sections.add(new Section("tables", "testtables", "testtablesdescr", "TestTables"));
			sections.add(new Section("storages", "datastorages", "datastoragesdescr", "DataStorages"));
			sections.add(new Section("enumerations", "enums", "enumsdescr", "Enumerations"));
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
