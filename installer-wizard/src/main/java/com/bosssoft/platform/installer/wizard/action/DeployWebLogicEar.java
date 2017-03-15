package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

public class DeployWebLogicEar implements IAction {
	private Logger logger = Logger.getLogger(DeployWebLogicEar.class);
	private static final String BEA_HOME = "BEA_HOME";
	private static final String WEBLOGIC_HOME = "WEBLOGIC_HOME";
	private static final String DOMAIN_HOME = "DOMAIN_HOME";
	private static final String LOGIN_NAME = "LOGIN_NAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String IP = "IP";
	private static final String PORT = "PORT";
	private static final String EAR_PATH = "EAR_PATH";
	private static final String TARGET_SERVER = "TARGET_SERVER";
	private static final String APP_NAME = "APP_NAME";
	private static final String STAGE_MODEL = "STAGE_MODEL";

	public void execute(IContext context, Map parameters) throws InstallException {
		String beaHome = parameters.get("BEA_HOME").toString();
		String webLogicHome = parameters.get("WEBLOGIC_HOME").toString();
		String domainHome = parameters.get("DOMAIN_HOME").toString();
		String loginName = parameters.get("LOGIN_NAME").toString();
		String password = parameters.get("PASSWORD").toString();
		String ip = parameters.get("IP").toString();
		String port = parameters.get("PORT").toString();
		String earPath = parameters.get("EAR_PATH").toString();
		String targetServer = parameters.get("TARGET_SERVER").toString();
		String stageModel = "";
		if (parameters.get("STAGE_MODEL") != null) {
			stageModel = parameters.get("STAGE_MODEL").toString();
		}

		WeblogicEnv env = new WeblogicEnv(beaHome, webLogicHome, domainHome, loginName, password, ip, port);
		WeblogicServerImpl serverImpl = new WeblogicServerImpl(env);

		TargetModelImpl targetModel = new TargetModelImpl(targetServer);
		String isCulster = context.getStringValue("IS_CLUSTER");
		if ((isCulster != null) && ("true".equalsIgnoreCase(isCulster))) {
			targetModel.setCluster(true);
		}

		File earFile = new File(earPath);
		String appName = earFile.getName();
		Object _appName = parameters.get("APP_NAME");
		if ((_appName == null) || (StringUtils.isEmpty(_appName.toString()))) {
			if ((appName.toLowerCase().endsWith(".ear")) || (appName.toLowerCase().endsWith(".war")))
				appName = appName.substring(0, appName.length() - 4);
		} else {
			appName = _appName.toString();
		}
		try {
			Properties properties = new Properties();
			properties.put("STAGE_MODEL", stageModel);
			serverImpl.deploy(appName, earFile, targetModel, properties);
		} catch (JEEServerOperationException e) {
			this.logger.error(e.getMessage(), e);
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}