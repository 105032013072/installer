package com.bosssoft.platform.installer.wizard.appsvr;

import java.io.File;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class JBossEnvIniter implements IJeeServerEnvIniter {
	Logger logger = Logger.getLogger(getClass());
	private String jbossHome = null;

	public JBossEnvIniter(String home) {
		this.jbossHome = home;
	}

	public boolean excute() {
		try {
			modifyEarDeployer();
		} catch (Exception e) {
			throw new InstallException("Modify ear-deployer.xml failed!");
		}
		try {
			modifyServerXml();
		} catch (Exception e) {
			throw new InstallException("Modify server.xml failed!");
		}
		try {
			setCompilerSourceVM();
		} catch (Exception e) {
			throw new InstallException("Modify web.xml failed!");
		}

		return true;
	}

	protected void setCompilerSourceVM() throws Exception {
		String configFilePath = this.jbossHome.concat("/server/default/deploy/jbossweb-tomcat55.sar/conf/web.xml");

		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			this.logger.error("Not found JBoss config file as " + configFilePath);
			return;
		}

		XmlFile xmlFile = new XmlFile(configFile);
		String xql = "//servlet[servlet-name='jsp']/init-param[paramname='compilerSourceVM']/param-value";
		Node node = xmlFile.findNode(xql);
		String xmlStr = "<init-param><param-name>compilerSourceVM</param-name><param-value>1.5</param-value></init-param>";

		if (node == null)
			xmlFile.addNode("//servlet[servlet-name='jsp']", xmlStr);
		else
			xmlFile.setNodeValue(xql, "1.5");
		xmlFile.save();
	}

	protected void modifyServerXml() throws Exception {
		String configFilePath = this.jbossHome.concat("/server/default/deploy/jbossweb-tomcat55.sar/server.xml");

		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			this.logger.error("Not found JBoss config file as " + configFilePath);
			return;
		}

		XmlFile xmlFile = new XmlFile(configFile);
		String xql = "//Server/Service/Connector";

		NodeList nl = xmlFile.findNodes(xql);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			if (!((Element) nl.item(i)).getAttribute("protocol").startsWith("AJP")) {
				((Element) nl.item(i)).setAttribute("URIEncoding", "UTF-8");
			}
		}
		xmlFile.save();
	}

	protected void modifyEarDeployer() throws Exception {
		String configFilePath = this.jbossHome.concat("/server/default/deploy/ear-deployer.xml");

		File configFile = new File(configFilePath);
		if (!configFile.exists()) {
			this.logger.error("Not found JBoss config file as " + configFilePath);
			return;
		}

		XmlFile xmlFile = new XmlFile(configFile);
		String xql = "//attribute[@name='Isolated']";
		NodeList nl = xmlFile.findNodes(xql);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			nl.item(i).setTextContent("true");
		}

		xql = "//attribute[@name='CallByValue']";
		nl = xmlFile.findNodes(xql);
		if (nl == null)
			return;
		for (int i = 0; i < nl.getLength(); i++) {
			nl.item(i).setTextContent("true");
		}

		xmlFile.save();
	}

	public static void main(String[] args) {
		String jbossHome = "D:\\appsvr\\jboss-4.0.5.GA";
		JBossEnvIniter je = new JBossEnvIniter(jbossHome);
		try {
			je.modifyEarDeployer();
			je.modifyServerXml();
			je.setCompilerSourceVM();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}