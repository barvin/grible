package org.pine.web;

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
