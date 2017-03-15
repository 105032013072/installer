package com.bosssoft.platform.installer.core.util;

import java.io.File;
import java.io.IOException;

public class InstallerFileManager {
	private static String installerRoot = null;

	private static String installerHome = getInstallerHomePath();

	private static String imageDir = installerHome.concat("/image");
	private static String configDir = installerHome.concat("/config");

	private static String resourcesDir = getInstallerRoot().concat("/resources");
	private static String baseCompsDir = resourcesDir + "/base_comps";
	private static String optionCompsDir = resourcesDir + "/option_comps";
	private static String commonCompsDir = resourcesDir + "/common_comps";
	private static String jdkDir = commonCompsDir + "/jdk";

	private static String getInstallerHomePath() {
		String path = System.getProperty("install.home");
		try {
			if (path == null)
				path = PathUtil.getFullPathRelateClass("../", InstallerFileManager.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return path;
	}

	public static String getInstallerRoot() {
		if (installerRoot == null) {
			File file = new File(installerHome);
			installerRoot = file.getParentFile().getAbsolutePath();
		}
		return installerRoot;
	}

	public static String getInstallerHome() {
		return installerHome;
	}

	public static String getResourcesDir() {
		return resourcesDir;
	}

	public static String getImageDir() {
		return imageDir;
	}

	public static String getConfigDir() {
		return configDir;
	}

	public static String getBaseCompsDir() {
		return baseCompsDir;
	}

	public static String getOptionCompsDir() {
		return optionCompsDir;
	}

	public static String getCommonCompsDir() {
		return commonCompsDir;
	}

	public static String getJDKDir() {
		return jdkDir;
	}

	public static File getConfigFile(String fileName) {
		return new File(getConfigDir(), fileName);
	}

	public static File getImageFile(String fileName) {
		return new File(getImageDir(), fileName);
	}
}