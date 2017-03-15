package com.bosssoft.platform.installer.core.action;

import java.util.HashMap;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.MessageManager;

public class UserAction extends AbstractAction {
	private String name = null;
	private String clazzName = null;
	private Map parameters = new HashMap();

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActionClassName() {
		return this.clazzName;
	}

	public void setActionClassName(String name) {
		this.clazzName = name;
	}

	public void putParameter(String name, String value) {
		this.parameters.put(name, value);
	}

	public String getParameter(String name) {
		return this.parameters.get(name).toString();
	}

	public Map getParameters() {
		return this.parameters;
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		MessageManager.syncSendMessage(getName());
		try {
			IAction action = (IAction) Class.forName(this.clazzName).newInstance();
			action.execute(context, parameters);
		} catch (Exception e) {
			throw new InstallException(e);
		} finally {
			MessageManager.syncSendMessage("");
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
		if (this.clazzName == null)
			throw new InstallException("Action class name is null!");
		try {
			IAction action = (IAction) Class.forName(this.clazzName).newInstance();
			action.rollback(context, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InstallException(e);
		}
	}

	public String toString() {
		return getName();
	}
}