package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import java.io.File;
import java.io.IOException;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.config.AbstractJMSConfig;
import com.bosssoft.platform.installer.jee.server.internal.util.Util;

public class WeblogicJMSServerConfig extends AbstractJMSConfig {
	private String jmsServerName;
	private String jmsServerResourceName;
	private String jmsSubDeploymentName;
	private boolean useJDBCStore = false;
	private String dataSourceName;
	private String storeName;
	private String storeDirectory;
	private String tablePrefix;

	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		WeblogicEnv env = (WeblogicEnv) jeeServer.getEnv();

		run(env, "addJMS");
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
		WeblogicEnv env = (WeblogicEnv) jeeServer.getEnv();
		run(env, "removeJMS");
	}

	private void run(WeblogicEnv env, String operation) throws JEEServerOperationException {
		WlstProperties.JMSProperty properties = getProperties(env, operation);

		File tempDir = Util.getTempDir();
		File propertieFile = new File(tempDir, "jmsProperty.properties");
		String path = propertieFile.getAbsolutePath();
		try {
			properties.save(path);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}

		String weblogicHome = env.getWeblogicHome();
		File wlstCMD = WeblogicScrptTool.getWlstCmd(weblogicHome);
		File pythonFile = WlstProperties.getJmsPython();
		RunWlstCmdArgs arg = new RunWlstCmdArgs(wlstCMD, pythonFile, propertieFile);
		try {
			WeblogicScrptTool.runWLST(arg);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
	}

	private WlstProperties.JMSProperty getProperties(WeblogicEnv env, String operation) {
		String userName = env.getLoginName();
		String password = env.getPassword();
		String host = env.getHost();
		String port = env.getPort();
		String url = "t3://" + host + ":" + port;

		AbstractJMSConfig.NameJndiModel[] connModels = getConnFactories();
		StringBuilder connName = new StringBuilder();
		StringBuilder connJNDI = new StringBuilder();
		appendNameJndi(connModels, connName, connJNDI);

		AbstractJMSConfig.NameJndiModel[] queueModels = getQueueModels();
		StringBuilder queueName = new StringBuilder();
		StringBuilder queueJNDI = new StringBuilder();
		appendNameJndi(queueModels, queueName, queueJNDI);

		AbstractJMSConfig.NameJndiModel[] topicModels = getTopics();
		StringBuilder topicName = new StringBuilder();
		StringBuilder topicJNDI = new StringBuilder();
		appendNameJndi(topicModels, topicName, topicJNDI);

		String resultFile = WeblogicScrptTool.getResultLogFile().getAbsolutePath();

		WlstProperties.JMSProperty property = new WlstProperties.JMSProperty();
		property._operations = operation;
		property.adminServerURL = url;
		property._storeName = this.storeName;
		property.storeDirectory = this.storeDirectory;
		property.useJDBCStore = (this.useJDBCStore ? "true" : "false");
		property._dataSourceName = this.dataSourceName;
		property.tablePrefix = this.tablePrefix;

		property.jmsConnectionFactoryJndi = connName.toString();
		property.jmsConnectionFactoryName = connJNDI.toString();
		property.jmsModuleName = getJmsServerResourceName();
		property.jmsQueueJndi = queueJNDI.toString();
		property.jmsQueueName = queueName.toString();
		property.jmsServerName = getJmsServerName();
		property.jmsSubDeploymentName = getJmsSubDeploymentName();
		property.jmsTopicJndi = topicJNDI.toString();
		property.jmsTopicName = topicName.toString();
		property.userName = userName;
		property.passWord = password;
		property.resultFile = resultFile;
		property._domainHome = env.getDomainHome();

		StringBuilder servers = new StringBuilder();
		StringBuilder clusters = new StringBuilder();
		ITargetModel target = getTarget();
		addTargets(servers, clusters, target);

		property.targetServers = servers.toString();
		property.targetClusters = clusters.toString();
		return property;
	}

	private void appendNameJndi(AbstractJMSConfig.NameJndiModel[] models, StringBuilder name, StringBuilder jndi) {
		if ((models == null) || (models.length <= 0)) {
			return;
		}
		for (int i = 0; i < models.length; i++) {
			AbstractJMSConfig.NameJndiModel model = models[i];
			name.append(model.getName()).append(',');
			jndi.append(model.getJndi()).append(',');
		}
		name.deleteCharAt(name.length() - 1);
		jndi.deleteCharAt(jndi.length() - 1);
	}

	private void addTargets(StringBuilder servers, StringBuilder clusters, ITargetModel target) {
		boolean isCluster = target.isCluster();
		String name = target.getName();
		if (isCluster) {
			if ((name != null) && (!name.trim().equals(""))) {
				if (clusters.length() > 0) {
					clusters.append(',');
				}
				clusters.append(name);
			}
		} else if ((name != null) && (!name.trim().equals(""))) {
			if (clusters.length() > 0) {
				clusters.append(',');
			}
			servers.append(name);
		}
		ITargetModel[] children = target.getChildren();
		for (int i = 0; i < children.length; i++) {
			ITargetModel model = children[i];
			addTargets(servers, clusters, model);
		}
	}

	public String getStoreDirectory() {
		return this.storeDirectory;
	}

	public void setStoreDirectory(String storeDirectory) {
		this.storeDirectory = storeDirectory;
	}

	public String getStoreName() {
		return this.storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getJmsServerName() {
		return this.jmsServerName;
	}

	public void setJmsServerName(String jmsServerName) {
		this.jmsServerName = jmsServerName;
	}

	public String getJmsServerResourceName() {
		return this.jmsServerResourceName;
	}

	public void setJmsServerResourceName(String jmsServerResourceName) {
		this.jmsServerResourceName = jmsServerResourceName;
	}

	public String getJmsSubDeploymentName() {
		return this.jmsSubDeploymentName;
	}

	public void setJmsSubDeploymentName(String jmsSubDeploymentName) {
		this.jmsSubDeploymentName = jmsSubDeploymentName;
	}

	public boolean isUseJDBCStore() {
		return this.useJDBCStore;
	}

	public void setUseJDBCStore(boolean useJDBCStore) {
		this.useJDBCStore = useJDBCStore;
	}

	public String getDataSourceName() {
		return this.dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getTablePrefix() {
		return this.tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}
}