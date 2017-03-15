package com.bosssoft.platform.installer.core.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;

public abstract interface IAction {
	public static final String STRATEGY_RETRY = "retry";
	public static final String STRATEGY_QUIT = "quit";
	public static final String STRATEGY_IGNORE = "ignore";

	public abstract void execute(IContext paramIContext, Map paramMap) throws InstallException;

	public abstract void rollback(IContext paramIContext, Map paramMap) throws InstallException;
}