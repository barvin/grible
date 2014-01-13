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
package org.grible.uimodel;

public class Section {
	private String key;
	private String name;
	private String description;
	private String dirName;
	
	public Section(String key, String name, String description, String dirName) {
		this.key = key;
		this.name = name;
		this.description = description;
		this.dirName = dirName;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDirName() {
		return dirName;
	}
}
