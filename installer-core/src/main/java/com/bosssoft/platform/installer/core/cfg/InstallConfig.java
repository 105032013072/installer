package com.bosssoft.platform.installer.core.cfg;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstallConfig {
	public static final String TYPE_INSTALL = "install";
	public static final String TYPE_UNINSTALL = "uninstall";
	private String contextFactory = null;

	private String renderer = null;

	private String navigator = null;

	private List<Variable> variables = new ArrayList<Variable>();

	private List<String> listeners = new ArrayList<String>();

	private List<String> loadproperties = new ArrayList<String>();
	private String firstStepID;
	private HashMap<String, Step> steps = new HashMap<String, Step>();

	private HashMap<String, Actions> actionsMap = new HashMap<String, Actions>();

	private Dimension dimension = null;

	private String runnerType = null;

	public String getContextFactory() {
		return this.contextFactory;
	}

	public void setContextFactory(String className) {
		this.contextFactory = className;
	}

	public String getNavigator() {
		return this.navigator;
	}

	public void setNavigator(String v) {
		this.navigator = v;
	}

	public boolean addVariable(String key, String value) {
		return this.variables.add(new Variable(key, value));
	}

	public List<Variable> getVariables() {
		return this.variables;
	}

	public boolean addListener(String classname) {
		return this.listeners.add(classname);
	}

	public List<String> getListeners() {
		return this.listeners;
	}

	public boolean addLoadPropeties(String path) {
		return this.loadproperties.add(path);
	}

	public List<String> getLoadProperties() {
		return this.loadproperties;
	}

	public void setFirstStepID(String firstStepID) {
		this.firstStepID = firstStepID;
	}

	public String getFirstStepID() {
		return this.firstStepID;
	}

	public void putStep(String id, Step step) {
		this.steps.put(id, step);
	}

	public Step getStep(String id) {
		return (Step) this.steps.get(id);
	}

	public void putActions(String id, Actions actions) {
		this.actionsMap.put(id, actions);
	}

	public Actions getActions(String id) {
		return (Actions) this.actionsMap.get(id);
	}

	public String getRenderer() {
		return this.renderer;
	}

	public void setRenderer(String className) {
		this.renderer = className;
	}

	public String getType() {
		if (this.runnerType != null) {
			return this.runnerType;
		}
		for (Variable var : this.variables) {
			if (var.getKey().equals("RUNNER_TYPE")) {
				this.runnerType = var.getValue();
			}
		}
		return this.runnerType;
	}

	public Dimension getDimension() {
		return this.dimension;
	}

	public void setDimension(Dimension d) {
		this.dimension = d;
	}
}