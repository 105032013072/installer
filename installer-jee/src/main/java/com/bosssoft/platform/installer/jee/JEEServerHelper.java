package com.bosssoft.platform.installer.jee;

import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.IJEEServerEnv;
import com.bosssoft.platform.installer.jee.server.ProductDefination;
import com.bosssoft.platform.installer.jee.server.spi.IJEEServerProvider;
import com.bosssoft.platform.installer.jee.server.spi.manager.internal.IServiceLocator;
import com.bosssoft.platform.installer.jee.server.spi.manager.internal.ServiceLocatorHelper;

public class JEEServerHelper {
	public static IJEEServer createJEEServer(ProductDefination defination, IJEEServerEnv env) {
		IJEEServerProvider provider = getServerProvider(defination);
		if (provider != null) {
			return provider.createServer(env);
		}
		throw new UnSupportServerException("Unsupport JEEServer:" + defination);
	}

	public static boolean support(ProductDefination defination) {
		IJEEServerProvider provider = getServerProvider(defination);
		return provider != null;
	}

	private static IJEEServerProvider getServerProvider(ProductDefination defination) {
		IServiceLocator locator = ServiceLocatorHelper.getServiceLocator();
		Object[] providers = locator.getService(IJEEServerProvider.class);

		sortProviders(providers);

		for (int i = 0; i < providers.length; i++) {
			IJEEServerProvider provider = (IJEEServerProvider) providers[i];
			if (provider.support(defination)) {
				return provider;
			}
		}
		return null;
	}

	private static void sortProviders(Object[] providers) {
		for (int i = 0; i < providers.length; i++) {
			for (int j = i; (j > 0) && (((IJEEServerProvider) providers[j]).getPriority() > ((IJEEServerProvider) providers[(j - 1)]).getPriority()); j--) {
				IJEEServerProvider temp = (IJEEServerProvider) providers[j];
				providers[j] = providers[(j - 1)];
				providers[(j - 1)] = temp;
			}
		}
	}
}