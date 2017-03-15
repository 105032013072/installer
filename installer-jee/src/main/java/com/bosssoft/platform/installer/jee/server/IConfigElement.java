package com.bosssoft.platform.installer.jee.server;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;

public  interface IConfigElement {
	
	public void config(IJEEServer jeeServer) throws JEEServerOperationException;

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException;
}