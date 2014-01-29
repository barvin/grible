package org.grible.model.json;

public class TableJson {
	private String className;
	private boolean showUsage;
	private boolean showWarning;
	private KeyJson[] keys;
	private String[][] values;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isShowUsage() {
		return showUsage;
	}

	public void setShowUsage(boolean showUsage) {
		this.showUsage = showUsage;
	}

	public boolean isShowWarning() {
		return showWarning;
	}

	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}

	public KeyJson[] getKeys() {
		return keys;
	}

	public void setKeys(KeyJson[] keys) {
		this.keys = keys;
	}

	public String[][] getValues() {
		return values;
	}

	public void setValues(String[][] values) {
		this.values = values;
	}

}
