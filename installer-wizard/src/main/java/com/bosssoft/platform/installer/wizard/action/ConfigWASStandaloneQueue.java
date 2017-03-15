package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebshpereJMSConfig;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;

public class ConfigWASStandaloneQueue implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String profileName = context.getStringValue("AS_WAS_PROFILE");
		String userName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");
		String nodeName = context.getStringValue("AS_WAS_NODE_NAME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String appName = context.getStringValue("DEFAULT_APP_NAME");

		WebsphereEnv env = new WebsphereEnv();
		env.setProfilesHome(profileHome);
		env.setProfileName(profileName);
		env.setLoginName(userName);
		env.setPassword(password);
		env.setNodeName(nodeName);
		env.setServerName(serverName);

		WebSphereServerImpl server = new WebSphereServerImpl(env);

		WebshpereJMSConfig wjc = new WebshpereJMSConfig();
		try {
			wjc.configStandaloneQueue(server, appName);
		} catch (JEEServerOperationException e) {
			throw new InstallException("Config Was JMS Queue failed!", e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}