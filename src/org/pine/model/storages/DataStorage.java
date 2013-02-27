package org.pine.model.storages;

public class DataStorage {
	private int id;
	private int categoryId;
	private String name;
	private String className;
	private boolean showUsage;

	public DataStorage(int id) {
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

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isShowUsage() {
		return showUsage;
	}

	public void setShowUsage(boolean showUsage) {
		this.showUsage = showUsage;
	}
}
