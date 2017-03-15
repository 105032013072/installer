package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebSphereServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.websphere.WebsphereEnv;

public class SetWASClusterJvmParam implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		File profile = new File(profileHome);
		String websphereHome = profile.getParentFile().getParentFile().getAbsolutePath();
		String profileName = profile.getName();

		String loginName = context.getStringValue("AS_WAS_USERNAME");
		String password = context.getStringValue("AS_WAS_PASSWORD");

		String clusterName = context.getStringValue("AS_WAS_CLUSTER_NAME");
		String cellName = context.getStringValue("AS_WAS_CELL_NAME");
		WebsphereEnv env = new WebsphereEnv(websphereHome, profileHome, profileName, cellName, null, null, 0, 0, loginName, password, null);
		env.setClusterName(clusterName);

		WebSphereServerImpl serverImpl = new WebSphereServerImpl(env);

		String paramName = parameters.get("PARAM_NAME").toString();
		String paramValue = parameters.get("PARAM_VALUE").toString().replaceAll("\\\\", "/");
		try {
			serverImpl.setClusterJVMParameter(paramName, paramValue);
		} catch (JEEServerOperationException e) {
			this.logger.error(e);
			throw new InstallException(e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}