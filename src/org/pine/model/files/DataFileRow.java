package org.pine.model.files;

public class DataFileRow {
	private int id;
	private int fileId;
	private int order;

	public DataFileRow(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
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

}
