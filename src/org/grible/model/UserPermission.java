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

public class UserPermission {
	private int id;
	private int userId;
	private Product product;
	private boolean hasWriteAccess;

	public UserPermission(int id) {
		this.id = id;
		setHasWriteAccess(true);
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

	public boolean isHasWriteAccess() {
		return hasWriteAccess;
	}

	public void setHasWriteAccess(boolean hasWriteAccess) {
		this.hasWriteAccess = hasWriteAccess;
	}
}
