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
package org.grible.dao;

import org.grible.settings.GlobalSettings;

public class DataManager {
	private static DataManager dataManager;
	private Dao dao;

	private DataManager() throws Exception {
		switch (GlobalSettings.getInstance().getAppType()) {
		case PostgreSQL:
			dao = new PostgresDao();
			break;

		case Json:
			dao = new JsonDao();
			break;

		default:
			break;
		}
	}

	public static DataManager getInstance() throws Exception {
		if (dataManager == null) {
			dataManager = new DataManager();
		}
		return dataManager;
	}

	public Dao getDao() {
		return dao;
	}

}
