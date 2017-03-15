package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class InstallerInitialize implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		setCurrentLocale(context);
		context.setValue("INSTALL_ROOT", InstallerFileManager.getInstallerRoot());
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private void setCurrentLocale(IContext context) {
		String locale = I18nUtil.getCurrentLocale().toString();
		context.setValue("current.locale", locale);
	}
}