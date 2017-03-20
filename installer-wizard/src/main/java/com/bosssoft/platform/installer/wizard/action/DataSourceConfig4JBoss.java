package com.bosssoft.platform.installer.wizard.action;

import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossDatasourcConfig;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossEnv;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossServerImpl;
import com.bosssoft.platform.installer.wizard.appsvr.IJeeServerEnvIniter;

public class DataSourceConfig4JBoss implements IJeeServerEnvIniter {
	private IDataSource dataSource;
	private String jbossHome;

	public DataSourceConfig4JBoss(IDataSource dataSource, String jbossHome) {
		this.dataSource = dataSource;
		this.jbossHome = jbossHome;
	}

	public boolean excute() {
		try {
			JBossEnv env = new JBossEnv(this.jbossHome, "default");
			JBossServerImpl server = new JBossServerImpl(env);
			JBossDatasourcConfig dsConfig = new JBossDatasourcConfig();
			dsConfig.setDataSource(this.dataSource);
			server.config(dsConfig);

			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}