package com.bosssoft.platform.installer.jee.server.impl.jboss;

import com.bosssoft.platform.installer.jee.server.AbstractJEEServerEnv;

public class JBossEnv extends AbstractJEEServerEnv {
	private String jbossHome;
	private String serverName;

	public JBossEnv() {
	}

	public JBossEnv(String jbossHome, String serverName) {
		this.jbossHome = jbossHome;
		this.serverName = serverName;
	}

	public String getJbossHome() {
		return this.jbossHome;
	}

	public void setJbossHome(String jbossHome) {
		this.jbossHome = jbossHome;
	}

	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}