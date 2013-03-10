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
package org.pine.settings;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

public class ConfigParser {
	private String configPath;
	private String dbhost;
	private String dbport;
	private String dbname;
	private String dblogin;
	private String dbpswd;

	public ConfigParser(String pathToRoot) {
		this.configPath = pathToRoot + "/WEB-INF/config.xml";
		if (new File(configPath).exists()) {
			Builder parser = new Builder();
			Document doc = null;
			try {
				doc = parser.build(configPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (doc != null) {
				Element database = doc.getRootElement().getFirstChildElement("database");
				this.dbhost = database.getFirstChildElement("dbhost").getValue();
				this.dbport = database.getFirstChildElement("dbport").getValue();
				this.dbname = database.getFirstChildElement("dbname").getValue();
				this.dblogin = database.getFirstChildElement("dblogin").getValue();
				this.dbpswd = database.getFirstChildElement("dbpswd").getValue();
			}
		}
	}

	public String getDbhost() {
		return dbhost;
	}

	public String getDbport() {
		return dbport;
	}

	public String getDbname() {
		return dbname;
	}

	public String getDblogin() {
		return dblogin;
	}

	public String getDbpswd() {
		return dbpswd;
	}
}
