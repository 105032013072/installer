package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereDatasourceConfig;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;
import com.bosssoft.platform.installer.jee.server.internal.JDBCDataSourceImpl;
import com.bosssoft.platform.installer.wizard.cfg.ProductInstallConfigs;

public class ConfigWASDataSource implements IAction {
	private final String WAS_INSTALL_ROOT = "\\${WAS_INSTALL_ROOT}/lib";

	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String profileName = context.getStringValue("AS_WAS_PROFILE");
		String userName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");
		String nodeName = context.getStringValue("AS_WAS_NODE_NAME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String cellName = context.getStringValue("AS_WAS_CELL_NAME");

		WebsphereEnv env = new WebsphereEnv();
		env.setProfilesHome(profileHome);
		env.setProfileName(profileName);
		env.setNodeName(nodeName);
		env.setLoginName(userName);
		env.setPassword(password);
		env.setServerName(serverName);
		env.setCellName(cellName);

		String jndiName = context.getStringValue("DB_DS_JNDI_NAME");
		String dsName = jndiName;

		WebSphereServerImpl server = new WebSphereServerImpl(env);

		WebsphereDatasourceConfig wdc = new WebsphereDatasourceConfig();
		JDBCDataSourceImpl ds = new JDBCDataSourceImpl();

		String jdbcJars = context.getStringValue("DB_JDBC_LIBS");
		if ((jdbcJars == null) || (jdbcJars.equals(""))) {
			jdbcJars = getJdbcJars(context);
		}

		String informixServer = context.getStringValue("DB_INFORMIX_SERVER");
		if ((informixServer == null) || (informixServer.equals(""))) {
			informixServer = "server";
		}

		String dbUrl = context.getStringValue("DB_URL");

		String dbType = context.getStringValue("DB_TYPE");
		if (dbType.startsWith("DB2"))
			dbType = "db2";
		else if (dbType.startsWith("Informix")) {
			dbUrl = informixServer;
		}

		ds.setProperty("DB_IP", context.getStringValue("DB_IP"));
		ds.setProperty("DB_PORT", context.getStringValue("DB_SERVER_PORT"));
		ds.setProperty("DB_DRIVER_JARS", jdbcJars);
		ds.setProperty("DB_NAME", context.getStringValue("DB_NAME"));
		ds.setProperty("DB_TYPE", dbType);
		ds.setUrl(dbUrl);
		ds.setDriver(context.getStringValue("DB_DRIVER"));
		ds.setJndiName(jndiName);
		ds.setName(dsName);
		ds.setUser(context.getStringValue("DB_USERNAME"));
		ds.setPassword(context.getStringValue("DB_PASSWORD"));

		wdc.setDataSource(ds);
		try {
			wdc.config(server);
		} catch (JEEServerOperationException e) {
			throw new InstallException("config WAS DataSource failed!", e);
		}
	}

	private String getJdbcJars(IContext context) {
		boolean isDefaultJars = context.getStringValue("DB_IS_DEFAULT_JAR").equals("true");
		StringBuffer drivers = new StringBuffer("");
		String dbType = context.getStringValue("DB_TYPE");
		String defaultDriver = ProductInstallConfigs.getDBServer(dbType).getJars();

		if (isDefaultJars) {
			drivers = drivers.append("\\${WAS_INSTALL_ROOT}/lib").append("/").append(defaultDriver);
		} else {
			String[] libStr = context.getStringValue("DB_JDBC_LIBS").replace('\\', '/').split(";");
			for (int i = 0; i < libStr.length; i++) {
				String temp = libStr[i];
				int beginIndex = temp.lastIndexOf("/");
				String jdbcDriver = temp.substring(beginIndex);
				drivers = drivers.append("\\${WAS_INSTALL_ROOT}/lib").append(jdbcDriver).append(";");
			}
		}
		return drivers.toString();
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}