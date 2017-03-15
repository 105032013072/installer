package com.bosssoft.platform.installer.core.action;

import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.ExpressionParser;

public class SetValue extends AbstractAction {
	private String key = null;
	private String value = null;

	public void execute(IContext context, Map parameters) throws InstallException {
		context.setValue(this.key, ExpressionParser.parseString(this.value));
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void setKey(String k) {
		this.key = k;
	}

	public void setValue(String v) {
		this.value = v;
	}
}