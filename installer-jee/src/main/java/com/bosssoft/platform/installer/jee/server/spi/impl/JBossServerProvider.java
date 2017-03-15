package com.bosssoft.platform.installer.jee.server.spi.impl;

import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.IJEEServerEnv;
import com.bosssoft.platform.installer.jee.server.ProductDefination;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossEnv;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossServerImpl;
import com.bosssoft.platform.installer.jee.server.spi.IJEEServerProvider;

public class JBossServerProvider implements IJEEServerProvider {
	public static final int PRORITY = 1;
	public static final String SERVER_TYPE = "jboss";

	public IJEEServer createServer(IJEEServerEnv env) {
		JBossEnv jBossEnv = (JBossEnv) env;
		return new JBossServerImpl(jBossEnv);
	}

	public boolean support(ProductDefination productDefination) {
		String name = productDefination.getName();
		String version = productDefination.getVersion();
		if ((name == null) || (version == null)) {
			return false;
		}
		if (name.toLowerCase().equals(SERVER_TYPE)) {
			if (version.startsWith("4.0")) {
				return true;
			}

		}

		return false;
	}

	public int getPriority() {
		return 1;
	}
}