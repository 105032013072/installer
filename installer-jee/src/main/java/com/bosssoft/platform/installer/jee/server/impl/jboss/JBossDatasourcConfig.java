package com.bosssoft.platform.installer.jee.server.impl.jboss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import org.apache.commons.io.IOUtils;

import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.config.AbstractDataSourceConfig;

public class JBossDatasourcConfig extends AbstractDataSourceConfig {
	public static boolean isXA = false;

	public JBossDatasourcConfig() {
	}

	public JBossDatasourcConfig(IDataSource dataSource, ITargetModel target) {
		super(dataSource, target);
	}

	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) jeeServer.getEnv();

		String jbossHome = env.getJbossHome();

		String serverName = env.getServerName();

		IDataSource ds = getDataSource();

		String driver = ds.getDriver();
		String jndiName = ds.getJndiName();
		String dsName = ds.getName();
		String user = ds.getUser();
		String password = ds.getPassword();
		String url = ds.getUrl();

		File file = getDsFile(jbossHome, serverName, dsName);
		if (file.exists()) {
			throw new JEEServerOperationException("the data source file already exists!" + file.getAbsolutePath());
		}
		file.getParentFile().mkdirs();

		String lineSep = System.getProperty("line.separator");

		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(lineSep);
		sb.append("<datasources>").append(lineSep);
		sb.append("\t<local-tx-datasource>").append(lineSep);
		sb.append("\t\t<jndi-name>").append(jndiName).append("</jndi-name>").append(lineSep);
		sb.append("\t\t<connection-url>").append(url).append("</connection-url>").append(lineSep);
		sb.append("\t\t<driver-class>").append(driver).append("</driver-class>").append(lineSep);
		sb.append("\t\t<connection-property name=\"user\">").append(user).append("</connection-property>").append(lineSep);
		sb.append("\t\t<connection-property name=\"password\">").append(password).append("</connection-property>").append(lineSep);
		sb.append("\t\t<min-pool-size>").append(5).append("</min-pool-size>").append(lineSep);
		sb.append("\t\t<max-pool-size>").append(100).append("</max-pool-size>").append(lineSep);
		sb.append("\t</local-tx-datasource>").append(lineSep);
		sb.append("</datasources>");

		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			StringReader reader = new StringReader(sb.toString());
			IOUtils.copy(reader, out);
			reader.close();
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) jeeServer.getEnv();
		String jbossHome = env.getJbossHome();

		String serverName = env.getServerName();

		IDataSource ds = getDataSource();

		String dsName = ds.getName();

		File file = getDsFile(jbossHome, serverName, dsName);
		if (file.exists())
			file.delete();
	}

	private File getDsFile(String jbossHome, String serverName, String dsName) {
		String path = "server" + File.separator + serverName + File.separator + "deploy";
		File file = new File(jbossHome, path);
		String dsFileName = dsName + "-ds.xml";

		file = new File(file, dsFileName);
		return file;
	}

	public boolean isDSExist(IJEEServer jeeServer) {
		JBossEnv env = (JBossEnv) jeeServer.getEnv();
		String jbossHome = env.getJbossHome();

		String serverName = env.getServerName();

		IDataSource ds = getDataSource();

		String dsName = ds.getName();

		File file = getDsFile(jbossHome, serverName, dsName);

		return file.exists();
	}
}