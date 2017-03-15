package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossEnv;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossServerImpl;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

public class DeployJBossEar implements IAction {
	private Logger logger = Logger.getLogger(DeployJBossEar.class);
	private static final String JBOSS_HOME = "JBOSS_HOME";
	private static final String SERVER_NAME = "SERVER_NAME";
	private static final String EAR_PATH = "EAR_PATH";

	public void execute(IContext context, Map parameters) throws InstallException {
		String jbossHome = parameters.get("JBOSS_HOME").toString();
		String serveName = parameters.get("SERVER_NAME").toString();
		String earPath = parameters.get("EAR_PATH").toString();
		JBossEnv env = new JBossEnv(jbossHome, serveName);
		JBossServerImpl server = new JBossServerImpl(env);
		File earFile = new File(earPath);
		String appName = earFile.getName();

		if (appName.toLowerCase().endsWith(".ear")) {
			appName = appName.substring(0, appName.length() - 4);
		}

		ITargetModel target = new TargetModelImpl(serveName);
		try {
			server.deploy(appName, earFile, target, null);
		} catch (JEEServerOperationException e) {
			this.logger.error(e.getMessage(), e);
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}