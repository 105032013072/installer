package com.bosssoft.platform.installer.jee.server.impl.websphere;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.AbstractJEEServer;
import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.internal.ApplicationModelImpl;

public class WebSphereServerImpl extends AbstractJEEServer {
	public WebSphereServerImpl(WebsphereEnv env) {
		super(env);
	}

	public void start(boolean cluster, String _jvmArgs) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profilesHome = env.getProfilesHome();
		String profileName = env.getProfileName();
		int result = -1;
		if (cluster)
			result = WASScriptTool.startNDServer(profilesHome, profileName);
		else {
			result = WASScriptTool.startServer(profilesHome, profileName);
		}
		if (result == WASScriptTool.EXEC_FAIL)
			throw new JEEServerOperationException("The websphere server was started failed!");
	}

	public void stop(boolean cluster) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profilesHome = env.getProfilesHome();
		String profileName = env.getProfileName();
		String username = env.getLoginName();
		String password = env.getPassword();

		int result = -1;
		if (cluster)
			result = WASScriptTool.stopNDServer(profilesHome, profileName, username, password);
		else {
			result = WASScriptTool.stopServer(profilesHome, profileName, username, password);
		}
		if (result == WASScriptTool.EXEC_FAIL)
			throw new JEEServerOperationException("The websphere server was stoped failed!");
	}

	public void deploy(String appName, File earwar, ITargetModel target, Properties properties) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profileHome = env.getProfilesHome();
		String serverName = env.getServerName();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String nodeName = env.getNodeName();
		String externalConfigDir = env.getProperty("EXTERNAL_CONFIG_DIR").toString();
		String isCreateJMS = env.getProperty("IS_CREATE_JMS").toString();
		String deployEJB = env.getDeployEJB();
		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}
		File scriptFile = new File(dir, "deploy_app.jacl");

		String[] jaclParameters = { cellName, nodeName, serverName, externalConfigDir.replace('\\', '/'), earwar.getAbsolutePath().replace('\\', '/'), appName, deployEJB,
				isCreateJMS };

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("deploy the application was failed!" + appName);
	}

	public void deploy2Cluster(String appName, File earwar, ITargetModel target) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();

		String clusterName = env.getClusterName();

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}
		File scriptFile = new File(dir, "deploy_cluster_app.jacl");

		String[] jaclParameters = { cellName, clusterName, appName, "default_host", earwar.getAbsolutePath().replace('\\', '/') };

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("deploy the application was failed!" + appName);
	}

	public IApplicationModel[] getApplications() throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profileHome = env.getProfilesHome();
		String cellName = env.getCellName();
		String profileName = env.getProfileName();

		StringBuilder strBuilder = new StringBuilder(profileHome);
		strBuilder.append(File.separatorChar).append("config").append(File.separatorChar).append("cells");
		strBuilder.append(File.separatorChar).append(cellName).append(File.separatorChar).append("applications");
		File appDir = new File(strBuilder.toString());

		if (!appDir.exists()) {
			return new IApplicationModel[0];
		}
		File[] appFiles = appDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName().toLowerCase();
				if ((name.endsWith(".ear")) || (name.endsWith(".war"))) {
					return true;
				}
				return false;
			}
		});
		if (appFiles == null) {
			return new IApplicationModel[0];
		}

		List list = new ArrayList(appFiles.length);
		for (int i = 0; i < appFiles.length; i++) {
			File file = appFiles[i];
			String appName = file.getName();
			appName = appName.substring(0, appName.length() - 4);
			ApplicationModelImpl modelImpl = new ApplicationModelImpl(appName);
			list.add(modelImpl);
		}
		return (IApplicationModel[]) list.toArray(new IApplicationModel[list.size()]);
	}

	public void setJVMParameter(String paramName, String paramValue) throws JEEServerOperationException {
		setParameter(paramName, paramValue, "set_jvm.jacl");
	}

	private void setParameter(String paramName, String paramValue, String scriptFileName) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profileHome = env.getProfilesHome();
		String serverName = env.getServerName();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String nodeName = env.getNodeName();

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
		File scriptFile = new File(dir, scriptFileName);

		String[] jaclParameters = { cellName, nodeName, serverName, paramName, paramValue };

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("set JVM Parameter failed!" + paramName);
	}

	public void setWebContainerParameter(String paramName, String paramValue) throws JEEServerOperationException {
		setParameter(paramName, paramValue, "set_webcontainer_param.jacl");
	}

	public void setClusterJVMParameter(String paramName, String paramValue) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String clusterName = env.getClusterName();

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
		File scriptFile = new File(dir, "set_cluster_jvm.jacl");

		String[] jaclParameters = { cellName, clusterName, paramName, paramValue };

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("set Cluster JVM Parameter failed!" + paramName);
	}

	public void undeploy(String appName) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) getEnv();
		String profilesHome = env.getProfilesHome();
		String profileName = env.getProfileName();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String nodeName = env.getNodeName();

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}
		File scriptFile = new File(dir, "app_uninstall.jacl");

		String[] jaclParameters = { cellName, nodeName, profileName, appName };

		int result = WASScriptTool.execScript(profilesHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);
		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("unDeploy the application was failed!" + appName);
	}
}