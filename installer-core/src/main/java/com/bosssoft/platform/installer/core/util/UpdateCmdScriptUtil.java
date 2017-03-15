package com.bosssoft.platform.installer.core.util;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class UpdateCmdScriptUtil {
	static transient Logger logger = Logger.getLogger(UpdateCmdScriptUtil.class);

	public static boolean addExeMode(String dir) {
		String[] cmd = new String[3];
		Runtime r = Runtime.getRuntime();
		try {
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
			cmd[2] = ("chmod -R +x * " + dir);
			Process proc = r.exec(cmd);
			String result = ProcessOutputReader.read(proc);
			logger.info(result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return false;
	}

	public static void addExecutePermission(String[] filePaths) {
		String[] arrayOfString = filePaths;
		int j = filePaths.length;
		for (int i = 0; i < j; i++) {
			String dir = arrayOfString[i];
			addExeMode(dir);
		}
	}

	public static void replaceVariable(String[] filePaths, Properties props) {
		String os = System.getProperty("os.name").toLowerCase();
		String[] arrayOfString;
		int j;
		int i;
		if (os.indexOf("window") >= 0)
			try {
				arrayOfString = filePaths;
				j = filePaths.length;
				for (i = 0; i < j; i++) {
					String file = arrayOfString[i];
					StringConvertor.replaceFile(file, props);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		else
			try {
				arrayOfString = filePaths;
				j = filePaths.length;
				for (i = 0; i < j; i++) {
					String file = arrayOfString[i];
					StringConvertor.replaceUnixFile(file, props);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
	}
}