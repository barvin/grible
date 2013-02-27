package org.pine.model.storages;

public class StorageCategory {
	private int id;
	private String name;
	private int productId;

	public StorageCategory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getProductId() {
		return productId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}
	
}
