package com.bosssoft.platform.installer.core.cfg;

public class Variable {
	private String key = null;
	private String value = null;

	public Variable(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return this.key;
	}
}