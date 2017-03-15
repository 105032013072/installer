package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.AbstractAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;

public class DeployTomcatWar extends AbstractAction {
	private static final String TOMCAT_HOME = "TOMCAT_HOME";
	private static final String WAR_PATH = "WAR_PATH";

	public void execute(IContext context, Map parameters) throws InstallException {
		String tomcatHome = parameters.get("TOMCAT_HOME").toString();
		String warPath = parameters.get("WAR_PATH").toString();

		TomcatEnv env = new TomcatEnv(tomcatHome);
		TomcatServerImpl server = null;
		try {
			server = new TomcatServerImpl(env);
		} catch (IOException e) {
			throw new InstallException(e);
		}
		File war = new File(warPath);
		String appName = war.getName();
		if (appName.toLowerCase().endsWith(".war"))
			appName = appName.substring(0, appName.length() - 4);
		try {
			server.deploy(appName, war, null, null);
		} catch (JEEServerOperationException e) {
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}