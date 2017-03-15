package com.bosssoft.platform.installer.wizard.cfg;

public class Server {
	private String name = null;
	private String version = null;
	private String clusterDeploy = null;
	private String editorPanel = null;
	private String clusterEditorPanel = null;
	private String jars = null;

	public String getName() {
		return this.name;
	}

	public void setName(String v) {
		this.name = v;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String v) {
		this.version = v;
	}

	public boolean supportedCluterDeploy() {
		if (this.clusterDeploy == null)
			return false;
		return this.clusterDeploy.equalsIgnoreCase("true");
	}

	public void setClusterDeploy(String c) {
		this.clusterDeploy = c;
	}

	public String getEditorPanel() {
		return this.editorPanel;
	}

	public void setEditorPanel(String c) {
		this.editorPanel = c;
	}

	public String getClusterEditorPanel() {
		return this.clusterEditorPanel;
	}

	public void setClusterEditorPanel(String c) {
		this.clusterEditorPanel = c;
	}

	public String getJars() {
		return this.jars;
	}

	public void setJars(String jars) {
		this.jars = jars;
	}

	public String toString() {
		return this.name.concat(this.version);
	}
}