package org.pine.model.storages;

public class DataStorageKey {
	private int id;
	private int storageId;
	private String name;
	private int order;
	private int referenceStorageId;

	public DataStorageKey(int id) {
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

	public int getStorageId() {
		return storageId;
	}

	public void setStorageId(int storageId) {
		this.storageId = storageId;
	}
}
