package com.bosssoft.platform.installer.jee.server;

public interface IJEEServerEnv {
	
	public Object getProperty(String key);

	public void setProperty(String key, Object value);
}