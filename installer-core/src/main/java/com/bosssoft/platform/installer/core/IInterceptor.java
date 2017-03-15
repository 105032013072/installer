package com.bosssoft.platform.installer.core;

import java.util.Map;

import com.bosssoft.platform.installer.core.action.IAction;

public interface IInterceptor {
	
	public void doAction(String paramString, IAction action, IContext context, Map paramMap, int paramInt);
}