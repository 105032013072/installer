package com.bosssoft.platform.installer.wizard.action;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossEnv;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;

public class CheckAppExistAction implements IAction {
	private String APP_NAME = "APP_NAME";

	public void execute(IContext context, Map parameters) throws InstallException {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		String appName = ExpressionParser.parseString(parameters.get(this.APP_NAME).toString());

		if (checkAppExist(context, appsvrType, appName)) {
			String msg = I18nUtil.getString("CheckAppExistAction.Exist.Error");
			msg = MessageFormat.format(msg, new Object[] { appName });
			throw new InstallException(msg);
		}
	}

	public static boolean checkAppExist(IContext context, String appsvrType, String appName) {
		boolean exist = false;

		if (appsvrType.toLowerCase().indexOf("tomcat") != -1) {
			String tomcatHome = context.getStringValue("AS_TOMCAT_HOME");
			try {
				IJEEServer jeeServer = new TomcatServerImpl(new TomcatEnv(tomcatHome));
				exist = exsitApp(jeeServer, appName);
			} catch (IOException localIOException) {
			}
		} else if (appsvrType.toLowerCase().indexOf("jboss") != -1) {
			String jbossHome = context.getStringValue("AS_JBOSS_HOME");
			IJEEServer jeeServer = new JBossServerImpl(new JBossEnv(jbossHome, "default"));
			exist = exsitApp(jeeServer, appName);
		} else if (appsvrType.toLowerCase().indexOf("weblogic") != -1) {
			WeblogicEnv weblogicEnv = createWeblogicEnv(context);
			IJEEServer jeeServer = new WeblogicServerImpl(weblogicEnv);
			exist = exsitApp(jeeServer, appName);
		} else if (appsvrType.toLowerCase().indexOf("websphere") != -1) {
			WebsphereEnv env = createwebsphereEnv(context);

			IJEEServer jeeServer = new WebSphereServerImpl(env);

			exist = exsitApp(jeeServer, appName);
		}

		return exist;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private static WeblogicEnv createWeblogicEnv(IContext context) {
		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");

		String userName = context.getStringValue("AS_WL_USERNAME");
		String password = context.getStringValue("AS_WL_PASSWORD");
		String host = context.getStringValue("USER_IP");
		WeblogicEnv env = new WeblogicEnv(beaHome, wlHome, domainHome, userName, password, host, port);
		return env;
	}

	private static WebsphereEnv createwebsphereEnv(IContext context) {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String profileName = context.getStringValue("AS_WAS_PROFILE");
		String nodeName = context.getStringValue("AS_WAS_NODE_NAME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String cellName = context.getStringValue("AS_WAS_CELL_NAME");
		WebsphereEnv env = new WebsphereEnv();
		env.setProfilesHome(profileHome);
		env.setProfileName(profileName);
		env.setNodeName(nodeName);
		env.setServerName(serverName);
		env.setCellName(cellName);
		return env;
	}

	private static boolean exsitApp(IJEEServer jeeServer, String appName) {
		IApplicationModel[] apps = new IApplicationModel[0];
		try {
			apps = jeeServer.getApplications();
		} catch (JEEServerOperationException localJEEServerOperationException) {
		}
		for (int i = 0; i < apps.length; i++) {
			IApplicationModel model = apps[i];
			if (StringUtils.equals(model.getAppName(), appName)) {
				return true;
			}
		}
		return false;
	}
}