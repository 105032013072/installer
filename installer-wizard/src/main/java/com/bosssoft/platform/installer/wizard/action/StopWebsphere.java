package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WASScriptTool;

public class StopWebsphere implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String userName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");

		stopServer(profileHome, serverName, userName, password);
	}

	private void stopServer(String profileHome, String serverName, String userName, String password) {
		WASScriptTool.stopServer(profileHome, serverName, userName, password);
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}