package com.bosssoft.platform.installer.jee.server;

import java.io.File;
import java.util.Properties;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;

public abstract class AbstractJEEServer implements IJEEServer {
	private IJEEServerEnv env;

	public AbstractJEEServer() {
	}

	public AbstractJEEServer(IJEEServerEnv env) {
		this.env = env;
	}

	public void start(boolean cluster, String _jvmArgs) throws JEEServerOperationException {
		throw new UnsupportedOperationException();
	}

	public void stop(boolean cluster) throws JEEServerOperationException {
		throw new UnsupportedOperationException();
	}

	public void deploy(String appName, File earwar, ITargetModel target, Properties properties) throws JEEServerOperationException {
		throw new UnsupportedOperationException();
	}

	public IApplicationModel[] getApplications() throws JEEServerOperationException {
		throw new UnsupportedOperationException();
	}

	public void undeploy(String appName) throws JEEServerOperationException {
		throw new UnsupportedOperationException();
	}

	public IJEEServerEnv getEnv() {
		return this.env;
	}

	public void setEnv(IJEEServerEnv env) {
		this.env = env;
	}

	public void config(IConfigElement configElement) throws JEEServerOperationException {
		configElement.config(this);
	}

	public void unconfig(IConfigElement configElement) throws JEEServerOperationException {
		configElement.unconfig(this);
	}
}