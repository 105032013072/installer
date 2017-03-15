package com.bosssoft.platform.installer.jee.server;

public interface ITargetModel {
	
	public String getName();

	public ITargetModel[] getChildren();

	public boolean isCluster();
}