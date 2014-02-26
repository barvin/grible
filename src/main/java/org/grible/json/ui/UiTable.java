package org.grible.json.ui;

import org.grible.model.json.KeyJson;

public class UiTable {
	private boolean isIndex;
	private KeyJson[] keys;
	private String[][] values;
	private UiInfo info;
	private UiColumn[] columns;

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

	public UiInfo getInfo() {
		return info;
	}

	public void setInfo(UiInfo info) {
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

}
