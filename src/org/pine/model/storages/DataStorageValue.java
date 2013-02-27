package org.pine.model.storages;


public class DataStorageValue {
	private int id;
	private int keyId;
	private int rowId;
	private String value;
	private boolean isStorage;
	private Integer[] storageIds;

	public DataStorageValue(int id) {
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
