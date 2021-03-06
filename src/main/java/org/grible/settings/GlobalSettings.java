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
package org.grible.settings;

import java.io.File;

import org.grible.json.ConfigJson;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

public class GlobalSettings {
	private static GlobalSettings settings;
	private File configFile;
	private AppTypes appType;
	private String dbhost;
	private String dbport;
	private String dbname;
	private String dblogin;
	private String dbpswd;
	private String localRootPath;
	private String configFilePath;
	private String configJsonFilePath;
	private ConfigJson configJson;

	private GlobalSettings() throws Exception {
	}

	public boolean init(String localRootPath) throws Exception {
		this.localRootPath = localRootPath;
		if (this.configFile == null) {
			this.configFilePath = localRootPath + File.separator + ".." + File.separator + "config" + File.separator + "config.xml";
			this.configJsonFilePath = localRootPath + File.separator + ".." + File.separator + "config" + File.separator + "config.json";
			this.configFile = new File(configFilePath);
		}
		if ((!configFile.exists())) {
			return false;
		}
		setAppTypeFromFile();
		if ((appType == AppTypes.POSTGRESQL) && hasNulls()) {
			setDbSettingsFromFile();
		}
		return true;
	}

	private void setDbSettingsFromFile() throws Exception {
		Builder parser = new Builder();
		Document doc = null;
		doc = parser.build(configFile);
		if (doc != null) {
			Element database = doc.getRootElement().getFirstChildElement("database");
			this.dbhost = database.getFirstChildElement("dbhost").getValue();
			this.dbport = database.getFirstChildElement("dbport").getValue();
			this.dbname = database.getFirstChildElement("dbname").getValue();
			this.dblogin = database.getFirstChildElement("dblogin").getValue();
			this.dbpswd = database.getFirstChildElement("dbpswd").getValue();
		}
	}

	private void setAppTypeFromFile() throws Exception {
		Builder parser = new Builder();
		Document doc = null;
		doc = parser.build(configFile);
		if (doc != null) {
			Element apptype = doc.getRootElement().getFirstChildElement("apptype");
			this.appType = AppTypes.valueOf(apptype.getValue());
		}
	}

	public static GlobalSettings getInstance() throws Exception {
		if (settings == null) {
			settings = new GlobalSettings();
		}
		return settings;
	}

	public String getDbHost() throws Exception {
		return dbhost;
	}

	public void setDbHost(String dbhost) {
		this.dbhost = dbhost;
	}

	public String getDbPort() throws Exception {
		return dbport;
	}

	public void setDbPort(String dbport) {
		this.dbport = dbport;
	}

	public String getDbName() throws Exception {
		return dbname;
	}

	public void setDbName(String dbname) {
		this.dbname = dbname;
	}

	public String getDbLogin() throws Exception {
		return dblogin;
	}

	public void setDbLogin(String dblogin) {
		this.dblogin = dblogin;
	}

	public String getDbPswd() throws Exception {
		return dbpswd;
	}

	public void setDbPswd(String dbpswd) {
		this.dbpswd = dbpswd;
	}

	public AppTypes getAppType() {
		return appType;
	}

	public void setAppType(AppTypes appType) {
		this.appType = appType;
	}

	public String getLocalRootPath() {
		return localRootPath;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public String getConfigJsonFilePath() {
		return configJsonFilePath;
	}

	public ConfigJson getConfigJson() throws Exception {
		if (configJson == null) {
			configJson = new ConfigJson().read();
		}
		return configJson;
	}

	public void setConfigJson(ConfigJson json) {
		this.configJson = json;
	}

	private boolean hasNulls() {
		if ((dbhost == null) || (dbport == null) || (dbname == null) || (dblogin == null) || (dbpswd == null)) {
			return true;
		}
		return false;
	}

	/**
	 * Makes DB settings null, so that system would launch "firstlaunch" page. Use this method for exceptions during
	 * creating database.
	 */
	public void eraseDbSettings() {
		setDbHost(null);
		setDbPort(null);
		setDbName(null);
		setDbLogin(null);
		setDbPswd(null);
	}

}
