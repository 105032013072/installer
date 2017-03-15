package com.bosssoft.platform.installer.jee.server.impl.websphere;

import com.bosssoft.platform.installer.jee.server.AbstractJEEServerEnv;

public class WebsphereEnv extends AbstractJEEServerEnv {
	private String websphereHome;
	private String profilesHome;
	private String profileName;
	private String cellName;
	private String nodeName;
	private String serverName;
	private String clusterName;
	private String ip;
	private int adminPort = 9060;
	private int adminSecurity = 9043;
	private String loginName;
	private String password;
	private String isDeployEJB;
	private String dbDriverJars = null;

	public WebsphereEnv() {
	}

	public WebsphereEnv(String websphereHome, String profilesHome, String profileName, String cellName, String nodeName, String ip, int adminPort, int adminSecurity,
			String loginName, String password, String deployEJB) {
		this.websphereHome = websphereHome;
		this.profilesHome = profilesHome;
		this.profileName = profileName;
		this.cellName = cellName;
		this.nodeName = nodeName;
		this.ip = ip;
		this.adminPort = adminPort;
		this.adminSecurity = adminSecurity;
		this.loginName = loginName;
		this.password = password;
		this.isDeployEJB = deployEJB;
	}

	public int getAdminPort() {
		return this.adminPort;
	}

	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}

	public int getAdminSecurity() {
		return this.adminSecurity;
	}

	public void setAdminSecurity(int adminSecurity) {
		this.adminSecurity = adminSecurity;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public String getProfileName() {
		return this.profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfilesHome() {
		return this.profilesHome;
	}

	public void setProfilesHome(String profilesHome) {
		this.profilesHome = profilesHome;
	}

	public String getWebsphereHome() {
		return this.websphereHome;
	}

	public void setWebsphereHome(String websphereHome) {
		this.websphereHome = websphereHome;
	}

	public String getCellName() {
		return this.cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getServerName() {
		return this.serverName;
	}

	public void setServerName(String name) {
		this.serverName = name;
	}

	public String getDbDriverJars() {
		return this.dbDriverJars;
	}

	public void setDbDriverJars(String dbDriverJars) {
		this.dbDriverJars = dbDriverJars;
	}

	public String getDeployEJB() {
		return this.isDeployEJB;
	}

	public String getClusterName() {
		return this.clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}