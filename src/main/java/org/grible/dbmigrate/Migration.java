package org.grible.dbmigrate;

public class Migration {
	private int order;
	private String version;
	private String fileName;
	
	public Migration(int order, String version, String fileName) {
		setOrder(order);
		setVersion(version);
		setFileName(fileName);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
