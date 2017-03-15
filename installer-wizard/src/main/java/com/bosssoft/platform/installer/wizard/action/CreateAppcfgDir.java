package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.AbstractAction;

public class CreateAppcfgDir extends AbstractAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String appsvrType = parameters.get("APPSVR_TYPE").toString();
		String appName = parameters.get("DEFAULT_APP_NAME").toString();
	}

	public void rollback(IContext arg0, Map arg1) throws InstallException {
	}
}