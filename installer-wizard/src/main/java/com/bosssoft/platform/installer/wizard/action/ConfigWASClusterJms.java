package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebshpereJMSConfig;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;

public class ConfigWASClusterJms implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String profileName = context.getStringValue("AS_WAS_PROFILE");
		String userName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");
		String cellName = context.getStringValue("AS_WAS_CELL_NAME");

		String clusterName = context.getStringValue("AS_WAS_CLUSTER_NAME");
		String appName = context.getStringValue("DEFAULT_APP_NAME");

		WebsphereEnv env = new WebsphereEnv();
		env.setProfilesHome(profileHome);
		env.setProfileName(profileName);
		env.setLoginName(userName);
		env.setPassword(password);
		env.setCellName(cellName);

		env.setClusterName(clusterName);

		WebSphereServerImpl server = new WebSphereServerImpl(env);

		WebshpereJMSConfig wjc = new WebshpereJMSConfig();
		try {
			wjc.configClusterJMS(server, appName);
		} catch (JEEServerOperationException e) {
			throw new InstallException("Config Was JMS failed!", e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}