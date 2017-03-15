package com.bosssoft.platform.installer.wizard.action;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossDatasourcConfig;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossEnv;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatDataSourceConfig;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereDatasourceConfig;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;
import com.bosssoft.platform.installer.jee.server.internal.JDBCDataSourceImpl;

public class CheckDataSourceExistAction implements IAction {
	private static final String DS_NAME = "DS_NAME";

	public void execute(IContext context, Map parameters) throws InstallException {
		String dsName = parameters.get(DS_NAME).toString();
		boolean exist = checkDataSourceExist(context, dsName);
		if (exist) {
			String msg = I18nUtil.getString("CheckDataSourceExistAction.Exist.Error");
			msg = MessageFormat.format(msg, new Object[] { dsName });
			throw new InstallException(msg);
		}
	}

	public static boolean checkDataSourceExist(IContext context, String dsName) {
		JDBCDataSourceImpl dataSource = new JDBCDataSourceImpl();
		dataSource.setName(dsName);
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		boolean exist = false;
		if (appsvrType.toLowerCase().indexOf("tomcat") != -1) {
			String tomcatHome = context.getStringValue("AS_TOMCAT_HOME");
			TomcatDataSourceConfig config = new TomcatDataSourceConfig();
			config.setDataSource(dataSource);
			try {
				exist = config.isDSExist(new TomcatServerImpl(new TomcatEnv(tomcatHome)));
			} catch (IOException localIOException) {
			}
		} else if (appsvrType.toLowerCase().indexOf("jboss") != -1) {
			String jbossHome = context.getStringValue("AS_JBOSS_HOME");
			JBossDatasourcConfig config = new JBossDatasourcConfig();
			config.setDataSource(dataSource);
			exist = config.isDSExist(new JBossServerImpl(new JBossEnv(jbossHome, "default")));
		} else if (appsvrType.toLowerCase().indexOf("weblogic") != -1) {
			CreateWeblogicDataSource weblogicDataSource = new CreateWeblogicDataSource();
			exist = weblogicDataSource.isDsExist(context, dsName);
		} else if (appsvrType.toLowerCase().indexOf("websphere") != -1) {
			WebsphereEnv env = createwebsphereEnv(context);
			WebSphereServerImpl serverImpl = new WebSphereServerImpl(env);
			WebsphereDatasourceConfig config = new WebsphereDatasourceConfig();
			config.setDataSource(dataSource);
			exist = config.isDSExist(serverImpl);
		}
		return exist;
	}

	public static WebsphereEnv createwebsphereEnv(IContext context) {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String profileName = context.getStringValue("AS_WAS_PROFILE");
		String nodeName = context.getStringValue("AS_WAS_NODE_NAME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String cellName = context.getStringValue("AS_WAS_CELL_NAME");
		String clusterName = context.getStringValue("AS_WAS_CLUSTER_NAME");
		WebsphereEnv env = new WebsphereEnv();
		env.setProfilesHome(profileHome);
		env.setProfileName(profileName);
		env.setNodeName(nodeName);
		env.setServerName(serverName);
		env.setCellName(cellName);
		env.setClusterName(clusterName);
		return env;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}