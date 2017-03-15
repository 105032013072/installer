package com.bosssoft.platform.installer.wizard.tools;

import com.bosssoft.platform.installer.core.IContext;

public class WeblogicConfigCheck extends AbstractConfigCheck {
	public boolean check(IContext context) {
		if (assertNull(new String[] { context.getStringValue("AS_WL_BEA_HOME") })) {
			nullError("AS_WL_BEA_HOME");
			return false;
		}
		if (assertNull(new String[] { context.getStringValue("AS_WL_HOME") })) {
			nullError("AS_WL_HOME");
			return false;
		}
		if (assertNull(new String[] { context.getStringValue("AS_WL_DOMAIN_HOME") })) {
			nullError("AS_WL_DOMAIN_HOME");
			return false;
		}

		if (Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_CLUSTER"))) {
			if (assertNull(new String[] { context.getStringValue("CLUSTER_NAME") })) {
				nullError("CLUSTER_NAME");
				return false;
			}

			if (Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_DEPLOY_JMSQUEUE"))) {
				if (!Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_DEPLOY_DATASOURCE"))) {
					if (assertNull(new String[] { context.getStringValue("WEBLOGIC_CLUSTER_JMS_DS_NAME") })) {
						nullError("WEBLOGIC_CLUSTER_JMS_DS_NAME");
						return false;
					}
					context.setValue("DB_DS_JNDI_NAME", context.getStringValue("WEBLOGIC_CLUSTER_JMS_DS_NAME"));
				}
			}

		}

		if (assertNull(new String[] { context.getStringValue("AS_WL_TARGET_SERVER") })) {
			nullError("AS_WL_TARGET_SERVER");
			return false;
		}

		return true;
	}
}