package com.bosssoft.platform.installer.jee.server.impl.tomcat;

import java.io.File;

import com.bosssoft.platform.installer.jee.server.internal.ApplicationModelImpl;

public class TomcatAppModel extends ApplicationModelImpl {
	private File configFile;

	public TomcatAppModel(String appName) {
		super(appName);
	}

	public File getConfigFile() {
		return this.configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}
}