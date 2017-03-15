package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;

public class StopWeblogicServer implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");
		String userName = context.getStringValue("AS_WL_USERNAME");
		String password = context.getStringValue("AS_WL_PASSWORD");
		String ip = context.getStringValue("USER_IP");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");

		WeblogicEnv env = new WeblogicEnv(beaHome, wlHome, domainHome, userName, password, ip, port);

		WeblogicServerImpl serverImpl = new WeblogicServerImpl(env);
		try {
			serverImpl.stop(false);
		} catch (JEEServerOperationException e) {
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}