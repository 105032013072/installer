package com.bosssoft.platform.installer.jee.server;

import java.io.File;
import java.util.Properties;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;

public interface IJEEServer {
	public void start(boolean cluster, String _jvmArgs) throws JEEServerOperationException;

	public void stop(boolean cluster) throws JEEServerOperationException;

	public IApplicationModel[] getApplications() throws JEEServerOperationException;

	public void deploy(String appName, File earwar, ITargetModel target, Properties properties) throws JEEServerOperationException;

	public void undeploy(String paramString) throws JEEServerOperationException;

	public IJEEServerEnv getEnv();

	public void config(IConfigElement configElement) throws JEEServerOperationException;

	public void unconfig(IConfigElement configElement) throws JEEServerOperationException;
}