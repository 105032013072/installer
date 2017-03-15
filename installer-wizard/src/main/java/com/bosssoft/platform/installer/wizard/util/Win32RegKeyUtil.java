package com.bosssoft.platform.installer.wizard.util;

import org.apache.log4j.Logger;

import com.primeton.eos.install.util.jni.Win32RegKey;

public class Win32RegKeyUtil {
	static Logger logger = Logger.getLogger(Win32RegKeyUtil.class);

	public static String getStartMenuPath() {
		Win32RegKey key = new Win32RegKey(-2147483647, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders");

		key.names();

		Object value = key.getValue("Programs");
		try {
			return value.toString();
		} catch (Exception e) {
			logger.debug("Encoding Error!");
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(getStartMenuPath());
	}
}