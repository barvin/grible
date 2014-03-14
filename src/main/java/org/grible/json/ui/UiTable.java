package org.grible.json.ui;

import org.grible.model.json.KeyJson;

public class UiTable {
	private boolean isIndex;
	private KeyJson[] keys;
	private String[][] values;
	private UiRowsUsage info;
	private UiColumn[] columns;
	private int[] storageIds;
	private String[] storages;
	private int[] enumerationIds;
	private String[] enumerations;

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public KeyJson[] getKeys() {
		return keys;
	}

	public void setKeys(KeyJson[] keys) {
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

}
