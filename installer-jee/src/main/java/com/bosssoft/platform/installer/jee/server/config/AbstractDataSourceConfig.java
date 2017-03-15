package com.bosssoft.platform.installer.jee.server.config;

import com.bosssoft.platform.installer.jee.server.IConfigElement;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.ITargetModel;

public abstract class AbstractDataSourceConfig implements IConfigElement {
	private IDataSource dataSource;
	private ITargetModel target;

	public AbstractDataSourceConfig() {
	}

	public AbstractDataSourceConfig(IDataSource dataSource, ITargetModel target) {
		this.dataSource = dataSource;
		this.target = target;
	}

	public IDataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ITargetModel getTarget() {
		return this.target;
	}

	public void setTarget(ITargetModel target) {
		this.target = target;
	}

	public abstract boolean isDSExist(IJEEServer paramIJEEServer);
}