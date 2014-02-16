package org.grible.json.ui;

public class UiTable {
	private boolean isIndex;
	private String[] keys;
	private String[][] values;
	private String storages;
	private UiInfo info;

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
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

	public String getStorages() {
		return storages;
	}

	public void setStorages(String storages) {
		this.storages = storages;
	}

}
