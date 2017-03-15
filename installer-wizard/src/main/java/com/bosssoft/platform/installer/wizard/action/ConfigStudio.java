package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.AbstractAction;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.core.util.StringConvertor;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.wizard.cfg.ProductInstallConfigs;
import com.bosssoft.platform.installer.wizard.cfg.Server;

public class ConfigStudio extends AbstractAction {
	transient Logger logger = Logger.getLogger(getClass().getName());

	public void execute(IContext context, Map parameters) throws InstallException {
		String installDir = context.getStringValue("INSTALL_DIR");

		String tomcatHome = context.getStringValue("AS_TOMCAT_HOME");
		updateServerConfig(context, installDir, tomcatHome);
		updateStudioConfig(context, installDir);
		updateDBConfig(context, installDir);

		updateWorkspace(installDir);

		movePlugins(installDir);
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private void movePlugins(String installDir) throws InstallException {
		String eostools = installDir + "/ide/eostools/eclipse/plugins";

		File tmp = new File(eostools);
		String platformPath = null;

		File[] plugins = tmp.listFiles();

		for (int i = 0; i < plugins.length; i++) {
			if (plugins[i].getName().startsWith("com.primeton.studio.platform")) {
				platformPath = plugins[i].getAbsolutePath();
				break;
			}
		}

		if (platformPath != null) {
			File tmpfile = new File(platformPath);
			File dest = new File(installDir + "/ide/eclipse/plugins/" + tmpfile.getName());
			try {
				FileUtils.copy(tmpfile, dest, null, FileOperationMessageListener.INSTANCE);
			} catch (OperationException e) {
				throw new InstallException(e);
			}
		}
	}

	private void updateWorkspace(String installDir) {
		String dest = installDir + "/ide/eclipse/configuration";

		String plugin = "org.eclipse.ui.ide";
		String fileName = "recentWorkspaces.xml";

		Properties prop = new Properties();
		prop.setProperty("STUDIO_INSTALL_PATH", installDir + "/ide");

		String target = dest + "/" + plugin + "/" + fileName;

		if (!new File(target).exists()) {
			return;
		}

		if (!new File(target).exists())
			return;
		try {
			StringConvertor.replaceFile(target, "$", "$", prop);
		} catch (IOException e) {
			this.logger.error(e, e);
		}
		prop.clear();
	}

	private void updateStudioConfig(IContext context, String installDir) {
		String dest = installDir + "/ide/eclipse/configuration/eos_studio_config";

		String fileName = "StudioConfig.xml";

		String studioUser = context.getStringValue("USER_NAME");

		String studioCompany = context.getStringValue("USER_COMPANY");
		if (studioUser.trim().length() == 0) {
			studioUser = System.getProperty("user.name");
		}
		Properties prop = new Properties();
		prop.setProperty("STUDIO_USER", studioUser);
		prop.setProperty("STUDIO_COMPANY", studioCompany);

		String target = dest + "/" + fileName;
		if (!new File(target).exists())
			return;
		try {
			StringConvertor.replaceXmlFile(target, "$", "$", prop);
		} catch (IOException e) {
			this.logger.error(e, e);
		}
		prop.clear();
	}

	private void updateDBConfig(IContext context, String installDir) {
		File eosToolsDir = new File(installDir + "/ide/eostools/eclipse/plugins");
		File[] serverLibPlugin = eosToolsDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().startsWith("com.primeton.studio.server.library")) {
					return true;
				}
				return false;
			}
		});
		String dest = serverLibPlugin[0].getAbsolutePath() + "/server/3rd/jdbc";
		String config = installDir + "/ide/eclipse/configuration/eos_studio_config/connection";
		String fileName = "connection.info";

		String dbCfgType = context.getStringValue("DB_TYPE");
		Server dbsrv = ProductInstallConfigs.getDBServer(dbCfgType);

		String dbVersion = dbsrv.getVersion();

		if (dbCfgType.toLowerCase().startsWith("oracle")) {
			dbVersion = "10";
		}

		String dbDirName = StringUtils.replace(dbCfgType.toLowerCase(), " ", "");
		String targetDir = config + File.separatorChar + dbDirName;

		String dbUser = context.getStringValue("DB_USERNAME");
		String dbPwd = context.getStringValue("DB_PASSWORD");
		String dbUrl = context.getStringValue("DB_URL");
		String dbDriver = context.getStringValue("DB_DRIVER");
		String isDefaultJar = context.getStringValue("DB_IS_DEFAULT_JAR").toLowerCase();
		if (isDefaultJar.equals("")) {
			isDefaultJar = "true";
		}
		Properties prop = new Properties();
		prop.setProperty("STUDIO_DB_TYPE", dbCfgType.equalsIgnoreCase("sqlserver") ? "SQL Server" : dbCfgType);
		prop.setProperty("STUDIO_DB_DIRNAME", dbDirName);
		prop.setProperty("STUDIO_DB_USER", dbUser);
		prop.setProperty("STUDIO_DB_PASSWORD", dbPwd);
		prop.setProperty("STUDIO_DB_URL", dbUrl);
		prop.setProperty("STUDIO_DB_DRIVER", dbDriver);
		prop.setProperty("STUDIO_DB_VERSION", dbVersion);

		if ("true".equals(isDefaultJar)) {
			String[] jars = getDriverJars(dbCfgType);
			String jarPath = dest + "/" + StringUtils.join(jars, new StringBuilder(";").append(dest).append("/").toString());
			jarPath = jarPath.replace('\\', '/');

			prop.setProperty("STUDIO_DB_JDBC_LIBS", jarPath);
		} else {
			String jarPath = context.getStringValue("DB_JDBC_LIBS");
			String[] path = StringConvertor.parsePath(jarPath);
			String libFiles = "";
			if (path.length == 1) {
				libFiles = dest + "/" + new File(path[0]).getName();
			} else {
				for (int i = 0; i < path.length; i++) {
					libFiles = libFiles + dest + "/" + new File(path[i]).getName() + ";";
				}
				libFiles = libFiles.substring(0, libFiles.length() - 1);
			}
			libFiles = libFiles.replace('\\', '/');
			prop.setProperty("STUDIO_DB_JDBC_LIBS", libFiles);
		}
		try {
			String target = targetDir + "/" + fileName;
			String sourceFile = InstallerFileManager.getBaseCompsDir() + "/eos/studio/config/" + fileName;
			FileUtils.copy(new File(sourceFile), new File(target), null, FileOperationMessageListener.INSTANCE);
			StringConvertor.replaceFile(target, "$", "$", prop);
		} catch (Exception e) {
			this.logger.error(e, e);
		}
		prop.clear();
	}

	private String[] getDriverJars(String dbType) {
		List servers = ProductInstallConfigs.getSupportedDBSvrs();
		for (Iterator iter = servers.iterator(); iter.hasNext();) {
			Server server = (Server) iter.next();
			if (server.getName().equals(dbType)) {
				String jars = server.getJars();
				if (jars == null) {
					return null;
				}
				return StringConvertor.parsePath(jars);
			}
		}
		return null;
	}

	private void updateServerConfig(IContext context, String installDir, String appServer) {
		String dest = installDir + "/ide/eclipse/configuration/eos_studio_config";
		dest = dest.replace("\\\\", "/");
		dest = dest.replace("\\", "/");
		String fileName = "server.xml";
		String externalDir = installDir + "/apps_config";
		externalDir = externalDir.replace("\\\\", "/");
		externalDir = externalDir.replace("\\", "/");
		Properties prop = new Properties();

		prop.setProperty("STUDIO_TOMCAT_INSTALLDIR", appServer);
		prop.setProperty("STUDIO_EXTERNAL_CONFIG_DIR", externalDir);

		String target = dest + "/" + fileName;

		if (!new File(target).exists()) {
			return;
		}

		if (!new File(target).exists())
			return;
		try {
			StringConvertor.replaceFile(target, "$", "$", prop);
		} catch (IOException e) {
			this.logger.error(e);
		}
		prop.clear();
	}
}