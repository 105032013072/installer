package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.AbstractAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

public class DeployWASEar extends AbstractAction {
	private Logger logger = Logger.getLogger(getClass());
	private static final String ADMIN_PORT = "ADMIN_PORT";
	private static final String ADMIN_SECURITY = "ADMIN_SECURITY";
	private static final String EAR_PATH = "EAR_PATH";

	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		File profile = new File(profileHome);
		String websphereHome = profile.getParentFile().getParentFile().getAbsolutePath();
		String profileName = profile.getName();

		String cellName = context.getStringValue("AS_WAS_CELL_NAME");
		String nodeName = context.getStringValue("AS_WAS_NODE_NAME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String ip = context.getStringValue("USER_IP");
		int adminPort = 9060;
		int adminSecurity = 9043;
		if (parameters.get("ADMIN_PORT") != null)
			adminPort = Integer.parseInt(parameters.get("ADMIN_PORT").toString());
		if (parameters.get("ADMIN_SECURITY") != null) {
			adminSecurity = Integer.parseInt(parameters.get("ADMIN_SECURITY").toString());
		}
		String deployEJB = "true";
		if (parameters.get("IS_DEPLOY_EJB") != null) {
			deployEJB = parameters.get("IS_DEPLOY_EJB").toString();
		}
		String loginName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");

		String externalConfigDir = parameters.get("EXTERNAL_CONFIG_DIR").toString();
		String earPath = parameters.get("EAR_PATH").toString();
		String isCreateJMS = parameters.get("IS_CREATE_JMS") != null ? parameters.get("IS_CREATE_JMS").toString() : "false";
		String targetServer = serverName;

		WebsphereEnv env = new WebsphereEnv(websphereHome, profileHome, profileName, cellName, nodeName, ip, adminPort, adminSecurity, loginName, password, deployEJB);
		env.setServerName(serverName);
		env.setProperty("EXTERNAL_CONFIG_DIR", externalConfigDir);
		env.setProperty("IS_CREATE_JMS", isCreateJMS);

		WebSphereServerImpl serverImpl = new WebSphereServerImpl(env);

		String appName = parameters.get("APP_NAME").toString();
		File earFile = new File(earPath);

		ITargetModel targetModel = new TargetModelImpl(targetServer);
		try {
			serverImpl.deploy(appName, earFile, targetModel, null);
		} catch (JEEServerOperationException e) {
			this.logger.error(e.getMessage(), e);
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}