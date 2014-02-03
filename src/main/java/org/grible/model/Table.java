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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.grible.model.json.TableJson;

import com.google.gson.Gson;

public class Table {
	private int id;
	private TableType type;
	private Integer categoryId;
	private File file;
	private TableJson tableJson;
	private Integer parentId;
	private String name;
	private String className;
	private boolean showUsage;
	private boolean showWarning;
	private Date modifiedTime;

	public Table(int id) {
		this.id = id;
		setCategoryId(null);
		setParentId(null);
	}

	public Table(File file) throws Exception {
		setFile(file);
		setName(StringUtils.substringBefore(file.getName(), ".json"));
		this.tableJson = new TableJson();
	}

	public TableJson getTableJson() {
		return tableJson;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isShowUsage() {
		return showUsage;
	}

	public void setShowUsage(boolean showUsage) {
		this.showUsage = showUsage;
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

	public boolean isShowWarning() {
		return showWarning;
	}

	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date dateTime) {
		this.modifiedTime = dateTime;
	}

	public void save() throws Exception {
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(new Gson().toJson(tableJson));
		bw.close();
	}

	public void setTableJson() throws Exception {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		TableJson tableJson = new Gson().fromJson(br, TableJson.class);
		br.close();
		setType(tableJson.getType());
		setClassName(tableJson.getClassName());
		setShowUsage(tableJson.isShowUsage());
		setShowWarning(tableJson.isShowWarning());
		this.tableJson = tableJson;
	}
}
