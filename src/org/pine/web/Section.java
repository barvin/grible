package org.pine.web;

public class Section {
	private String key;
	private String name;
	private String description;
	
	public Section(String key, String name, String description) {
		this.key = key;
		this.name = name;
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
