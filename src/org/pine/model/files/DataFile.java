package org.pine.model.files;

public class DataFile {
	private int id;
	private String name;
	private int categoryId;
	private boolean hasPreconditions;
	private boolean hasPostconditions;

	public DataFile(int id) {
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

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public boolean isHasPreconditions() {
		return hasPreconditions;
	}

	public void setHasPreconditions(boolean hasPreconditions) {
		this.hasPreconditions = hasPreconditions;
	}

	public boolean isHasPostconditions() {
		return hasPostconditions;
	}

	public void setHasPostconditions(boolean hasPostconditions) {
		this.hasPostconditions = hasPostconditions;
	}
}
