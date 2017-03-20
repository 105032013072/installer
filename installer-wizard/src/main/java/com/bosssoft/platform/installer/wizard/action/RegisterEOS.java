package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class RegisterEOS implements IAction {
	private transient Logger logger = Logger.getLogger(RegisterEOS.class.getName());

	public int strategyWhenException(Exception arg0) {
		return 0;
	}

	public static String getEOSREGPath(IContext context) {
		return System.getProperty("user.home");
	}

	public void execute(IContext context, Map map) throws InstallException {
		String userdir = getEOSREGPath(context);
		Registry r = new Registry(userdir);
		this.logger.info("Register the installed information to " + userdir);
		r.addInstalledProduct(context);
	}

	public void rollback(IContext context, Map map) throws InstallException {
	}
}