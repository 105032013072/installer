package com.bosssoft.platform.installer.io.operation.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.bosssoft.platform.installer.io.listener.DefaultFileOperationEvent;
import com.bosssoft.platform.installer.io.listener.IFileOperationEvent;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class ZipOperation extends PackOperation {
	public ZipOperation(File src, File dest) {
		super(src, dest);
	}

	protected void doExcute() throws OperationException {
		File src = getSrc();
		File dest = innerGetDest();

		ZipOutputStream outputStream = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(dest);
			outputStream = new ZipOutputStream(fileOutputStream);

			if (src.isDirectory())
				zip(src, "/", outputStream);
			else
				zip(src, src.getName(), outputStream);
		} catch (IOException e) {
			throw new OperationException(e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	private void zip(File src, String path, ZipOutputStream outputStream) throws IOException {
		if (!noNullFilter().accept(src)) {
			return;
		}
		checkCanceled();
		IFileOperationEvent event = new DefaultFileOperationEvent(src.getAbsolutePath(), path, "pack");
		fireOperationStarted(event);

		checkCanceled();

	
		
		ZipEntry zipEntry = new ZipEntry(path);
		
		if(!"/".equals(path))  outputStream.putNextEntry(zipEntry);
		if (src.isDirectory()) {
			outputStream.closeEntry();
			File[] children = src.listFiles();
			for (int i = 0; i < children.length; i++) {
				File file = children[i];
				String childpath = path + file.getName();
				if ("/".equals(path)) {
					childpath = file.getName();
				}

				childpath = file.isDirectory() ? childpath + "/" : childpath;
				zip(file, childpath, outputStream);
			}
		} else {
			checkCanceled();
			InputStream inputStream = new BufferedInputStream(new FileInputStream(src));
			try {
				IOUtils.copy(inputStream, outputStream);
				if (isPreserveFileDate())
					zipEntry.setTime(src.lastModified());
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
			outputStream.closeEntry();
		}
		event = new DefaultFileOperationEvent(src.getAbsolutePath(), path, "pack");
		fireOperationFinished(event);
	}

	private File innerGetDest() throws OperationException {
		File src = getSrc();

		File dest = getDest();

		if ((dest.exists()) && (dest.isDirectory())) {
			dest = new File(dest, src.getName());
		}
		if (!dest.exists()) {
			try {
				dest.createNewFile();
			} catch (IOException e) {
				throw new OperationException(e);
			}
		}
		return dest;
	}
}