package com.bosssoft.platform.installer.jee.server.impl.websphere;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Node;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.ProductDefination;
import com.bosssoft.platform.installer.jee.server.config.AbstractDataSourceConfig;

public class WebsphereDatasourceConfig extends AbstractDataSourceConfig {
	public static final String DB_INFORMIX_SERVER = "DB_INFORMIX_SERVER";
	public static final String DB_DRIVER_JARS = "";

	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profilesHome = env.getProfilesHome();
		String profileName = env.getProfileName();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String nodeName = env.getNodeName();
		String serverName = env.getServerName();

		IDataSource dataSource = getDataSource();

		ProductDefination productDefination = dataSource.getProductDefination();

		String dbIP = dataSource.getProperty("DB_IP");
		String dbPort = dataSource.getProperty("DB_PORT");
		String dbDriverJars = dataSource.getProperty("DB_DRIVER_JARS");
		String dbName = dataSource.getProperty("DB_NAME");
		String dbType = dataSource.getProperty("DB_TYPE").toLowerCase();
		String dbUrl = dataSource.getUrl();

		String driver = dataSource.getDriver();
		String jndiName = dataSource.getJndiName();
		String dsName = dataSource.getName();

		String dbUser = dataSource.getUser();
		String dbPassword = dataSource.getPassword();

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}
		File scriptFile = new File(dir, "create_datasource.jacl");

		String[] jaclParameters = { nodeName, serverName, dbType, dbDriverJars, dbIP, dbUrl, dbPort, dbName, dbUser, dbPassword, jndiName, dsName };

		int result = WASScriptTool.execScript(profilesHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);
		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("config data source was failed!" + dsName);
	}

	public void configCluster(IJEEServer jeeServer) throws JEEServerOperationException {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		String profileHome = env.getProfilesHome();
		String username = env.getLoginName();
		String password = env.getPassword();
		String cellName = env.getCellName();
		String clusterName = env.getClusterName();

		IDataSource dataSource = getDataSource();

		String dbIP = dataSource.getProperty("DB_IP");
		String dbPort = dataSource.getProperty("DB_PORT");
		String dbDriverJars = dataSource.getProperty("DB_DRIVER_JARS");
		String dbName = dataSource.getProperty("DB_NAME");
		String dbType = dataSource.getProperty("DB_TYPE").toLowerCase();
		String dbUrl = dataSource.getUrl();
		String jndiName = dataSource.getJndiName();
		String dsName = dataSource.getName();

		String dbUser = dataSource.getUser();
		String dbPassword = dataSource.getPassword();

		File dir = null;
		try {
			dir = WASScriptTool.getScriptDir();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage(), e);
		}
		File scriptFile = new File(dir, "create_cluster_datasource.jacl");

		String[] jaclParameters = { clusterName, dbType, dbDriverJars, dbIP, dbUrl, dbPort, dbName, dbUser, dbPassword, jndiName, dsName, cellName };

		int result = WASScriptTool.execScript(profileHome, username, password, scriptFile.getAbsolutePath(), jaclParameters);

		if (WASScriptTool.EXEC_SUCC != result)
			throw new JEEServerOperationException("config WAS DataSource '" + dsName + "' failed!");
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
	}

	public boolean isDSExist(IJEEServer jeeServer) {
		WebsphereEnv env = (WebsphereEnv) jeeServer.getEnv();
		StringBuilder strBuilder = new StringBuilder(env.getProfilesHome()).append(File.separator);
		strBuilder.append("config").append(File.separator);
		strBuilder.append("cells").append(File.separatorChar).append(env.getCellName());

		File resourceFile = new File(strBuilder.toString(), "resources.xml");

		if ((env.getClusterName() != null) && (!"".equals(env.getClusterName()))) {
			strBuilder.append(File.separatorChar).append("clusters").append(File.separatorChar).append(env.getClusterName());
			resourceFile = new File(strBuilder.toString(), "resources.xml");
			if (checkDsExist(resourceFile)) {
				return true;
			}
		}

		strBuilder.append(File.separatorChar).append("nodes").append(File.separatorChar);
		strBuilder.append(env.getNodeName()).append(File.separatorChar);
		strBuilder.append("servers").append(File.separatorChar).append(env.getServerName());
		resourceFile = new File(strBuilder.toString(), "resources.xml");

		if (checkDsExist(resourceFile)) {
			return true;
		}
		return false;
	}

	private boolean checkDsExist(File resourceFile) {
		String dsName = getDataSource().getName();
		try {
			XmlFile xmlFile = new XmlFile(resourceFile);
			Node node = xmlFile.findNode("//factories[@name=\"" + dsName + "\"]");
			if (node != null)
				return true;
		} catch (Exception localException) {
		}
		return false;
	}
}