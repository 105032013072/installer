package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.wizard.appsvr.IJeeServerEnvIniter;
import com.bosssoft.platform.installer.wizard.appsvr.JBossEnvIniter;
import com.bosssoft.platform.installer.wizard.appsvr.TomcatEnvIniter;

public class InitAppsvr implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		IJeeServerEnvIniter config = getConfig(context, parameters);

		if (config != null)
			config.excute();
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private IJeeServerEnvIniter getConfig(IContext context, Map parameters) {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");

		if (appsvrType.toLowerCase().indexOf("tomcat") != -1) {
			String tomcatHome = null;

			tomcatHome = context.getStringValue("AS_TOMCAT_HOME");

			TomcatEnvIniter config = new TomcatEnvIniter(tomcatHome);
			return config;
		}

		if (appsvrType.toLowerCase().indexOf("jboss") != -1) {
			String jbossHome = context.getStringValue("AS_JBOSS_HOME");

			JBossEnvIniter config = new JBossEnvIniter(jbossHome);
			return config;
		}

		return null;
	}
}