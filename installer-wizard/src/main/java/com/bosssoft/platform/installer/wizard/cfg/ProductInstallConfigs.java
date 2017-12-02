package com.bosssoft.platform.installer.wizard.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ProductInstallConfigs {
	private static XStream xStream = null;
	static {
		xStream = new XStream(new DomDriver());

		xStream.alias("server", Server.class);
		xStream.alias("servers", Servers.class);

		xStream.useAttributeFor("name", String.class);
		xStream.useAttributeFor("version", String.class);
		xStream.useAttributeFor("clusterDeploy", String.class);
		xStream.useAttributeFor("size", String.class);
		xStream.useAttributeFor("editorPanel", String.class);
		xStream.useAttributeFor("clusterEditorPanel", String.class);
		xStream.useAttributeFor("jars", String.class);
		xStream.useAttributeFor("desc", String.class);
		xStream.useAttributeFor("type", String.class);

		xStream.addImplicitCollection(Servers.class, "servers", Server.class);
	}
	private static final String DB_SERVERS_CONFIG_FILENAME = "dbsvr-supported";
	private static final String APP_SERVERS_CONFIG_FILENAME = "appsvr-supported";
	private static List<Server> appsvrs = loadSupportedServers(xStream, APP_SERVERS_CONFIG_FILENAME);
	private static List<Server> dbsvrs = loadSupportedServers(xStream, DB_SERVERS_CONFIG_FILENAME);

	private static List<Server> loadSupportedServers(XStream stream, String configFileName) {
		File configFile = getConfigFile(configFileName);
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile));
			Servers result = (Servers) xStream.fromXML(reader);
			return result.getServers();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File getConfigFile(String defaultName) {
		String edition = InstallRuntime.INSTANCE.getContext().getStringValue("EDITION").toLowerCase();
		String configFileName = defaultName + "_" + edition + ".xml";

		File configFile = InstallerFileManager.getConfigFile(configFileName);
		if (!configFile.exists()) {
			configFileName = defaultName + ".xml";
			configFile = InstallerFileManager.getConfigFile(configFileName);
		}
		return configFile;
	}

	public static List<Server> getSupportedDBSvrs() {
		return dbsvrs;
	}

	public static List<Server> getSupportedAppSvrs() {
		return appsvrs;
	}

	public static Server getDBServer(String name) {
		for (Server s : dbsvrs) {
			if (s.getName().equalsIgnoreCase(name))
				return s;
		}
		return null;
	}

	public static Server getAppServer(String name) {
		for (Server s : appsvrs) {
			if (s.getName().equalsIgnoreCase(name))
				return s;
		}
		return null;
	}
}