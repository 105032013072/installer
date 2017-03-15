package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import java.io.File;

public class RunWlstCmdArgs {
	private File propertiesFile;
	private File wlstCmdFile;
	private File pythonFile;

	public RunWlstCmdArgs() {
	}

	public RunWlstCmdArgs(File wlstCmdFile, File pythonFile, File propertiesFile) {
		this.propertiesFile = propertiesFile;
		this.wlstCmdFile = wlstCmdFile;
		this.pythonFile = pythonFile;
	}

	public File getPropertiesFile() {
		return this.propertiesFile;
	}

	public void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public File getWlstCmdFile() {
		return this.wlstCmdFile;
	}

	public void setWlstCmdFile(File wlstCmdFile) {
		this.wlstCmdFile = wlstCmdFile;
	}

	public File getPythonFile() {
		return this.pythonFile;
	}

	public void setPythonFile(File pythonFile) {
		this.pythonFile = pythonFile;
	}
}