package com.bosssoft.platform.installer.jee.server.spi.impl;

import java.io.IOException;

import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.IJEEServerEnv;
import com.bosssoft.platform.installer.jee.server.ProductDefination;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;
import com.bosssoft.platform.installer.jee.server.spi.IJEEServerProvider;

public class TomcatServerProvider implements IJEEServerProvider {
	public static final String SERVER_TYPE = "tomcat";
	public static final int PRIORITY = 1;

	public IJEEServer createServer(IJEEServerEnv env) {
		try {
			return new TomcatServerImpl((TomcatEnv) env);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean support(ProductDefination productDefination) {
		String productName = productDefination.getName();
		String version = productDefination.getVersion();
		if ((SERVER_TYPE.equals(productName.toLowerCase())) && (version.startsWith("5.5."))) {
			return true;
		}

		return false;
	}

	public int getPriority() {
		return 1;
	}
}