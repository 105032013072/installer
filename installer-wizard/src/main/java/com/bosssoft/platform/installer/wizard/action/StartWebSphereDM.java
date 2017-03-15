package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WASScriptTool;

public class StartWebSphereDM implements IAction {
	transient Logger logger = Logger.getLogger(getClass());
	private static final String START_CMD = "startServer.bat";
	private static final String START_SH = "startServer.sh";

	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String userName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");

		if (isStarted(profileHome, serverName, userName, password)) {
			return;
		}
		boolean isCluster = false;
		if (isCluster)
			startCluster();
		else {
			startServer(profileHome, serverName);
		}
		if (!isStarted(profileHome, serverName, userName, password))
			throw new InstallException("Start WAS failed!");
	}

	private void startCluster() {
	}

	private void startServer(String profileHome, String serverName) {
		WASScriptTool.startServer(profileHome, serverName);
	}

	private boolean isStarted(String profileHome, String serverName, String userName, String password) {
		return WASScriptTool.isStarted(profileHome, serverName, userName, password);
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}