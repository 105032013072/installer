package com.bosssoft.platform.installer.core.util;

import java.io.InputStream;

import com.bosssoft.platform.installer.core.InstallException;

public class FileFindUtil {
	public static InputStream getFileInputStream(String resource) throws InstallException {
		InputStream stream = FileFindUtil.class.getResourceAsStream(resource);

		if (stream == null) {
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
		}
		if (stream == null) {
			throw new InstallException(resource + " not found");
		}
		return stream;
	}
}