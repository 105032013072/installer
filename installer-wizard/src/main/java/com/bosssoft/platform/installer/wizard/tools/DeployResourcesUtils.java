package com.bosssoft.platform.installer.wizard.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.context.DefaultContextFactory;
import com.bosssoft.platform.installer.core.option.DeployDef;
import com.bosssoft.platform.installer.core.option.DeployDefLoader;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.util.ActionsUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.wizard.action.CheckDataSourceExistAction;
import com.bosssoft.platform.installer.wizard.util.JDBCURLUtil;
import com.bosssoft.platform.installer.wizard.util.PropertiesUtil;

public class DeployResourcesUtils {
	private static Logger logger = Logger.getLogger(DeployResourcesUtils.class);
	public static final String DEPLOY_RESOURCES_FILENAME = "deployResources.xml";
	public static final String DEPLOY_PROPERTIES_FILENAME = "deployResources.properties";

	public static void main(String[] args) {
		deployResources();
	}

	public static void deployResources() {
		String install_home = InstallerFileManager.getInstallerHome();
		String deploy_xml_path = install_home + File.separator + "config" + File.separator + "deployResources.xml";
		String config_prop_path = install_home + File.separator + "bin" + File.separator + "deployResources.properties";
		String logPath = configLog4j(install_home);

		DefaultContextFactory factory = new DefaultContextFactory();
		IContext context = factory.createContext();
		InstallRuntime runtime = InstallRuntime.INSTANCE;
		runtime.setContext(context);
		runtime.getContext().setValue("INSTALL_LOGFILE_PATH", logPath);
		runtime.getContext().setValue("INSTALL_LOGFILE_NAME", logPath.substring(logPath.lastIndexOf("/") + 1));

		DeployDef deploy = null;
		try {
			deploy = DeployDefLoader.getDeploy(deploy_xml_path);
		} catch (FileNotFoundException e) {
			throw new InstallException("Not Found File as " + deploy_xml_path, e);
		}
		if (deploy == null) {
			logger.warn("Load deploy.xml failed!" + deploy_xml_path);
			return;
		}

		List actions = deploy.getActions();
		if (actions == null) {
			logger.warn("Not found any action, deploy tool exit");
			return;
		}

		loadProperties2Context(context, config_prop_path);

		setContextValue(context);

		if (!checkConfig(context)) {
			return;
		}

		logger.info("Start deploying server resources ...");
		System.out.println("APP_NAME: " + context.getStringValue("APP_NAME"));
		System.out.println("APP_SERVER_TYPE: " + context.getStringValue("APP_SERVER_TYPE"));
		System.out.println("IS_CLUSTER: " + context.getStringValue("IS_CLUSTER"));
		System.out.println("IS_DEPLOY_DATASOURCE: " + context.getStringValue("IS_DEPLOY_DATASOURCE"));
		System.out.println("IS_DEPLOY_JMSQUEUE: " + context.getStringValue("IS_DEPLOY_JMSQUEUE"));
		ActionsUtil.run(context, actions);
		logger.info("Deploying server resources succeed !");
	}

	private static void loadProperties2Context(IContext context, String propertiesPath) {
		Properties props = PropertiesUtil.readProperties(propertiesPath);
		for (Iterator localIterator = props.keySet().iterator(); localIterator.hasNext();) {
			Object key = localIterator.next();
			context.setValue(key.toString(), props.get(key));
		}
	}

	private static void setContextValue(IContext context) {
		context.setValue("DEFAULT_APP_NAME", context.getStringValue("APP_NAME"));

		if (Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_CLUSTER"))) {
			String appType = context.getStringValue("APP_SERVER_TYPE");
			if (appType.equalsIgnoreCase("WebLogic")) {
				context.setValue("AS_WL_TARGET_SERVER", context.getStringValue("CLUSTER_NAME"));
			}
			if (appType.equalsIgnoreCase("WebSphere")) {
				context.setValue("AS_WAS_CLUSTER_NAME", context.getStringValue("CLUSTER_NAME"));
			}

		}

		if (Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_DEPLOY_DATASOURCE"))) {
			String db_type = context.getStringValue("DB_TYPE");
			String ip = context.getStringValue("DB_IP");
			String port = context.getStringValue("DB_SERVER_PORT");
			String sid = context.getStringValue("DB_NAME");
			String db_url = "";
			if (db_type.toLowerCase().startsWith("oracle")) {
				db_url = JDBCURLUtil.getOracleURL(ip, port, sid);
			}
			if (db_type.toLowerCase().startsWith("db2")) {
				db_url = JDBCURLUtil.getDB2URL(ip, port, sid);
			}
			if (db_type.toLowerCase().startsWith("sqlserver")) {
				db_url = JDBCURLUtil.getSQLServerURL(ip, port, sid);
			}
			if (db_type.toLowerCase().startsWith("sybase")) {
				db_url = JDBCURLUtil.getSybaseURL(ip, port, sid);
			}
			if (db_type.toLowerCase().startsWith("informix")) {
				String serverName = context.getStringValue("DB_INFORMIX_SERVER");
				db_url = JDBCURLUtil.getInformixURL(ip, port, sid, serverName);
			}
			if (db_type.toLowerCase().startsWith("mysql")) {
				db_url = JDBCURLUtil.getMySqlURL(ip, port, sid);
			}
			context.setValue("DB_URL", db_url);
		}
	}

	public static boolean checkConfig(IContext context) {
		AbstractConfigCheck check = new AbstractConfigCheck();

		if (check.assertNull(new String[] { context.getStringValue("APP_NAME") })) {
			check.nullError("APP_NAME");
			return false;
		}

		if (check.assertNull(new String[] { context.getStringValue("APP_SERVER_TYPE") })) {
			check.nullError("APP_SERVER_TYPE");
			return false;
		}

		if (!check.executeCheck(context)) {
			return false;
		}

		String isDeployDS = context.getStringValue("IS_DEPLOY_DATASOURCE");
		if (Boolean.TRUE.toString().equalsIgnoreCase(isDeployDS)) {
			if (check.assertNull(new String[] { context.getStringValue("DB_DS_JNDI_NAME") })) {
				check.nullError("DB_DS_JNDI_NAME");
				return false;
			}

			boolean isExist = CheckDataSourceExistAction.checkDataSourceExist(context, context.getStringValue("DB_DS_JNDI_NAME"));
			if (isExist) {
				System.out.println("ERROR: The data source already exists in the server configuration: " + context.getStringValue("DB_DS_JNDI_NAME"));
				return false;
			}

			if (!check.validateDBConfig(context)) {
				System.out.println("ERROR: Data source '" + context.getStringValue("DB_DS_JNDI_NAME") + "' can not connect, Please check the database config");
				return false;
			}

		}

		return true;
	}

	public static String configLog4j(String installHome) {
		String logPath = null;
		String userHome = System.getProperty("user.home");
		String logFileName = "eos_toos_deployResources.log";

		Properties properties = new Properties();
		String configPath = installHome + File.separator + "config" + "/log4j.properties";
		try {
			properties.load(new FileInputStream(configPath));
		} catch (IOException e) {
			System.out.println("Cann't find file log4j.properties in path:" + configPath);
			return null;
		}

		logPath = userHome + "/" + logFileName;
		properties.setProperty("log4j.appender.InstallFile.File", logPath);
		properties.setProperty("log4j.appender.InstallFile.Threshold", "DEBUG");

		PropertyConfigurator.configure(properties);
		return logPath;
	}
}