package com.bosssoft.platform.installer.wizard.tools;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.util.StringUtil;
import com.bosssoft.platform.installer.wizard.util.DBConnectionUtil;

public class AbstractConfigCheck {
	public boolean executeCheck(IContext context) {
		String app_type = context.getStringValue("APP_SERVER_TYPE");
		if (app_type.equalsIgnoreCase("Tomcat")) {
			TomcatConfigCheck tomcat_check = new TomcatConfigCheck();
			return tomcat_check.check(context);
		}
		if (app_type.equalsIgnoreCase("JBoss")) {
			JbossConfigCheck jboss_check = new JbossConfigCheck();
			return jboss_check.check(context);
		}
		if (app_type.equalsIgnoreCase("WebLogic")) {
			WeblogicConfigCheck weblogic_check = new WeblogicConfigCheck();
			return weblogic_check.check(context);
		}
		if (app_type.equalsIgnoreCase("WebSphere")) {
			WebSphereConfigCheck was_check = new WebSphereConfigCheck();
			return was_check.check(context);
		}
		invalidError("APP_SERVER_TYPE");
		return false;
	}

	public boolean validateDBConfig(IContext context) {
		String isDefaultJar = context.getStringValue("DB_IS_DEFAULT_JAR");
		String jdbcUrl = context.getStringValue("DB_URL");
		String user = context.getStringValue("DB_USERNAME");
		String password = context.getStringValue("DB_PASSWORD");
		String jdbcDriverClass = context.getStringValue("DB_DRIVER");
		String driverFiles = null;

		if (Boolean.TRUE.toString().equalsIgnoreCase(isDefaultJar)) {
			driverFiles = context.getStringValue("DB_JDBC_LIBS");
		}

		int rtn = DBConnectionUtil.validateDBConfig(driverFiles, jdbcDriverClass, jdbcUrl, user, password);
		if (rtn == 100) {
			return true;
		}

		return false;
	}

	public boolean assertNull(String[] values) {
		for (int i = 0; i < values.length; i++) {
			if (StringUtil.isNullOrBlank(values[i])) {
				return true;
			}
		}
		return false;
	}

	public void error(String message) {
		System.err.println("ERROR: " + message);
	}

	public void nullError(String key) {
		error("The [" + key + "] must not be null!");
	}

	public void invalidError(String key) {
		error("The [" + key + "] is invalid!");
	}

	public void homeInvalidError(String home, String homeName) {
		error("The " + home + " [" + homeName + "] is invalid!");
	}

	public void inf(String message) {
		System.out.println("INF: " + message);
	}
}