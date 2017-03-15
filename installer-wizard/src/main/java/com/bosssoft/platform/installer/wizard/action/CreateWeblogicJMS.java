package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.config.AbstractJMSConfig;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicJMSServerConfig;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

public class CreateWeblogicJMS implements IAction {
	private static final String STORE_DIRECTORY = "STORE_DIRECTORY";
	private static final String FILE_STORE_NAME = "FILE_STORE_NAME";
	private static final String TARGET_SERVER = "TARGET_SERVER";
	private static final String TOPIC_JNDIS = "TOPIC_JNDIS";
	private static final String TOPIC_NAMES = "TOPIC_NAMES";
	private static final String QUEUE_JNDIS = "QUEUE_JNDIS";
	private static final String QUEUE_NAMES = "QUEUE_NAMES";
	private static final String CONNECTION_FACTORY_JNDI = "CONNECTION_FACTORY_JNDI";
	private static final String CONNECTION_FACTORY_NAME = "CONNECTION_FACTORY_NAME";
	private static final String SUB_DEPLOYMENT_NAME = "SUB_DEPLOYMENT_NAME";
	private static final String JMS_MODULE_NAME = "JMS_MODULE_NAME";
	private static final String JMS_SERVER_NAME = "JMS_SERVER_NAME";
	private static final String JDBC_STORE_NAME = "JDBC_STORE_NAME";
	private static final String DATA_SOURCE_NAME = "DATA_SOURCE_NAME";
	private static final String TABLE_PREFIX = "TABLE_PREFIX";

	public void execute(IContext context, Map parameters) throws InstallException {
		String host = context.getStringValue("USER_IP");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");
		String userName = context.getStringValue("AS_WL_USERNAME");
		String password = context.getStringValue("AS_WL_PASSWORD");
		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		WeblogicEnv env = new WeblogicEnv(beaHome, wlHome, domainHome, userName, password, host, port);
		WeblogicServerImpl serverImpl = new WeblogicServerImpl(env);

		WeblogicJMSServerConfig config = new WeblogicJMSServerConfig();

		String jmsServerName = getStringValue(parameters, "JMS_SERVER_NAME");
		config.setJmsServerName(jmsServerName);

		String jmsModuleName = getStringValue(parameters, "JMS_MODULE_NAME");
		config.setJmsServerResourceName(jmsModuleName);

		String jmsSubDeploymentName = getStringValue(parameters, "SUB_DEPLOYMENT_NAME");
		config.setJmsSubDeploymentName(jmsSubDeploymentName);

		String connFactoryName = getStringValue(parameters, "CONNECTION_FACTORY_NAME");
		String connFactoryJndi = getStringValue(parameters, "CONNECTION_FACTORY_JNDI");
		AbstractJMSConfig.NameJndiModel[] models = construct(connFactoryName, connFactoryJndi);
		config.setConnFactories(models);

		String jmsQueueName = getStringValue(parameters, "QUEUE_NAMES");
		String jmsQueueJndi = getStringValue(parameters, "QUEUE_JNDIS");
		models = construct(jmsQueueName, jmsQueueJndi);
		config.setQueueModels(models);

		String jmsTopicName = getStringValue(parameters, "TOPIC_NAMES");
		String jmsTopicJndi = getStringValue(parameters, "TOPIC_JNDIS");
		models = construct(jmsTopicName, jmsTopicJndi);
		config.setTopics(models);

		String fileStoreName = getStringValue(parameters, "FILE_STORE_NAME");
		String storeDirectory = getStringValue(parameters, "STORE_DIRECTORY");
		config.setStoreName(fileStoreName);
		config.setStoreDirectory(storeDirectory);

		String jdbcStoreName = getStringValue(parameters, "JDBC_STORE_NAME");
		String dsName = getStringValue(parameters, "DATA_SOURCE_NAME");
		String tablePrefix = getStringValue(parameters, "TABLE_PREFIX");
		config.setStoreName(jdbcStoreName);
		config.setDataSourceName(dsName);
		config.setTablePrefix(tablePrefix);

		String targetServer = getStringValue(parameters, "TARGET_SERVER");
		TargetModelImpl target = new TargetModelImpl(targetServer);
		String isCluster = context.getStringValue("IS_CLUSTER");
		if ((isCluster != null) && ("true".equalsIgnoreCase(isCluster))) {
			target.setCluster(true);
		}

		config.setTarget(target);
		try {
			serverImpl.config(config);
		} catch (JEEServerOperationException e) {
			throw new InstallException(e.getMessage(), e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private String getStringValue(Map paramters, String key) {
		String result = "";
		Object value = paramters.get(key);
		if (value == null) {
			result = "";
		}
		if ((value instanceof String))
			result = (String) value;
		else {
			result = value == null ? "" : value.toString();
		}
		if (StringUtils.isNotEmpty(result)) {
			result = ExpressionParser.parseString(result);
		}
		return result;
	}

	private AbstractJMSConfig.NameJndiModel[] construct(String names, String jndis) {
		if (StringUtils.isEmpty(names)) {
			return new AbstractJMSConfig.NameJndiModel[0];
		}
		String[] _names = names.split("[,]");
		String[] _jndis = jndis.split("[,]");
		AbstractJMSConfig.NameJndiModel[] nameJndiModels = new AbstractJMSConfig.NameJndiModel[_names.length];
		for (int i = 0; i < _names.length; i++) {
			nameJndiModels[i] = new AbstractJMSConfig.NameJndiModel(_names[i], _jndis[i]);
		}
		return nameJndiModels;
	}
}