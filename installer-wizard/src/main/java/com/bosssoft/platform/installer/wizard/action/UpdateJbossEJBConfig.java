package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.impl.context.InstallContext;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class UpdateJbossEJBConfig implements IAction {
	private static final String JBOSS_EJB_JAR_XML_PATH = "JBOSS_EJB_JAR_XML_PATH";

	public void execute(IContext context, Map parameters) throws InstallException {
		String path = parameters.get("JBOSS_EJB_JAR_XML_PATH").toString();
		String appName = context.getStringValue("DEFAULT_APP_NAME");
		try {
			XmlFile xmlFile = new XmlFile(new File(path));
			NodeList nodeList = xmlFile.findNodes("/jboss/enterprise-beans/session/jndi-name");

			int len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				modifyNode(appName, node);
			}

			nodeList = xmlFile.findNodes("/jboss/enterprise-beans/session/local-jndi-name");
			len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				replaceByAppName(appName, node);
			}

			nodeList = xmlFile.findNodes("/jboss/enterprise-beans/message-driven/destination-jndi-name");
			len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				replaceByAppName(appName, node);
			}
			xmlFile.save();
		} catch (IOException e) {
			throw new InstallException("修改jboss.xml中的默认应用报错", e);
		} catch (XPathExpressionException e) {
			throw new InstallException("修改jboss.xml中的默认应用报错", e);
		} catch (TransformerException e) {
			throw new InstallException("修改jboss.xml中的默认应用报错", e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private void replaceByAppName(String appName, Node node) {
		String content = node.getTextContent();
		int firstIndex = content.indexOf('/');
		int second = -1;
		char[] chars = content.toCharArray();
		for (int j = 0; j < chars.length; j++) {
			char c = chars[j];
			if ((c == '/') && (j > firstIndex)) {
				second = j;
				break;
			}
		}
		if (second > 0) {
			content = content.substring(0, firstIndex) + "/" + appName + content.substring(second);
		}
		node.setTextContent(content);
	}

	private void modifyNode(String appName, Node node) {
		String content = node.getTextContent();
		int index = content.indexOf("/");
		if (index > -1) {
			content = appName + content.substring(index);
			node.setTextContent(content);
		}
	}

	public static void main(String[] args) {
		UpdateJbossEJBConfig config = new UpdateJbossEJBConfig();
		InstallContext context = new InstallContext();
		context.setValue("DEFAULT_APP_NAME", "test");
		Map parameters = new HashMap();
		parameters.put("JBOSS_EJB_JAR_XML_PATH", "D:/jboss.xml");
		config.execute(context, parameters);
	}
}