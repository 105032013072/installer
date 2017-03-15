package com.bosssoft.platform.installer.core.track;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.bosssoft.platform.installer.core.action.IAction;

public class ActionsTrack implements Serializable {
	private Vector<ActionStub> actions = new Vector<ActionStub>();

	public void addAction(IAction a, Map m) {
		ActionStub stub = new ActionStub(a, m);
		this.actions.add(0, stub);
	}

	public List<ActionStub> getActions() {
		return this.actions;
	}

	public void removeLast() {
		if (this.actions.size() > 0)
			this.actions.remove(0);
	}

	public int size() {
		return this.actions.size();
	}
}