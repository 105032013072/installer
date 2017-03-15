package com.bosssoft.platform.installer.core.cfg;

import java.util.HashMap;
import java.util.Map;

/** @deprecated */
public class Action {
	private String name = null;
	private String clazzName = null;
	private Map parameters = new HashMap();
	private int scale = 1;
	public static final String STRATEGY_RETRY = "retry";
	public static final String STRATEGY_QUIT = "quit";
	public static final String STRATEGY_IGNORE = "ignore";
	private String strategy = "retry";

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

	public String toString() {
		return getName();
	}

	public int getScale() {
		return this.scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getStrategy() {
		return this.strategy;
	}

	public void setStrategy(String s) {
		s = s.toLowerCase();
		if ((s.equals("retry")) || (s.equals("quit")) || (s.equals("ignore")))
			this.strategy = s;
		else
			throw new IllegalArgumentException("Can not identify the strategy：" + s);
	}
}