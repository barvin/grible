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

public class Row {
	private int id;
	private int tableId;
	private int order;

	public Row(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	public int getTableId() {
		return tableId;
	}

	public void setTableId(int storageId) {
		this.tableId = storageId;
	}
}
