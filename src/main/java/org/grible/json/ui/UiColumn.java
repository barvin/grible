package org.grible.json.ui;

public class UiColumn {
	private String type;
	private boolean allowInvalid;
	private String[] source;
	private int refTableSelected;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAllowInvalid() {
		return allowInvalid;
	}

	public void setAllowInvalid(boolean allowInvalid) {
		this.allowInvalid = allowInvalid;
	}

	public String[] getSource() {
		return source;
	}

	public void setSource(String[] source) {
		this.source = source;
	}

	public int getRefTableSelected() {
		return refTableSelected;
	}

	public void setRefTableSelected(int refTableSelected) {
		this.refTableSelected = refTableSelected;
	}

}
