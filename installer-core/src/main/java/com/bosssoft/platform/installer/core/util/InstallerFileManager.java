package com.bosssoft.platform.installer.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class InstallerFileManager {
	private static String installerRoot = null;

	private static String installerHome = getInstallerHomePath();

	private static String imageDir = installerHome.concat("/image");
	private static String configDir = installerHome.concat("/config");
    private static String loggerDir=installerHome.concat("/logging");
	private static String resourcesDir = getInstallerRoot();
	private static String baseCompsDir = resourcesDir + "/base_comps";
	private static String optionCompsDir = resourcesDir + "/option_comps";
	private static String commonCompsDir = resourcesDir + "/common_comps";
	private static String jdkDir = commonCompsDir + "/jdk";

	private static String getInstallerHomePath() {
		String path=null;
		try {
			path=Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
	}

	public static String getInstallerRoot() {
		String path=null;
		try {
			path=Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return path;
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
	
	public static String getLoggerDir(){
		return loggerDir;
	}

	public static File getConfigFile(String fileName) {
		return new File(getConfigDir(), fileName);
	}

	public static File getImageFile(String fileName) {
		return new File(getImageDir(), fileName);
	}
}