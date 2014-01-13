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
package org.grible.model;

import org.grible.uimodel.Section;
import org.grible.uimodel.Sections;

public enum TableType {
	STORAGE(1, Sections.getSections().get(1)), TABLE(2, Sections.getSections().get(0)), PRECONDITION(3, Sections
			.getSections().get(0)), POSTCONDITION(4, Sections.getSections().get(0)), ENUMERATION(5, Sections
			.getSections().get(2));

	private int id;
	private Section section;

	private TableType(int id, Section section) {
		this.id = id;
		this.section = section;
	}

	public int getId() {
		return this.id;
	}

	public Section getSection() {
		return this.section;
	}
}
