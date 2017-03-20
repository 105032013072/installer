package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.bosssoft.platform.installer.io.xml.XmlFile;

public class ApplicationServerHelper {
	static transient Logger logger = Logger.getLogger(ApplicationServerHelper.class);
	static Properties prop = null;

	public static String getWebPort(String asType, Map<String, Object> parameters) throws Exception {
		String webPort = null;
		asType = asType.toLowerCase();
		if (asType.indexOf("Jboss4.0.5GA".toLowerCase()) >= 0) {
			String jbossHome = (String) parameters.get("AS_JBOSS_HOME");
			File file = new File(jbossHome + File.separator + "server" + File.separator + "default" + File.separator + "deploy" + File.separator + "jbossweb-tomcat55.sar"
					+ File.separator + "server.xml");

			XmlFile xmlFile = new XmlFile(file);

			Element connectorNode = (Element) xmlFile.findNode("/Server/Service/Connector");
			webPort = connectorNode.getAttribute("port");
			if ((webPort == null) || ("".equals(webPort.trim()))) {
				logger.error("Appserver:" + asType + " is not ready,Because can not find connector in server.xml");
			}
		} else if (asType.indexOf("WebLogic".toLowerCase()) >= 0) {
			webPort = (String) parameters.get("AS_WL_WEBSVR_PORT");
		} else if (asType.indexOf("WebSphere".toLowerCase()) >= 0) {
			String profileHome = (String) parameters.get("AS_WAS_PROFILE_HOME");
			String cellName = (String) parameters.get("AS_WAS_CELL_NAME");
			String nodeName = (String) parameters.get("AS_WAS_NODE_NAME");
			String serverName = (String) parameters.get("AS_WAS_SERVER_NAME");
			String indexPath = profileHome + "/config/cells/" + cellName + "/nodes/" + nodeName + "/serverindex.xml";

			XmlFile xmlFile = new XmlFile(new File(indexPath));

			Element element = (Element) xmlFile.findNode("//serverEntries[@serverName=\"" + serverName + "\"]/specialEndpoints[@endPointName=\"WC_defaulthost\"]/endPoint");

			if (element != null)
				webPort = element.getAttribute("port");
		} else if (asType.indexOf("Tomcat5.5.20".toLowerCase()) >= 0) {
			String tomcatHome = (String) parameters.get("AS_TOMCAT_HOME");
			XmlFile xmlFile = new XmlFile(new File(tomcatHome + File.separator + "conf" + File.separator + "server.xml"));

			Element connectorNode = (Element) xmlFile.findNode("/Server/Service/Connector");

			if (connectorNode == null)
				logger.error("Appserver:" + asType + " is not ready,Because can not find connector in server.xml");
			else {
				webPort = connectorNode.getAttribute("port");
			}
		}

		logger.info("AppServer Type=" + asType + ",webport=" + webPort);
		return webPort;
	}

	public static String getWASPortDef(String key, String profileHome) {
		if (prop == null) {
			String portDefFile = profileHome + File.separator + "properties" + File.separator + "portdef.props";
			prop = new Properties();
			try {
				prop.load(new FileInputStream(new File(portDefFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				logger.error(e);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
		return prop.getProperty(key);
	}
}