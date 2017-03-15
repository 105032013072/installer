package com.bosssoft.platform.installer.jee.server.internal.util;

import java.io.File;

public class Util {
	public static File getTempDir() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpdir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}