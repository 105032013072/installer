package com.bosssoft.platform.installer.jee.server.spi;

import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.IJEEServerEnv;
import com.bosssoft.platform.installer.jee.server.ProductDefination;

public interface IJEEServerProvider {
	
	public boolean support(ProductDefination productDefination);

	public IJEEServer createServer(IJEEServerEnv jeeServerEnv);

	public int getPriority();
}