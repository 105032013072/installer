package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.internal.JDBCDataSourceImpl;

public class ConfigAppsvr implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		IDataSource ds = getDataSource(context, parameters);

		if (appsvrType.toLowerCase().indexOf("tomcat") != -1) {
			String tomcatHome = context.getStringValue("AS_TOMCAT_HOME");
			DataSourceConfig4Tomcat config = new DataSourceConfig4Tomcat(ds, tomcatHome);
			boolean success = config.excute();
			if (!success)
				this.logger.error("Add Tomcat DataSource failed!");
		} else if (appsvrType.toLowerCase().indexOf("jboss") != -1) {
			String jbossHome = context.getStringValue("AS_JBOSS_HOME");
			DataSourceConfig4JBoss config = new DataSourceConfig4JBoss(ds, jbossHome);
			boolean success = config.excute();
			if (!success)
				this.logger.error("Add JBoss DataSource failed!");
		} else if (appsvrType.toLowerCase().indexOf("weblogic") == -1) {
			if (appsvrType.toLowerCase().indexOf("websphere") == -1)
				;
		}
	}

	public void rollback(IContext arg0, Map arg1) throws InstallException {
	}

	protected IDataSource getDataSource(IContext context, Map parameters) {
		JDBCDataSourceImpl dataSource = new JDBCDataSourceImpl();

		String dbDriver = context.getStringValue("DB_DRIVER");
		dataSource.setDriver(dbDriver);

		String jndiName = context.getStringValue("DB_DS_JNDI_NAME");

		dataSource.setJndiName(jndiName);

		String dataSourceName = jndiName;
		dataSource.setName(dataSourceName);

		String userName = context.getStringValue("DB_USERNAME");
		dataSource.setUser(userName);

		String password = context.getStringValue("DB_PASSWORD");
		dataSource.setPassword(password);

		String url = context.getStringValue("DB_URL");
		dataSource.setUrl(url);

		return dataSource;
	}
}