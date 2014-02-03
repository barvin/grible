package org.grible.json.ui;

public class UiTable {
	private boolean isIndex;
	private UiKey[] keys;
	private UiInfo info;
	private UiRow[] values;

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public UiKey[] getKeys() {
		return keys;
	}

	public void setKeys(UiKey[] keys) {
		this.keys = keys;
	}

	public UiInfo getInfo() {
		return info;
	}

	public void setInfo(UiInfo info) {
		this.info = info;
	}

	public UiRow[] getValues() {
		return values;
	}

	public void setValues(UiRow[] values) {
		this.values = values;
	}

}
