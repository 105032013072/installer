package com.bosssoft.platform.installer.wizard.action;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.config.AbstractJMSConfig;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossEnv;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossJMSConfig;
import com.bosssoft.platform.installer.jee.server.impl.jboss.JBossServerImpl;

public class CreateJBossJmsQueue implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String jbossHome = parameters.get("JBOSS_HOME").toString();
		String serverName = parameters.get("SERVER_NAME").toString();
		String queueNames = parameters.get("QUEUE_NAME").toString();
		String jndis = parameters.get("QUEUE_JNDI").toString();

		AbstractJMSConfig.NameJndiModel[] models = construct(queueNames, jndis);

		JBossJMSConfig config = new JBossJMSConfig();

		config.setQueueModels(models);

		JBossEnv env = new JBossEnv(jbossHome, serverName);

		JBossServerImpl serverImpl = new JBossServerImpl(env);
		try {
			serverImpl.config(config);
		} catch (JEEServerOperationException e) {
			throw new InstallException(e.getMessage(), e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
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