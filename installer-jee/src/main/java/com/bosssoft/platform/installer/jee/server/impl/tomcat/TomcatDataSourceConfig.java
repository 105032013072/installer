package com.bosssoft.platform.installer.jee.server.impl.tomcat;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IDataSource;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.config.AbstractDataSourceConfig;
import com.bosssoft.platform.installer.jee.server.internal.Debug;
import com.bosssoft.platform.installer.jee.server.internal.JDBCDataSourceImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TomcatDataSourceConfig extends AbstractDataSourceConfig {
	private Debug debug = Debug.getDebug();

	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		TomcatEnv env = (TomcatEnv) jeeServer.getEnv();
		String tomcatHome = env.getTomcatHome();
		IDataSource ds = getDataSource();
		boolean exist = existDataSource(ds.getName(), tomcatHome);
		if (exist) {
			throw new JEEServerOperationException("the datasource has been exist!" + ds.getName());
		}
		addDataSource(getDataSource(), tomcatHome);
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
		TomcatEnv env = (TomcatEnv) jeeServer.getEnv();
		String tomcatHome = env.getTomcatHome();
		IDataSource ds = getDataSource();
		removeDataSource(ds.getName(), tomcatHome);
	}

	private void addDataSource(IDataSource ds, String tomcatHome) throws JEEServerOperationException {
		try {
			String doc = getDocInServerConf(ds);
			XmlFile xmlFile = new XmlFile(new File(getServerConf(tomcatHome)));
			xmlFile.addNode("/Server/GlobalNamingResources", doc);
			xmlFile.save();

			doc = getDocInContextConf(ds);
			xmlFile = new XmlFile(new File(getContextConf(tomcatHome)));
			xmlFile.addNode("/Context", doc);
			xmlFile.save();
		} catch (Exception e) {
			throw new JEEServerOperationException(e);
		}
	}

	private void removeDataSource(String dsName, String tomcatHome) throws JEEServerOperationException {
		try {
			XmlFile xmlFile = new XmlFile(new File(getServerConf(tomcatHome)));
			Node node = xmlFile.findNode("/Server/GlobalNamingResources/Resource[@name=\"" + dsName + "\"]");
			if (node == null) {
				this.debug.debug("Could not be found the datasource:" + dsName);
			} else {
				xmlFile.removeNode("/Server/GlobalNamingResources/Resource[@name=\"" + dsName + "\" and @type=\"javax.sql.DataSource\"]");
				xmlFile.save();
			}

			xmlFile = new XmlFile(new File(getContextConf(tomcatHome)));

			node = xmlFile.findNode("/Context/ResourceLink[@global=\"" + dsName + "\" and @type=\"javax.sql.DataSource\"]");
			if (node == null) {
				this.debug.debug("Could not be found the datasource:" + dsName);
			} else {
				xmlFile.removeNode("/Context/ResourceLink[@global=\"" + dsName + "\" and @type=\"javax.sql.DataSource\"]");
				xmlFile.save();
			}
		} catch (Exception e) {
			throw new JEEServerOperationException("Could not be removed the datasource!", e);
		}
	}

	public IDataSource[] getDataSources(String tomcatHome) {
		try {
			XmlFile xmlFile = new XmlFile(new File(getServerConf(tomcatHome)));
			NodeList nodes = xmlFile.findNodes("/Server/GlobalNamingResources/Resource[@type=\"javax.sql.DataSource\"]");

			int len = nodes.getLength();
			List<IDataSource> list = new ArrayList<IDataSource>();

			for (int i = 0; i < len; i++) {
				Element node = (Element) nodes.item(i);
				String jndiName = node.getAttribute("name");
				String dsName = getDsName(jndiName, tomcatHome);
				String driver = node.getAttribute("driverClassName");
				String url = node.getAttribute("url");
				String username = node.getAttribute("username");
				String password = node.getAttribute("password");

				JDBCDataSourceImpl ds = new JDBCDataSourceImpl(driver, jndiName, dsName, username, password, url, null);

				list.add(ds);
			}
			return (IDataSource[]) list.toArray(new IDataSource[list.size()]);
		} catch (Exception e) {
			throw new RuntimeException("Could not be found any datasource!", e);
		}
	}

	private String getDsName(String jndiName, String tomcatHome) throws IOException {
		XmlFile contextFile = new XmlFile(new File(getContextConf(tomcatHome)));
		Element node=null;
		try {
			node = (Element) contextFile.findNode("/Context/ResourceLink[@name=\"" + jndiName + "\" and @type=\"javax.sql.DataSource\"]");
		} catch (XPathExpressionException e) {
			this.debug.debug("Could not be found the datasource with jndi name! jndi name:" + jndiName + "the config file:" + getContextConf(tomcatHome));
			return null;
		}
	
		if (node == null) {
			this.debug.debug("Could not be found the datasource with jndi name! jndi name:" + jndiName + "the config file:" + getContextConf(tomcatHome));
			return null;
		}
		String dsName = node.getAttribute("global");
		return dsName;
	}

	public boolean existDataSource(String dsName, String tomcatHome) {
		IDataSource[] dataSources = getDataSources(tomcatHome);

		boolean exist = false;
		for (int i = 0; i < dataSources.length; i++) {
			IDataSource source = dataSources[i];
			if (dsName.equals(source.getName())) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	private String getServerConf(String tomcatHome) {
		return tomcatHome + File.separator + "conf" + File.separator + "server.xml";
	}

	private String getContextConf(String tomcatHome) {
		return tomcatHome + File.separator + "conf" + File.separator + "context.xml";
	}

	private String getDocInServerConf(IDataSource ds) {
		String jndiName = ds.getJndiName();
		String user = ds.getUser();
		String password = ds.getPassword();
		String driver = ds.getDriver();
		String url = ds.getUrl();

		String doc = "\t<Resource name=\"" + jndiName + "\" type=\"javax.sql.DataSource\" password=\"" + password + "\" driverClassName=\"" + driver
				+ "\"  maxIdle=\"2\" maxWait=\"5000\" username=\"" + user + "\" url=\"" + url + "\" maxActive=\"4\"/>\n";

		return doc;
	}

	private String getDocInContextConf(IDataSource ds) {
		String dsName = ds.getName();
		String jndiName = ds.getJndiName();

		String doc = "\t<ResourceLink global=\"" + dsName + "\" name=\"" + jndiName + "\" type=\"javax.sql.DataSource\"/>\n";

		return doc;
	}

	public boolean isDSExist(IJEEServer jeeServer) {
		TomcatEnv env = (TomcatEnv) jeeServer.getEnv();
		String tomcatHome = env.getTomcatHome();
		IDataSource ds = getDataSource();
		String dsName = ds.getName();
		return existDataSource(dsName, tomcatHome);
	}
}