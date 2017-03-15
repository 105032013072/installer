package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicDataSourceConfig;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.internal.JDBCDataSourceImpl;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

public class CreateWeblogicDataSource implements IAction {
	transient Logger logger = Logger.getLogger(getClass());
	private static final String DS_NAME = "DS_NAME";
	private static final String DS_JNDI = "DS_JNDI";
	private static final String DS_TEST_TABLE = "DS_TEST_TABLE";

	public void execute(IContext context, Map parameters) throws InstallException {
		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");

		String userName = context.getStringValue("AS_WL_USERNAME");
		String password = context.getStringValue("AS_WL_PASSWORD");
		String host = context.getStringValue("USER_IP");
		String targetServer = context.getStringValue("AS_WL_TARGET_SERVER");
		WeblogicEnv env = new WeblogicEnv(beaHome, wlHome, domainHome, userName, password, host, port);

		IDataSource ds = getDataSource(context, parameters);

		TargetModelImpl target = new TargetModelImpl(targetServer);

		String isCluster = context.getStringValue("IS_CLUSTER");
		if ((isCluster != null) && ("true".equalsIgnoreCase(isCluster))) {
			target.setCluster(true);
		}

		WeblogicDataSourceConfig dataSourceConfig = new WeblogicDataSourceConfig(ds, target);

		WeblogicServerImpl serverImpl = new WeblogicServerImpl(env);
		try {
			serverImpl.config(dataSourceConfig);
		} catch (JEEServerOperationException e) {
			this.logger.error("Add Weblogic DataSource failed!");
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public boolean isDsExist(IContext context, String dsName) {
		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");

		String userName = context.getStringValue("AS_WL_USERNAME");
		String password = context.getStringValue("AS_WL_PASSWORD");
		String host = context.getStringValue("USER_IP");
		String targetServer = context.getStringValue("AS_WL_TARGET_SERVER");
		WeblogicEnv env = new WeblogicEnv(beaHome, wlHome, domainHome, userName, password, host, port);

		JDBCDataSourceImpl ds = new JDBCDataSourceImpl();
		ds.setName(dsName);

		TargetModelImpl target = new TargetModelImpl(targetServer);

		String isCluster = context.getStringValue("IS_CLUSTER");
		if ((isCluster != null) && ("true".equalsIgnoreCase(isCluster))) {
			target.setCluster(true);
		}

		WeblogicDataSourceConfig dataSourceConfig = new WeblogicDataSourceConfig(ds, target);

		WeblogicServerImpl serverImpl = new WeblogicServerImpl(env);

		return dataSourceConfig.isDSExist(serverImpl);
	}

	protected IDataSource getDataSource(IContext context, Map parameters) {
		JDBCDataSourceImpl dataSource = new JDBCDataSourceImpl();

		String dbDriver = context.getStringValue("DB_DRIVER");
		dataSource.setDriver(dbDriver);
		String jndiName = context.getStringValue("DB_DS_JNDI_NAME");
		dataSource.setJndiName(jndiName);

		String dataSourceName = jndiName;
		dataSource.setName(dataSourceName);

		if (parameters.get("DS_TEST_TABLE") != null) {
			dataSource.setTestTable(parameters.get("DS_TEST_TABLE").toString());
		}

		String userName = context.getStringValue("DB_USERNAME");
		dataSource.setUser(userName);

		String password = context.getStringValue("DB_PASSWORD");
		dataSource.setPassword(password);

		String url = context.getStringValue("DB_URL");
		dataSource.setUrl(url);

		return dataSource;
	}
}