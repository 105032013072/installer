package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import com.bosssoft.platform.installer.jee.server.AbstractJEEServerEnv;

public class WeblogicEnv extends AbstractJEEServerEnv {
	private String beaHome;
	private String weblogicHome;
	private String domainHome;
	private String loginName;
	private String password;
	private String host;
	private String port;

	public WeblogicEnv() {
	}

	public WeblogicEnv(String beaHome, String weblogicHome, String domainHome, String loginName, String password, String host, String port) {
		this.beaHome = beaHome;
		this.weblogicHome = weblogicHome;
		this.domainHome = domainHome;
		this.loginName = loginName;
		this.password = password;
		this.host = host;
		this.port = port;
		if (this.beaHome != null) {
			this.beaHome = this.beaHome.replace("\\", "/");
		}
		if (this.weblogicHome != null) {
			this.weblogicHome = this.weblogicHome.replace("\\", "/");
		}
		if (this.domainHome != null)
			this.domainHome = this.domainHome.replace("\\", "/");
	}

	public String getBeaHome() {
		return this.beaHome;
	}

	public void setBeaHome(String beaHome) {
		this.beaHome = beaHome;
		if (this.beaHome != null)
			this.beaHome = this.beaHome.replace("\\", "/");
	}

	public String getDomainHome() {
		return this.domainHome;
	}

	public void setDomainHome(String domainHome) {
		this.domainHome = domainHome;
		if (this.domainHome != null)
			this.domainHome = this.domainHome.replace("\\", "/");
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getWeblogicHome() {
		return this.weblogicHome;
	}

	public void setWeblogicHome(String weblogicHome) {
		this.weblogicHome = weblogicHome;
		if (this.weblogicHome != null)
			this.weblogicHome = this.weblogicHome.replace("\\", "/");
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}