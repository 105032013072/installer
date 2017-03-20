package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class UpdateTomcatPort implements IAction {
	private static final String SERVER_XML_PATH = "SERVER_XML_PATH";
	private static final String TOMCAT_PORT = "TOMCAT_PORT";
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String xmlpath = parameters.get(SERVER_XML_PATH).toString();
		String port = parameters.get(TOMCAT_PORT).toString();
		if (!"8080".equalsIgnoreCase(port))
			try {
				XmlFile xmlFile = new XmlFile(new File(xmlpath));
				Node pwdNode = xmlFile.findNode("//Connector");
				if (pwdNode == null) {
					throw new InstallException("修改Tomcat服务器端口失败!", new NullPointerException("Could not find the node for Port!"));
				}
				xmlFile.setNodeAttribute("//Connector", "port", port);
				xmlFile.save();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
	}

	public void rollback(IContext arg0, Map arg1) throws InstallException {
	}

	public static void main(String[] args) {
		UpdateTomcatPort action = new UpdateTomcatPort();
		Map parameters = new HashMap();
		parameters.put("SERVER_XML_PATH", "D:/server.xml");
		parameters.put("TOMCAT_PORT", "9090");
		action.execute(null, parameters);
	}
}