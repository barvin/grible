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
package org.grible.model;


public class Value {
	private int id;
	private int keyId;
	private int rowId;
	private String value;
	private boolean isStorage;
	private Integer[] storageIds;

	public Value(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public boolean isStorage() {
		return isStorage;
	}

	public void setIsStorage(boolean isStorage) {
		this.isStorage = isStorage;
	}

	public Integer[] getStorageIds() {
		return storageIds;
	}

	public String getStorageIdsAsString() {
		if (storageIds==null) {
			return "null";
		}
		String result = "'{";
		for (int i = 0; i < storageIds.length; i++) {
			result += storageIds[i] + ", ";
		}
		result = result.substring(0, result.length() - 2) + "}'"; 
		return result;
	}

	public void setStorageIds(Integer[] storageIds) {
		this.storageIds = storageIds;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
