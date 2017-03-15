package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;
import java.io.IOException;

import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class DiskCheckOperation extends AbstractOperation {
	private File file;
	private long freeSpace;
	private boolean hasExcuted = false;
	private static Object LOCK = new Object();

	public DiskCheckOperation(File file) {
		this.file = file;
	}

	protected void checkArgument() {
		if (this.file == null)
			throw new IllegalArgumentException("The file must not be null!");
	}

	protected void doExcute() throws OperationException {
		if (!this.hasExcuted)
			synchronized (LOCK) {
				if (!this.hasExcuted) {
					this.hasExcuted = true;
					try {
						this.freeSpace = DiskUtil.freeSpaceKb(this.file.getAbsolutePath());
					} catch (IOException e) {
						throw new OperationException(e);
					}
				}
			}
	}

	public long getFreeSpace() throws OperationException {
		if (!this.hasExcuted) {
			excute();
		}
		return this.freeSpace;
	}
}