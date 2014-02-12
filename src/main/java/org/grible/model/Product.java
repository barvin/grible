/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.model;

import java.io.File;
import java.util.ArrayList;

import org.grible.json.GribleJson;
import org.grible.json.IdPathPair;

public class Product {
	private int id;
	private String name;
	private String path;
	private GribleJson gribleJson;

	public Product(int id) {
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		this.gribleJson = new GribleJson();
		gribleJson.setIdPathPairs(new ArrayList<IdPathPair>());
		gribleJson.setFilePath(path + File.separator + "grible.json");
	}

	public GribleJson getGribleJson() throws Exception {
		return gribleJson;
	}
}
