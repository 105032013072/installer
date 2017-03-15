package com.bosssoft.platform.installer.jee.server.impl.websphere;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.config.AbstractJMSConfig;

public class WebshpereJMSConfig extends AbstractJMSConfig {
	private String destName;
	private String mdbName;
	private String mdbjndi;
	private String jmsBusName = "eos_jms_bus";
	private String queueFactoryName = "EOS_QUEUE_FACTORY";

	@Deprecated
	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profilesHome = env.getProfilesHome();
		String profileName = env.getProfileName();
		String username = env.getLoginName();
		String password = env.getPassword();
		String nodeName = env.getNodeName();

		AbstractJMSConfig.NameJndiModel[] queueModels = getQueueModels();
		for (int i = 0; i < queueModels.length; i++) {
			AbstractJMSConfig.NameJndiModel model = queueModels[i];
			String jmsQueueJndi = model.getJndi();
			String queueName = model.getName();

			addQueue(profilesHome, profileName, username, password, nodeName, jmsQueueJndi, queueName);
		}
	}

	public void configJMS(IJEEServer jeeServer) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();

		String nodeName = env.getNodeName();
		String serverName = env.getServerName();

		String externalConfigDir = env.getProperty("EXTERNAL_CONFIG_DIR").toString();

		String sibPort = getWASPortDef("SIB_ENDPOINT_ADDRESS", profileHome);
		String[] jaclParameters = { cellName, nodeName, serverName, externalConfigDir, this.queueFactoryName, this.jmsBusName, sibPort };

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}

		File scriptFile = new File(dir, "config_standalone_jms.jacl");
		String scriptFilePath = scriptFile.getAbsolutePath();

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFilePath, jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("deploy the queue was failed!");
	}

	public void configClusterJMS(IJEEServer jeeServer, String appName) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();

		String clusterName = env.getClusterName();

		String[] jaclParameters = { cellName, clusterName, appName };

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}

		File scriptFile = new File(dir, "config_cluster_jms.jacl");
		String scriptFilePath = scriptFile.getAbsolutePath();

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFilePath, jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("deploy the queue was failed!");
	}

	private static String getWASPortDef(String key, String profileHome) {
		Properties prop = null;
		String portDefFile = profileHome + File.separator + "properties" + File.separator + "portdef.props";
		prop = new Properties();
		try {
			prop.load(new FileInputStream(new File(portDefFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop.getProperty(key);
	}

	private void addQueue(String profilesHome, String profileName, String username, String password, String nodeName, String jmsQueueJndi, String queueName)
			throws JEEServerOperationException {
		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}

		File scriptFile = new File(dir, "config_standalone_jms.jacl");
		String scriptFilePath = scriptFile.getAbsolutePath();

		String[] jaclParameters = { nodeName, profileName, this.destName, queueName, jmsQueueJndi, this.mdbName, this.mdbjndi };

		int result = WASScriptTool.execScript(profilesHome, username, password, scriptFilePath, jaclParameters);
		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("config JMS Env failed!" + queueName);
	}

	public void configClusterQueue(IJEEServer jeeServer, String appName) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();

		String clusterName = env.getClusterName();

		String[] jaclParameters = { cellName, clusterName, appName };

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}

		File scriptFile = new File(dir, "config_cluster_queue.jacl");
		String scriptFilePath = scriptFile.getAbsolutePath();

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFilePath, jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("config the queue was failed!");
	}

	public void configStandaloneQueue(IJEEServer jeeServer, String appName) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String nodeName = env.getNodeName();
		String serverName = env.getServerName();

		String[] jaclParameters = { nodeName, serverName, appName };

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}

		File scriptFile = new File(dir, "create_queue.jacl");
		String scriptFilePath = scriptFile.getAbsolutePath();

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFilePath, jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("config the queue was failed!");
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profilesHome = env.getProfilesHome();
		String profileName = env.getProfileName();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String nodeName = env.getNodeName();

		AbstractJMSConfig.NameJndiModel[] queueModel = getQueueModels();
		for (int i = 0; i < queueModel.length; i++) {
			AbstractJMSConfig.NameJndiModel model = queueModel[i];
			String queueName = model.getName();
			removeQueue(profilesHome, profileName, username, password, cellName, nodeName, queueName);
		}
	}

	private void removeQueue(String profilesHome, String profileName, String username, String password, String cellName, String nodeName, String queueName)
			throws JEEServerOperationException {
		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}

		File scriptFile = new File(dir, "delete_queue.jacl");
		String scriptFilePath = scriptFile.getAbsolutePath();

		String[] jaclParameters = { cellName, nodeName, profileName, queueName, this.mdbName };

		int result = WASScriptTool.execScript(profilesHome, username, password, scriptFilePath, jaclParameters);
		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("undeploy the queue was failed!" + queueName);
	}

	public String getDestName() {
		return this.destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public String getMdbjndi() {
		return this.mdbjndi;
	}

	public void setMdbjndi(String mdbjndi) {
		this.mdbjndi = mdbjndi;
	}

	public String getMdbName() {
		return this.mdbName;
	}

	public void setMdbName(String mdbName) {
		this.mdbName = mdbName;
	}
}