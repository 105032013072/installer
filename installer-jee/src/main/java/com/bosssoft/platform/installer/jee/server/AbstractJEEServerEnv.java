package com.bosssoft.platform.installer.jee.server;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractJEEServerEnv implements IJEEServerEnv {
	private Map<String,Object> properites = new HashMap<String,Object>();

	public Object getProperty(String key) {
		return this.properites.get(key);
	}

	public void setProperty(String key, Object value) {
		this.properites.put(key, value);
	}
}