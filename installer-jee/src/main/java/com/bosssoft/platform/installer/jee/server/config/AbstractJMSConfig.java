package com.bosssoft.platform.installer.jee.server.config;

import com.bosssoft.platform.installer.jee.server.IConfigElement;
import com.bosssoft.platform.installer.jee.server.ITargetModel;

public abstract class AbstractJMSConfig implements IConfigElement {
	private ITargetModel target;
	private NameJndiModel[] queueModels;
	private NameJndiModel[] connFactories;
	private NameJndiModel[] topics;

	public NameJndiModel[] getConnFactories() {
		return this.connFactories;
	}

	public void setConnFactories(NameJndiModel[] connFactories) {
		this.connFactories = connFactories;
	}

	public NameJndiModel[] getQueueModels() {
		return this.queueModels;
	}

	public void setQueueModels(NameJndiModel[] queueModels) {
		this.queueModels = queueModels;
	}

	public NameJndiModel[] getTopics() {
		return this.topics;
	}

	public void setTopics(NameJndiModel[] topics) {
		this.topics = topics;
	}

	public ITargetModel getTarget() {
		return this.target;
	}

	public void setTarget(ITargetModel target) {
		this.target = target;
	}

	public static class NameJndiModel {
		private String name;
		private String jndi;

		public NameJndiModel() {
		}

		public NameJndiModel(String name, String jndi) {
			this.name = name;
			this.jndi = jndi;
		}

		public String getJndi() {
			return this.jndi;
		}

		public void setJndi(String jndi) {
			this.jndi = jndi;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}