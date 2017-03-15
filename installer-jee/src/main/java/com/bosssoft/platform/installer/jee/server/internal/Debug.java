package com.bosssoft.platform.installer.jee.server.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Debug {
	private boolean debugEnable = false;

	public static Debug getDebug() {
		return new Debug();
	}

	public void debug(String msg) {
		if (this.debugEnable)
			System.out.println(msg);
	}

	public void debug(String msg, Throwable t) {
		if (this.debugEnable) {
			System.out.println(msg);
			try {
				printStackTrace(t);
			} catch (Throwable localThrowable) {
			}
		}
	}

	private static void printStackTrace(Throwable t) {
		t.printStackTrace(System.out);

		Method[] methods = t.getClass().getMethods();

		int size = methods.length;
		Class throwable = Throwable.class;

		for (int i = 0; i < size; i++) {
			Method method = methods[i];
			if ((Modifier.isPublic(method.getModifiers())) && (method.getName().startsWith("get")) && (throwable.isAssignableFrom(method.getReturnType()))
					&& (method.getParameterTypes().length == 0))
				try {
					Throwable nested = (Throwable) method.invoke(t, null);
					if ((nested != null) && (nested != t)) {
						System.out.println("Nested Exception:");
						printStackTrace(nested);
					}
				} catch (IllegalAccessException localIllegalAccessException) {
				} catch (InvocationTargetException localInvocationTargetException) {
				}
		}
	}

	public boolean isDebugEnable() {
		return this.debugEnable;
	}

	public void setDebugEnable(boolean debugEnable) {
		this.debugEnable = debugEnable;
	}
}