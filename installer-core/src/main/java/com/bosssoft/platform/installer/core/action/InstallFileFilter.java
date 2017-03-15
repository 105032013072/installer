package com.bosssoft.platform.installer.core.action;

import java.io.File;
import java.io.FileFilter;

public class InstallFileFilter implements FileFilter {
	private String includes = null;
	private String excludes = null;

	private IncludesFilter includesFilter = null;

	private ExcludesFilter excludesFilter = null;

	public void setIncludes(String i) {
		this.includes = i;
		if ((i != null) && (i.trim().length() > 0))
			this.includesFilter = new IncludesFilter(this.includes);
		else
			this.includesFilter = null;
	}

	public void setExcludes(String e) {
		this.excludes = e;
		if ((e != null) && (e.trim().length() > 0))
			this.excludesFilter = new ExcludesFilter(this.excludes);
		else
			this.excludesFilter = null;
	}

	public boolean accept(File file) {
		if ((this.includesFilter == null) && (this.excludesFilter == null)) {
			return true;
		}
		if (this.includesFilter != null) {
			return this.includesFilter.accept(file);
		}

		if (this.excludesFilter != null) {
			return !this.excludesFilter.accept(file);
		}
		return true;
	}

	protected boolean isMatch(File f, String pattern) {
		String fname = f.getName();

		pattern = pattern.replace('\\', '/');
		if (pattern.startsWith("**/")) {
			pattern = pattern.substring(3);
		}
		return isMatch(fname, pattern);
	}

	private boolean isMatch(String fileName, String pattern) {
		String cp = null;
		int index = pattern.indexOf('*');

		if (index < 0) {
			return fileName.equals(pattern);
		}
		if (index == 0) {
			pattern = pattern.substring(1);
			index = pattern.indexOf('*');
		}

		if (index < 0) {
			return fileName.endsWith(pattern);
		}
		cp = pattern.substring(0, index);
		pattern = pattern.substring(index);
		index = fileName.indexOf(cp);
		if (index < 0) {
			return false;
		}
		fileName = fileName.substring(index + cp.length());
		return isMatch(fileName, pattern);
	}

	public class ExcludesFilter {
		String[] patterns = null;

		public ExcludesFilter(String excludes) {
			this.patterns = excludes.split(",");
		}

		public boolean accept(File f) {
			for (String pattern : this.patterns) {
				if (InstallFileFilter.this.isMatch(f, pattern))
					return true;
			}
			return false;
		}
	}

	public class IncludesFilter {
		String[] patterns = null;

		public IncludesFilter(String includes) {
			this.patterns = includes.split(",");
		}

		public boolean accept(File f) {
			for (String pattern : this.patterns) {
				if (InstallFileFilter.this.isMatch(f, pattern))
					return true;
			}
			return false;
		}
	}
}