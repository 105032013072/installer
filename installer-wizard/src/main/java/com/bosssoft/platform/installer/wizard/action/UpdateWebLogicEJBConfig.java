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

public class UpdateWebLogicEJBConfig implements IAction {
	private static final String WEBLOGIC_EJB_JAR_CONF_PATH = "WEBLOGIC_EJB_JAR_CONF_PATH";

	public void execute(IContext context, Map parameters) throws InstallException {
		String appName = context.getStringValue("DEFAULT_APP_NAME");
		String config = parameters.get("WEBLOGIC_EJB_JAR_CONF_PATH").toString();
		try {
			XmlFile xmlFile = new XmlFile(new File(config));

			NodeList nodeList = xmlFile.findNodes("/weblogic-ejb-jar/weblogic-enterprise-bean/jndi-name");
			int len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				modifyNode(appName, node);
			}

			nodeList = xmlFile.findNodes("/weblogic-ejb-jar/weblogic-enterprise-bean/local-jndi-name");
			len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				String content = node.getTextContent();
				content = replaceByAppName(appName, content);
				node.setTextContent(content);
			}

			nodeList = xmlFile.findNodes("/weblogic-ejb-jar/weblogic-enterprise-bean/message-driven-descriptor/destination-jndi-name");
			len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Node node = nodeList.item(i);
				modifyNode(appName, node);
			}
			xmlFile.save();
		} catch (IOException e) {
			throw new InstallException(e);
		} catch (TransformerException e) {
			throw new InstallException(e);
		} catch (XPathExpressionException e) {
			throw new InstallException(e);
		}
	}

	private String replaceByAppName(String appName, String content) {
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
		return content;
	}

	private void modifyNode(String appName, Node node) {
		String content = node.getTextContent();
		int index = content.indexOf("/");
		if (index > -1) {
			content = appName + content.substring(index);
			node.setTextContent(content);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public static void main(String[] args) {
		UpdateWebLogicEJBConfig config = new UpdateWebLogicEJBConfig();
		InstallContext context = new InstallContext();
		context.setValue("DEFAULT_APP_NAME", "test");
		Map parameters = new HashMap();
		parameters.put("WEBLOGIC_EJB_JAR_CONF_PATH", "D:/weblogic-ejb-jar.xml");
		config.execute(context, parameters);
	}
}