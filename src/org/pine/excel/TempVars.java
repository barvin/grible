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
package org.pine.excel;

public class TempVars {
	private static String dataStorageImportResult;
	private static String dataFileImportResult;

	public static String getDataStorageImportResult() {
		String result = dataStorageImportResult == null ? "" : dataStorageImportResult;
		TempVars.dataStorageImportResult = "";
		return result;
	}

	public static void setDataStorageImportResult(String result) {
		TempVars.dataStorageImportResult = result;
	}

	public static String getDataFileImportResult() {
		String result = dataFileImportResult == null ? "" : dataFileImportResult;
		TempVars.dataFileImportResult = "";
		return result;
	}

	public static void setDataFileImportResult(String result) {
		TempVars.dataFileImportResult = result;
	}
}
