package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.config.AbstractDataSourceConfig;
import com.bosssoft.platform.installer.jee.server.internal.util.Util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

public class WeblogicDataSourceConfig extends AbstractDataSourceConfig {
	public WeblogicDataSourceConfig() {
	}

	public WeblogicDataSourceConfig(IDataSource dataSource, ITargetModel target) {
		super(dataSource, target);
	}

	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		WeblogicEnv env = (WeblogicEnv) jeeServer.getEnv();
		run(env, "addDataSource");
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
		WeblogicEnv env = (WeblogicEnv) jeeServer.getEnv();
		run(env, "removeDataSource");
	}

	private void run(WeblogicEnv env, String operation) throws JEEServerOperationException {
		WlstProperties.DataSourceProperty properties = getProperties(env, operation);

		File tempDir = Util.getTempDir();
		File propertieFile = new File(tempDir, "dataSourceProperty.properties");
		String path = propertieFile.getAbsolutePath();
		try {
			properties.save(path);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}

		String weblogicHome = env.getWeblogicHome();
		File wlstCMD = WeblogicScrptTool.getWlstCmd(weblogicHome);
		File pythonFile = WlstProperties.getDSPython();
		RunWlstCmdArgs arg = new RunWlstCmdArgs(wlstCMD, pythonFile, propertieFile);
		try {
			WeblogicScrptTool.runWLST(arg);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
	}

	private WlstProperties.DataSourceProperty getProperties(WeblogicEnv env, String operation) {
		String userName = env.getLoginName();
		String password = env.getPassword();

		String host = env.getHost();
		String port = env.getPort();
		String url = "t3://" + host + ":" + port;

		IDataSource dataSource = getDataSource();

		String resultFile = WeblogicScrptTool.getResultLogFile().getAbsolutePath();
		StringBuilder servers = new StringBuilder();
		StringBuilder clusters = new StringBuilder();
		ITargetModel target = getTarget();
		addTargets(servers, clusters, target);

		WlstProperties.DataSourceProperty dsProperty = new WlstProperties.DataSourceProperty();
		dsProperty._operations = operation;
		dsProperty.adminServerURL = url;
		dsProperty.dbDriver = dataSource.getDriver();
		dsProperty.dbPassword = dataSource.getPassword();
		dsProperty.dbUrl = dataSource.getUrl();
		dsProperty.dbUser = dataSource.getUser();
		dsProperty.dsname = dataSource.getName();
		dsProperty.jndiName = dataSource.getJndiName();
		dsProperty.testTable = dataSource.getTestTable();
		dsProperty.resultFile = resultFile;
		dsProperty.targetClusters = clusters.toString();
		dsProperty.targetServers = servers.toString();
		dsProperty.userName = userName;
		dsProperty.passWord = password;

		return dsProperty;
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

	public boolean isDSExist(IJEEServer jeeServer) {
		WeblogicEnv weblogicEnv = (WeblogicEnv) jeeServer.getEnv();
		String domainHome = weblogicEnv.getDomainHome();

		if ((domainHome == null) || (domainHome.trim().equals("")) || (!new File(domainHome).exists())) {
			return false;
		}

		File jdbcDir = new File(domainHome, "config" + File.separator + "jdbc");

		if ((!jdbcDir.exists()) && (!jdbcDir.isDirectory())) {
			return false;
		}
		String name = getDataSource().getName();
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		File[] dsFiles = jdbcDir.listFiles();
		for (int i = 0; i < dsFiles.length; i++) {
			File file = dsFiles[i];
			if (file.getName().endsWith(".xml")) {
				try {
					XmlFile xmlFile = new XmlFile(file);
					Element element = (Element) xmlFile.findNode("/jdbc-data-source/name");
					String text = element.getTextContent();
					if ((text != null) && (!text.trim().equals("")) && (StringUtils.equals(text, name))) {
						return true;
					}
				} catch (Exception localException) {
				}
			}
		}
		return false;
	}
}