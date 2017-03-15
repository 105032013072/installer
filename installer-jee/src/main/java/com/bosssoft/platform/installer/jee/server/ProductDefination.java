package com.bosssoft.platform.installer.jee.server;

public class ProductDefination {
	private String name;
	private String version;

	public ProductDefination() {
	}

	public ProductDefination(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String toString() {
		return "ProductDefination{name:" + this.name + "; version:" + this.version + "}";
	}
}