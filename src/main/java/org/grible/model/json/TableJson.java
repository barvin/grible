package org.grible.model.json;

import java.util.ArrayList;
import java.util.List;

import org.grible.json.ui.UiColumn;
import org.grible.model.TableType;

public class TableJson {
	private TableType type;
	private String className;
	private boolean showUsage;
	private boolean showWarning;
	private KeyJson[] keys;
	private String[][] values;
	private UiColumn[] columns;

	public TableType getType() {
		return type;
	}

	public void setType(TableType type) {
		this.type = type;
	}

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

	public void setKeys(List<String> keyNames) {
		this.keys = new KeyJson[keyNames.size()];
		for (int i = 0; i < keys.length; i++) {
			keys[i] = new KeyJson(keyNames.get(i), KeyType.TEXT, 0);
		}
	}

	public void setValues(ArrayList<ArrayList<String>> valuesList) {
		this.values = new String[valuesList.size()][valuesList.get(0).size()];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				values[i][j] = valuesList.get(i).get(j);
			}
		}
	}

	public UiColumn[] getColumns() {
		return columns;
	}

	public void setColumns(UiColumn[] columns) {
		this.columns = columns;
	}

}
