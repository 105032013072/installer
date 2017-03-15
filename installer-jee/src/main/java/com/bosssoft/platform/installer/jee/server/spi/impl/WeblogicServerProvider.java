package com.bosssoft.platform.installer.jee.server.spi.impl;

import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.IJEEServerEnv;
import com.bosssoft.platform.installer.jee.server.ProductDefination;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.spi.IJEEServerProvider;

public class WeblogicServerProvider implements IJEEServerProvider {
	public static final String SERVER_TYPE = "weblogic";

	public IJEEServer createServer(IJEEServerEnv env) {
		WeblogicServerImpl serverImpl = new WeblogicServerImpl((WeblogicEnv) env);
		return serverImpl;
	}

	public boolean support(ProductDefination productDefination) {
		String version = productDefination.getVersion();
		String name = productDefination.getName();
		if ((name.toLowerCase().equals(SERVER_TYPE)) && ((version.trim().startsWith("9.2")) || (version.trim().startsWith("10.0")))) {
			return true;
		}

		return false;
	}

	public int getPriority() {
		return 1;
	}
}