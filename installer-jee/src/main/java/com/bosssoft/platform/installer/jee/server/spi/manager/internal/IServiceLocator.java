package com.bosssoft.platform.installer.jee.server.spi.manager.internal;

public interface IServiceLocator {
	
	public Object[] getService(Class serviceClass);

	public Object[] getService(Class serviceClass, ClassLoader classLoadr);
}