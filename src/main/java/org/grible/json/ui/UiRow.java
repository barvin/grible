package org.grible.json.ui;

public class UiRow {
	private UiIndex index;
	private UiValue[] values;
	private UiInfo info;

	public UiIndex getIndex() {
		return index;
	}

	public void setIndex(UiIndex index) {
		this.index = index;
	}

	public UiValue[] getValues() {
		return values;
	}

	public void setValues(UiValue[] values) {
		this.values = values;
	}

	public UiInfo getInfo() {
		return info;
	}

	public void setInfo(UiInfo info) {
		this.info = info;
	}

}
