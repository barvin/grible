package org.pine.model.storages;

public class DataStorageRow {
	private int id;
	private int storageId;
	private int order;

	public DataStorageRow(int id) {
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

	public int getStorageId() {
		return storageId;
	}

	public void setStorageId(int storageId) {
		this.storageId = storageId;
	}

}
