package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;

public class StartupWeblogicServer implements IAction {
	private static final String JVM_ARGS = "JVM_ARGS";

	public void execute(IContext context, Map parameters) throws InstallException {
		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");
		String userName = context.getStringValue("AS_WL_USERNAME");
		String password = context.getStringValue("AS_WL_PASSWORD");
		String ip = context.getStringValue("USER_IP");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");
		Object jvmArgs = parameters.get("JVM_ARGS");

		WeblogicEnv env = new WeblogicEnv(beaHome, wlHome, domainHome, userName, password, ip, port);

		WeblogicServerImpl serverImpl = new WeblogicServerImpl(env);
		try {
			if (serverImpl.isStarted()) {
				return;
			}

			if ((jvmArgs != null) && (jvmArgs.toString().trim().length() > 0))
				serverImpl.start(false, jvmArgs.toString());
			else {
				serverImpl.start(false, null);
			}
			context.setValue("DONE_START_WEBLOGIC", "true");
		} catch (JEEServerOperationException e) {
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}