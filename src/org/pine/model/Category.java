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
package org.pine.model;

public class Category implements Comparable<Category> {
	private int id;
	private TableType type;
	private String name;
	private int productId;
	private Integer parentId;

	public Category(int id) {
		this.id = id;
		setParentId(null);
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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public TableType getType() {
		return type;
	}

	public void setType(TableType type) {
		this.type = type;
	}

	@Override
	public int compareTo(Category compareCategory) {
		return this.name.compareTo(compareCategory.getName());
	}
	
}
