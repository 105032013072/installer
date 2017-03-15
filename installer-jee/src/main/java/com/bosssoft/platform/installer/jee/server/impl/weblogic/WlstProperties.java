package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import org.apache.commons.io.IOUtils;

import com.bosssoft.platform.installer.jee.server.internal.util.Util;

public class WlstProperties {
	public static final String OP_DEPLOY = "deploy";
	public static final String OP_UNDEPLOY = "undeploy";
	public static final String OP_START = "start";
	public static final String OP_STOP = "stop";
	public static final String OP_ADD_JMS = "addJMS";
	public static final String OP_REMOVE_JMS = "removeJMS";
	public static final String OP_ADD_DATASOURCE = "addDataSource";
	public static final String OP_REMOVE_DATASOURCE = "removeDataSource";
	private static final String FILE_START_UP_PY = "startUp.py";
	private static final String FILE_DEPLOY_PY = "deploy.py";
	private static final String FILE_JMS_PY = "jms.py";
	private static final String FILE_DS_PY = "ds.py";
	private static File wlstJmsPython = null;
	private static File wlstDsPython = null;
	private static File wlstStartupPython = null;
	private static File wlstDeployPython = null;

	public static File getJmsPython() {
		if ((wlstJmsPython != null) && (wlstJmsPython.exists())) {
			return wlstJmsPython;
		}
		String pythonFile = "jms.py";
		wlstJmsPython = copyToTemp(pythonFile);
		return wlstJmsPython;
	}

	public static File getDSPython() {
		if ((wlstDsPython != null) && (wlstDsPython.exists())) {
			return wlstDsPython;
		}
		String pythonFile = "ds.py";
		wlstDsPython = copyToTemp(pythonFile);
		return wlstDsPython;
	}

	public static File getDeployPython() {
		if ((wlstDeployPython != null) && (wlstDeployPython.exists())) {
			return wlstDeployPython;
		}
		String pythonFile = "deploy.py";
		wlstDeployPython = copyToTemp(pythonFile);
		return wlstDeployPython;
	}

	public static File getStartupPython() {
		if ((wlstStartupPython != null) && (wlstStartupPython.exists())) {
			return wlstStartupPython;
		}
		String pythonFile = "startUp.py";
		wlstStartupPython = copyToTemp(pythonFile);
		return wlstStartupPython;
	}

	private static File copyToTemp(String fileName) {
		InputStream inputStream = WeblogicDataSourceConfig.class.getResourceAsStream(fileName);
		OutputStream outputStream = null;
		try {
			File tmpdir = Util.getTempDir();
			File file = new File(tmpdir, fileName);
			if (file.exists()) {
				file.delete();
			}
			outputStream = new FileOutputStream(file);
			IOUtils.copy(inputStream, outputStream);

			return file;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(outputStream);
		}
	}

	static void save(Object object, String file) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(file);
		Properties properties = new Properties();

		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String name = field.getName();
			try {
				Object value = field.get(object);
				if (value == null) {
					value = "";
				}

				properties.put(name, String.valueOf(value).replace("\\", "/"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			properties.store(outputStream, null);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	public static class DataSourceProperty {
		public String _operations;
		public String resultFile;
		public String userName;
		public String passWord;
		public String adminServerURL;
		public String targetServers;
		public String targetClusters;
		public String dsname;
		public String jndiName;
		public String dbUrl;
		public String dbDriver;
		public String dbPassword;
		public String dbUser;
		public String testTable;

		public DataSourceProperty(String _operations, String resultFile, String userName, String passWord, String adminServerURL, String targetServers, String targetClusters,
				String dsname, String jndiName, String dbUrl, String dbDriver, String dbPassword, String dbUser, String testTable) {
			this._operations = _operations;
			this.resultFile = resultFile;
			this.userName = userName;
			this.passWord = passWord;
			this.adminServerURL = adminServerURL;
			this.targetServers = targetServers;
			this.targetClusters = targetClusters;
			this.dsname = dsname;
			this.jndiName = jndiName;
			this.dbUrl = dbUrl;
			this.dbDriver = dbDriver;
			this.dbPassword = dbPassword;
			this.dbUser = dbUser;
			if (this.resultFile != null) {
				this.resultFile.replace("\\", "/");
			}
			this.testTable = testTable;
		}

		public DataSourceProperty() {
		}

		public void save(String file) throws IOException {
			WlstProperties.save(this, file);
		}
	}

	public static class DeployProperty {
		public String _operations;
		public String resultFile;
		public String userName;
		public String passWord;
		public String adminServerURL;
		public String appName;
		public String earPath;
		public String targetServers;
		public String targetClusters;
		public String timeOut;
		public String _stageModel;

		public DeployProperty(String _operations, String resultFile, String userName, String passWord, String adminServerURL, String appName, String earPath, String targetServers,
				String targetClusters, String timeOut) {
			this._operations = _operations;
			this.resultFile = resultFile;
			this.userName = userName;
			this.passWord = passWord;
			this.adminServerURL = adminServerURL;
			this.appName = appName;
			this.earPath = earPath;
			this.targetServers = targetServers;
			this.targetClusters = targetClusters;
			this.timeOut = timeOut;
			if (this.resultFile != null)
				this.resultFile.replace("\\", "/");
		}

		public DeployProperty() {
		}

		public void save(String file) throws IOException {
			WlstProperties.save(this, file);
		}
	}

	public static class JMSProperty {
		public String _operations;
		public String resultFile;
		public String targetServers;
		public String targetClusters;
		public String userName;
		public String passWord;
		public String adminServerURL;
		public String _storeName;
		public String storeDirectory;
		public String jmsServerName;
		public String jmsModuleName;
		public String jmsSubDeploymentName;
		public String jmsConnectionFactoryName;
		public String jmsConnectionFactoryJndi;
		public String jmsTopicName;
		public String jmsTopicJndi;
		public String jmsQueueName;
		public String jmsQueueJndi;
		public String _domainHome;
		public String useJDBCStore = "false";
		public String _dataSourceName;
		public String tablePrefix;

		public JMSProperty(String _operations, String resultFile, String targetServers, String targetClusters, String userName, String passWord, String adminServerURL,
				String storeName, String storeDirectory, String jmsServerName, String jmsModuleName, String jmsSubDeploymentName, String jmsConnectionFactoryName,
				String jmsConnectionFactoryJndi, String jmsTopicName, String jmsTopicJndi, String jmsQueueName, String jmsQueueJndi, String _domainHome) {
			this._operations = _operations;
			this.resultFile = resultFile;
			this.targetServers = targetServers;
			this.targetClusters = targetClusters;
			this.userName = userName;
			this.passWord = passWord;
			this.adminServerURL = adminServerURL;
			this._storeName = storeName;
			this.storeDirectory = storeDirectory;
			this.jmsServerName = jmsServerName;
			this.jmsModuleName = jmsModuleName;
			this.jmsSubDeploymentName = jmsSubDeploymentName;
			this.jmsConnectionFactoryName = jmsConnectionFactoryName;
			this.jmsConnectionFactoryJndi = jmsConnectionFactoryJndi;
			this.jmsTopicName = jmsTopicName;
			this.jmsTopicJndi = jmsTopicJndi;
			this.jmsQueueName = jmsQueueName;
			this.jmsQueueJndi = jmsQueueJndi;
			this._domainHome = _domainHome;
			if (this.resultFile != null)
				this.resultFile.replace("\\", "/");
		}

		public JMSProperty() {
		}

		public void save(String file) throws IOException {
			WlstProperties.save(this, file);
		}
	}

	public static class StartupProperty {
		public String _operations;
		public String resultFile;
		public String userName;
		public String passWord;
		public String adminServerURL;
		public String domainHome;
		public String domainName;
		public String adminServer;
		public String domainDir;
		public String timeOut;
		public String _jvmArgs;

		public StartupProperty(String _operations, String resultFile, String userName, String passWord, String adminServerURL, String domainHome, String domainName,
				String adminServer, String domainDir, String timeOut, String _jvmArgs) {
			this._operations = _operations;
			this.resultFile = resultFile;
			this.userName = userName;
			this.passWord = passWord;
			this.adminServerURL = adminServerURL;
			this.domainHome = domainHome;
			this.domainName = domainName;
			this.adminServer = adminServer;
			this.domainDir = domainDir;
			this.timeOut = timeOut;
			this._jvmArgs = _jvmArgs;
			if (this.resultFile != null)
				this.resultFile.replace("\\", "/");
		}

		public StartupProperty() {
		}

		public void save(String file) throws IOException {
			WlstProperties.save(this, file);
		}
	}
}