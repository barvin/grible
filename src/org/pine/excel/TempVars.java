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
	private static String storageImportResult;
	private static String tableImportResult;

	public static String getStorageImportResult() {
		String result = storageImportResult == null ? "" : storageImportResult;
		TempVars.storageImportResult = "";
		return result;
	}

	public static void setStorageImportResult(String result) {
		TempVars.storageImportResult = result;
	}

	public static String getTableImportResult() {
		String result = tableImportResult == null ? "" : tableImportResult;
		TempVars.tableImportResult = "";
		return result;
	}

	public static void setTableImportResult(String result) {
		TempVars.tableImportResult = result;
	}

}
