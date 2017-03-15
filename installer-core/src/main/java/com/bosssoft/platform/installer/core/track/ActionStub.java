package com.bosssoft.platform.installer.core.track;

import java.io.Serializable;
import java.util.Map;

import com.bosssoft.platform.installer.core.action.IAction;

public class ActionStub implements Serializable {
	private IAction action = null;
	private Map parameters = null;

	public ActionStub(IAction a, Map m) {
		this.action = a;
		this.parameters = m;
	}

	public IAction getAction() {
		return this.action;
	}

	public void setAction(IAction a) {
		this.action = a;
	}

	public Map getParameters() {
		return this.parameters;
	}

	public void setParameters(Map m) {
		this.parameters = m;
	}
}