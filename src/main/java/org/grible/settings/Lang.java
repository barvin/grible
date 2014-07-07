package org.grible.settings;

public enum Lang {
	ENGLISH("en.json"), FRENCH("fr.json"), GERMAN("de.json"), SPANISH("es.json"), UKRAINIAN("uk.json");

	private String fileName;

	Lang(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public static String get(String key) {
		return "<script type=\"text/javascript\">document.write(lang." + key + ")</script>";
	}
}
