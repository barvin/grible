package org.pine.model.users;

import org.pine.model.Product;

public class UserPermission {
	private int id;
	private int userId;
	private Product product;

	public UserPermission(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public Product getProduct() {
		return product;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setProducts(Product product) {
		this.product = product;
	}
	
}
