/*******************************************************************************
 * Copyright (c) 2013 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	private int id;
	private String name;
	private String password;
	private boolean isAdmin;
	private List<UserPermission> permissions;
	private boolean isTooltipOnClick;

	public User(int id) {
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

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<UserPermission> getPermissions() {
		return permissions;
	}

	public String getProductsString() {
		if (permissions == null) {
			return "";
		}

		if (permissions.isEmpty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (UserPermission permission : permissions) {
			builder.append(permission.getProduct().getName()).append("; ");
		}
		builder.replace(builder.lastIndexOf("; "), builder.length(), "");
		return builder.toString();
	}

	public void setPermissions(List<UserPermission> permissions) {
		this.permissions = permissions;
	}

	public boolean hasAccessToProduct(int productId) {
		if (isAdmin) {
			return true;
		}
		List<Product> products = new ArrayList<Product>();
		for (UserPermission permission : permissions) {
			products.add(permission.getProduct());
		}

		for (Product product : products) {
			if (product.getId() == productId) {
				return true;
			}
		}
		return false;
	}

	public boolean isTooltipOnClick() {
		return isTooltipOnClick;
	}

	public void setTooltipOnClick(boolean isTooltipOnClick) {
		this.isTooltipOnClick = isTooltipOnClick;
	}
	
	
}
