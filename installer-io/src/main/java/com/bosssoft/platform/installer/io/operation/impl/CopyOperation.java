package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

import com.bosssoft.platform.installer.io.listener.DefaultFileOperationEvent;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CopyOperation extends AbstractFileOperation {
	public CopyOperation(File src, File dest) {
		super(src, dest);
	}

	protected void doExcute() throws OperationException {
		File src = getSrc();
		File dest = getDest();
		FileFilter fileFilter = noNullFilter();
		boolean preserveDate = isPreserveFileDate();

		if (src.isDirectory()) {
			if ((dest.exists()) && (!dest.isDirectory())) {
				throw new IllegalArgumentException("Could not copy directory to file!source:\"" + src.getAbsolutePath() + "\"destination:" + dest.getAbsolutePath());
			}

			copyDirectoryToDirectory(src, dest, fileFilter);
		} else {
			try {
				doCopyFile(src, dest, preserveDate, fileFilter);
			} catch (IOException e) {
				throw new OperationException(e);
			}
		}
	}

	private void copyDirectoryToDirectory(File srcDir, File destDir, FileFilter fileFilter) throws OperationException {
		checkCanceled();
		if (!fileFilter.accept(srcDir)) {
			return;
		}
		DefaultFileOperationEvent event = new DefaultFileOperationEvent(srcDir.getAbsolutePath(), destDir.getAbsolutePath(), "copy");
		fireOperationStarted(event);
		checkCanceled();
		boolean preserveFileDate = isPreserveFileDate();
		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				throw new OperationException(new IOException("Destination '" + destDir + "' directory cannot be created"));
			}
			if (preserveFileDate) {
				destDir.setLastModified(srcDir.lastModified());
			}
		}
		if (!destDir.canWrite()) {
			throw new OperationException(new IOException("Destination '" + destDir + "' cannot be written to"));
		}

		File[] files = srcDir.listFiles();
		if (files == null) {
			throw new OperationException(new IOException("Failed to list contents of " + srcDir));
		}
		for (int i = 0; i < files.length; i++) {
			File copiedFile = new File(destDir, files[i].getName());
			try {
				if (files[i].isDirectory())
					doCopyDirectory(files[i], copiedFile, preserveFileDate, fileFilter);
				else
					doCopyFile(files[i], copiedFile, preserveFileDate, fileFilter);
			} catch (IOException e) {
				throw new OperationException(e);
			}
		}
		event = new DefaultFileOperationEvent(srcDir.getAbsolutePath(), destDir.getAbsolutePath(), "copy");
		fireOperationFinished(event);
	}

	private void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate, FileFilter fileFilter) throws IOException {
		checkCanceled();
		if (!fileFilter.accept(srcDir)) {
			return;
		}
		DefaultFileOperationEvent event = new DefaultFileOperationEvent(srcDir.getAbsolutePath(), destDir.getAbsolutePath(), "copy");
		fireOperationStarted(event);
		checkCanceled();
		if (destDir.exists()) {
			if (!destDir.isDirectory())
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
		} else {
			destDir.mkdirs();

			if (preserveFileDate) {
				destDir.setLastModified(srcDir.lastModified());
			}
		}
		if (!destDir.canWrite()) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}

		File[] files = srcDir.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of " + srcDir);
		}
		for (int i = 0; i < files.length; i++) {
			File copiedFile = new File(destDir, files[i].getName());
			if (files[i].isDirectory())
				doCopyDirectory(files[i], copiedFile, preserveFileDate, fileFilter);
			else {
				doCopyFile(files[i], copiedFile, preserveFileDate, noNullFilter());
			}
		}
		event = new DefaultFileOperationEvent(srcDir.getAbsolutePath(), destDir.getAbsolutePath(), "copy");
		fireOperationFinished(event);
	}

	private void doCopyFile(File srcFile, File destFile, boolean preserveFileDate, FileFilter fileFilter) throws IOException {
		checkCanceled();
		if (!fileFilter.accept(srcFile)) {
			return;
		}
		DefaultFileOperationEvent event = new DefaultFileOperationEvent(srcFile.getAbsolutePath(), destFile.getAbsolutePath(), "copy");
		fireOperationStarted(event);
		checkCanceled();
		if ((destFile.exists()) && (destFile.isDirectory())) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}

		if (!destFile.exists()) {
			destFile.getParentFile().mkdirs();
		}

		FileInputStream input = new FileInputStream(srcFile);
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(destFile);
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
		}
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
		event = new DefaultFileOperationEvent(srcFile.getAbsolutePath(), destFile.getAbsolutePath(), "copy");
		fireOperationFinished(event);
	}
}