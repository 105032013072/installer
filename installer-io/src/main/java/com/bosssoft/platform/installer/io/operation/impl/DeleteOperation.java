package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;
import java.io.FileFilter;

import com.bosssoft.platform.installer.io.listener.DefaultFileOperationEvent;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class DeleteOperation extends AbstractOperation {
	private File deleteFile;
	private FileFilter fileFilter;

	public DeleteOperation(File deleteFile) {
		this.deleteFile = deleteFile;
	}

	protected void doExcute() throws OperationException {
		doDelete(this.deleteFile);
	}

	private void doDelete(File file) {
		if (!accep(file)) {
			return;
		}
		String path = file.getAbsolutePath();
		checkCanceled();

		DefaultFileOperationEvent event = new DefaultFileOperationEvent(path, path, "delete");
		fireOperationStarted(event);
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			checkCanceled();
			for (int i = 0; i < children.length; i++) {
				File file2 = children[i];
				doDelete(file2);
			}
		}
		file.delete();
		checkCanceled();
		fireOperationFinished(event);
	}

	private boolean accep(File file) {
		if (this.fileFilter == null) {
			return true;
		}
		return this.fileFilter.accept(file);
	}

	protected void checkArgument() {
		if (this.deleteFile == null)
			throw new IllegalArgumentException("The deleteFile must not be null!");
	}

	public File getDeleteFile() {
		return this.deleteFile;
	}

	public FileFilter getFileFilter() {
		return this.fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}
}