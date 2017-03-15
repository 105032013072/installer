package com.bosssoft.platform.installer.core.action;

public class Fileset {
	private String dir = null;
	private String includes = null;
	private String excludes = null;

	public String getDir() {
		return this.dir;
	}

	public void setDir(String d) {
		this.dir = d;
	}

	public String getIncludes() {
		return this.includes;
	}

	public void setIncludes(String i) {
		this.includes = i;
	}

	public String getExcludes() {
		return this.excludes;
	}

	public void setExcludes(String e) {
		this.excludes = e;
	}
}