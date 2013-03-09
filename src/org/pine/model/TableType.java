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
package org.pine.model;

public enum TableType {
	STORAGE(1), TABLE(2), PRECONDITION(3), POSTCONDITION(4);

	private int id;

	private TableType(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}
