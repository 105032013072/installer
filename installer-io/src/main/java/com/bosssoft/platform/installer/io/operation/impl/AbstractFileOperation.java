package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;
import java.io.FileFilter;

public abstract class AbstractFileOperation extends AbstractOperation {
	private File src;
	private File dest;
	private FileFilter filter;
	private boolean preserveFileDate = false;

	public AbstractFileOperation(File src, File dest) {
		this.src = src;
		this.dest = dest;
	}

	public File getDest() {
		return this.dest;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

	public File getSrc() {
		return this.src;
	}

	public void setSrc(File src) {
		this.src = src;
	}

	public FileFilter getFilter() {
		return this.filter;
	}

	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}

	public boolean isPreserveFileDate() {
		return this.preserveFileDate;
	}

	public void setPreserveFileDate(boolean preserveFileDate) {
		this.preserveFileDate = preserveFileDate;
	}

	protected FileFilter noNullFilter() {
		FileFilter fileFilter = getFilter();
		if (fileFilter == null) {
			return new NullFileFilter();
		}

		return fileFilter;
	}

	protected void checkArgument() {
		File src = getSrc();
		File dest = getDest();
		if (src == null) {
			throw new NullPointerException("The source file must not be null!");
		}
		if (!src.exists()) {
			throw new IllegalArgumentException("The source file must be exist!" + src.getAbsolutePath());
		}

		if (dest == null)
			throw new NullPointerException("The destination must not be null!");
	}
}