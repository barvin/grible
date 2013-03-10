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

import org.pine.excel.TempVars;

public class GlobalSettings {
	private static GlobalSettings settings;
	private String dbhost;
	private String dbport;
	private String dbname;
	private String dblogin;
	private String dbpswd;

	private GlobalSettings() {
		ConfigParser configParser = new ConfigParser(TempVars.getLocalRootPath());
		setDbHost(configParser.getDbhost());
		setDbPort(configParser.getDbport());
		setDbName(configParser.getDbname());
		setDbLogin(configParser.getDblogin());
		setDbPswd(configParser.getDbpswd());
	}

	public static GlobalSettings getInstance() {
		if (settings == null) {
			settings = new GlobalSettings();
		}
		return settings;
	}

	public String getDbHost() {
		return dbhost;
	}

	public void setDbHost(String dbhost) {
		this.dbhost = dbhost;
	}

	public String getDbPort() {
		return dbport;
	}

	public void setDbPort(String dbport) {
		this.dbport = dbport;
	}

	public String getDbName() {
		return dbname;
	}

	public void setDbName(String dbname) {
		this.dbname = dbname;
	}

	public String getDbLogin() {
		return dblogin;
	}

	public void setDbLogin(String dblogin) {
		this.dblogin = dblogin;
	}

	public String getDbPswd() {
		return dbpswd;
	}

	public void setDbPswd(String dbpswd) {
		this.dbpswd = dbpswd;
	}

	public boolean hasNulls() {
		if ((dbhost == null) || (dbport == null) || (dbname == null) || (dblogin == null) || (dbpswd == null)) {
			return true;
		}
		return false;
	}

	/**
	 * Makes DB settings null, so that system would launch "firstlaunch" page.
	 * Use this method for exceptions during creating database.
	 */
	public void eraseDbSettings() {
		setDbHost(null);
		setDbPort(null);
		setDbName(null);
		setDbLogin(null);
		setDbPswd(null);
	}
}
