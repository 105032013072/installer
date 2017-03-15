package com.bosssoft.platform.installer.core.cfg;

import java.util.ArrayList;
import java.util.List;

import com.bosssoft.platform.installer.core.action.IAction;

public class Actions {
	private String id = null;
	
	private ArrayList<IAction> actions = new ArrayList<IAction>();

	public List<IAction> getActions() {
		return this.actions;
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void addAction(IAction action) {
		this.actions.add(action);
	}
}