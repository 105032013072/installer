package com.bosssoft.platform.installer.jee.server;

public interface IApplicationModel {
	public String getAppName();

	public String getDeployPath();

	public ITargetModel getTargetModel();
}