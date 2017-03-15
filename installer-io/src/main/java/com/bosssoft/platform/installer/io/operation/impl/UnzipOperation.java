package com.bosssoft.platform.installer.io.operation.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import com.bosssoft.platform.installer.io.listener.DefaultFileOperationEvent;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class UnzipOperation extends UnpackOperation {
	public UnzipOperation(File zipFile, File dest) {
		super(zipFile, dest);
	}

	protected void doExcute() throws OperationException {
		File src = getSrc();
		File dest = getDest();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(src);

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			FileFilter fileFilter = noNullFilter();
			while (enumeration.hasMoreElements()) {
				checkCanceled();

				ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
				String name = zipEntry.getName();
				File unpackDest = new File(dest, name);

				if (fileFilter.accept(unpackDest)) {
					DefaultFileOperationEvent event = new DefaultFileOperationEvent(name, unpackDest.getAbsolutePath(), "unpack");
					fireOperationStarted(event);

					checkCanceled();

					unzip(zipFile, zipEntry, unpackDest);

					event = new DefaultFileOperationEvent(name, unpackDest.getAbsolutePath(), "unpack");
					fireOperationFinished(event);
				}
			}
		} catch (IOException e) {
			throw new OperationException(e);
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void unzip(ZipFile zipFile, ZipEntry zipEntry, File unpackDest) throws IOException, FileNotFoundException {
		if (zipEntry.isDirectory()) {
			if (!unpackDest.exists())
				unpackDest.mkdirs();
		} else {
			if (!unpackDest.getParentFile().exists()) {
				unpackDest.getParentFile().mkdirs();
			}

			InputStream inputStream = zipFile.getInputStream(zipEntry);

			FileOutputStream outputStream = new FileOutputStream(unpackDest);
			BufferedOutputStream bufferOutput = new BufferedOutputStream(outputStream);

			if (inputStream != null) {
				byte[] buffer = new byte[4096];
				int n = 0;
				while (-1 != (n = inputStream.read(buffer))) {
					bufferOutput.write(buffer, 0, n);
				}
			}
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(bufferOutput);

			if (isPreserveFileDate())
				unpackDest.setLastModified(zipEntry.getTime());
		}
	}
}