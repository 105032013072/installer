package com.bosssoft.platform.installer.core.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosssoft.platform.installer.core.action.GroupAction;
import com.bosssoft.platform.installer.core.action.IAction;

public class Step {
	private static final String DEFAULT_CONTROLLPANEL_CLASSNAME = "com.bosssoft.platform.installer.core.gui.DefaultControlPanel";
	private String id = null;

	private String desc = null;

	private boolean reflesh = false;

	private String installPanelClassName = null;

	private String controlPanelClassName = DEFAULT_CONTROLLPANEL_CLASSNAME;

	private String interceptorClassName = null;

	private Map<String,String> branches = new HashMap<String,String>();

	private boolean auto = false;

	private String defaultNextStepID = null;

	private String nextStepDiscriminator = null;

	private String callerID = null;

	private boolean hasGUI = false;

	private boolean isRender = true;

	private List<IAction> actions = new ArrayList<IAction>();

	public String getSetupPanelClassName() {
		return this.installPanelClassName;
	}

	public void setSetupPanelClassName(String clazzName) {
		this.installPanelClassName = clazzName;
	}

	public String getControlPanelClassName() {
		if (this.controlPanelClassName == null)
			this.controlPanelClassName = DEFAULT_CONTROLLPANEL_CLASSNAME;
		return this.controlPanelClassName;
	}

	public void setControllPanelClassName(String clazzName) {
		this.controlPanelClassName = clazzName;
	}

	public String getInterceptorClassName() {
		return this.interceptorClassName;
	}

	public void setInterceptorClassName(String clazzName) {
		this.interceptorClassName = clazzName;
	}

	public String getNextTaskID(String branch) {
		if (this.branches.containsKey(branch))
			return this.branches.get(branch).toString();
		return null;
	}

	public void setNextTaskID(String index, String taskID) {
		this.branches.put(index, taskID);
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public boolean isAuto() {
		return this.auto;
	}

	public void setAuto(boolean b) {
		this.auto = b;
	}

	public void setCallerID(String caller) {
		this.callerID = caller;
	}

	public String getCallerID() {
		return this.callerID;
	}

	public String getDefaultNextStepID() {
		return this.defaultNextStepID;
	}

	public void setDefaultNextStepID(String nextID) {
		this.defaultNextStepID = nextID;
	}

	public String getNextStepDiscriminator() {
		return this.nextStepDiscriminator;
	}

	public void setNextStepDiscriminator(String discriminator) {
		this.nextStepDiscriminator = discriminator;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void addAction(IAction action) {
		this.actions.add(action);
	}

	public void addActionsRef(String refID) {
		this.actions.add(new GroupAction(refID));
	}

	public List<IAction> getActions() {
		return this.actions;
	}

	public boolean isRender() {
		return this.isRender;
	}

	public void setRender(boolean b) {
		this.isRender = b;
	}

	public String toString() {
		return getID();
	}

	public boolean getReflesh() {
		return this.reflesh;
	}

	public void setReflesh(boolean b) {
		this.reflesh = b;
	}

	public boolean hasGUI() {
		return this.hasGUI;
	}

	public void setHasGUI(boolean b) {
		this.hasGUI = b;
	}
}