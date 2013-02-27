package org.pine.model.files;


public class PostconditionValue {
	private int id;
	private int dataFileid;
	private String key;
	private String value;
	private boolean isStorage;
	private Integer[] storageIds;
	private int referenceStorageId;

	public PostconditionValue(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public int getDataFileid() {
		return dataFileid;
	}

	public void setDataFileid(int dataFileid) {
		this.dataFileid = dataFileid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getReferenceStorageId() {
		return referenceStorageId;
	}

	public void setReferenceStorageId(int referenceStorageId) {
		this.referenceStorageId = referenceStorageId;
	}

	public String getReferenceStorageIdAsString() {
		return (referenceStorageId == 0) ? "NULL" : String.valueOf(referenceStorageId);
	}
}
