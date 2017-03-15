package com.bosssoft.platform.installer.core.context;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.impl.context.InstallContext;

public class DefaultContextFactory implements IContextFactory {
	public IContext createContext() {
		return new InstallContext();
	}
}