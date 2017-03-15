package com.bosssoft.platform.installer.core.util;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log4j.Logger;

public class ShortCutUtil {
	static Logger logger = Logger.getLogger(ShortCutUtil.class);

	private static String SHORTCUT_EXE = InstallerFileManager.getInstallerHome() + "/bin/" ;

	public static void createExeShortcut(String link, String exe, String ico, String workDir) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("window") < 0) {
			return;
		}

		String[] cmds = { SHORTCUT_EXE, "/f:" + link, "/a:c", "/t:" + exe, "/i:" + ico, "/w:" + workDir };
		try {
			Runtime.getRuntime().exec(cmds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createUrlShortcut(String file, String url, String icon) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("window") < 0) {
			return;
		}

		StringBuffer content = new StringBuffer();

		content.append("[InternetShortcut]\nUrl=" + url);

		content.append("\nIconIndex=0\nIconFile=" + icon);

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.toString().getBytes());
		} catch (IOException ex) {
			logger.error("Create shortcut fail", ex);
			try {
				fileOutputStream.close();
			} catch (Exception e) {
				logger.error(e, e);
			}
		} finally {
			try {
				fileOutputStream.close();
			} catch (Exception e) {
				logger.error(e, e);
			}
		}
	}
}