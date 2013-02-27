package org.pine.model.files;

public class DataFileKey {
	private int id;
	private int fileId;
	private String name;
	private int order;
	private int referenceStorageId;

	public DataFileKey(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReferenceStorageId(int referenceStorageId) {
		this.referenceStorageId = referenceStorageId;
	}

	public int getReferenceStorageId() {
		return referenceStorageId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getReferenceStorageIdAsString() {
		return (referenceStorageId == 0) ? "NULL" : String.valueOf(referenceStorageId);
	}

}
