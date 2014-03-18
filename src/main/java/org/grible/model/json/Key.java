package org.grible.model.json;

public class Key {
	private String name;
	private String type;
	private int refid;
	
	public Key(String name, KeyType type, int refid) {
		setName(name);
		setType(type);
		setRefid(refid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public KeyType getType() {
		return KeyType.valueOf(type.toUpperCase());
	}

	public void setType(KeyType type) {
		this.type = type.toString().toLowerCase();
	}

	public int getRefid() {
		return refid;
	}

	public void setRefid(int refid) {
		this.refid = refid;
	}
}
