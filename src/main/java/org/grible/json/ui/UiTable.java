package org.grible.json.ui;

import org.grible.model.json.Key;

public class UiTable {
	private int[] rowHeaders;
	private Key[] keys;
	private String[][] values;
	private UiRowsUsage info;
	private UiColumn[] columns;
	private int[] storageIds;
	private String[] storages;
	private int[] enumerationIds;
	private String[] enumerations;
	private String time;

	public int[] getRowHeaders() {
		return rowHeaders;
	}

	public void setRowHeaders(int[] rowHeaders) {
		this.rowHeaders = rowHeaders;
	}

	public Key[] getKeys() {
		return keys;
	}

	public void setKeys(Key[] keys) {
		this.keys = keys;
	}

	public UiRowsUsage getInfo() {
		return info;
	}

	public void setInfo(UiRowsUsage info) {
		this.info = info;
	}

	public String[][] getValues() {
		return values;
	}

	public void setValues(String[][] values) {
		this.values = values;
	}

	public UiColumn[] getColumns() {
		return columns;
	}

	public void setColumns(UiColumn[] columns) {
		this.columns = columns;
	}

	public String[] getStorages() {
		return storages;
	}

	public void setStorages(String[] storages) {
		this.storages = storages;
	}

	public String[] getEnumerations() {
		return enumerations;
	}

	public void setEnumerations(String[] enumerations) {
		this.enumerations = enumerations;
	}

	public int[] getStorageIds() {
		return storageIds;
	}

	public void setStorageIds(int[] storageIds) {
		this.storageIds = storageIds;
	}

	public int[] getEnumerationIds() {
		return enumerationIds;
	}

	public void setEnumerationIds(int[] enumerationIds) {
		this.enumerationIds = enumerationIds;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
