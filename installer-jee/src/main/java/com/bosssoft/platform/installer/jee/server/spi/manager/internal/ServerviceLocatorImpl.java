package com.bosssoft.platform.installer.jee.server.spi.manager.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ServerviceLocatorImpl implements IServiceLocator {
	private static ServerviceLocatorImpl INSTANCE;
	private static Object lock = new Object();

	public static ServerviceLocatorImpl getInstance() {
		if (INSTANCE == null) {
			synchronized (lock) {
				if (INSTANCE == null) {
					INSTANCE = new ServerviceLocatorImpl();
				}
			}
		}
		return INSTANCE;
	}

	public Object[] getService(Class interfaceClass) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return getService(interfaceClass, classLoader);
	}

	public Object[] getService(Class interfaceClass, ClassLoader classLoader) {
		ServiceLoader loader = ServiceLoader.load(interfaceClass, classLoader);
		Iterator iterator = loader.iterator();
		List list = new ArrayList();
		while (iterator.hasNext()) {
			Object element = iterator.next();
			list.add(element);
		}
		return list.toArray();
	}
}