package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class RegisterProduct implements IAction {
	private RegisterEOS registerEOS = new RegisterEOS();

	public void execute(IContext context, Map params) throws InstallException {
		this.registerEOS.execute(context, params);
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}
}
