package com.bosssoft.platform.installer.core.util;

import java.io.PrintStream;

public class ReflectUtil {
	public static Object newInstanceBy(String clazzName) {
		Object instance = null;
		try {
			Class clazz = Class.forName(clazzName.trim());
			instance = clazz.newInstance();
		} catch (ClassNotFoundException cnfe) {
			System.out.println("the not found class is: " + clazzName);
			cnfe.printStackTrace();
		} catch (IllegalAccessException localIllegalAccessException) {
		} catch (InstantiationException localInstantiationException) {
		}
		return instance;
	}

	public static Object newInstanceBy(Class clazz) {
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (IllegalAccessException localIllegalAccessException) {
		} catch (InstantiationException localInstantiationException) {
		}
		return instance;
	}
}