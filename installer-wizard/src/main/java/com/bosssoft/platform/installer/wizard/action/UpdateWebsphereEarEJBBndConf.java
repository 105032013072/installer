package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.impl.context.InstallContext;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class UpdateWebsphereEarEJBBndConf implements IAction {
	private static final String EAR_EJB_BND_CONF_PATH = "EAR_EJB_BND_CONF_PATH";

	public void execute(IContext context, Map parameters) throws InstallException {
		String config = parameters.get("EAR_EJB_BND_CONF_PATH").toString();
		String appName = context.getStringValue("DEFAULT_APP_NAME");
		try {
			XmlFile xmlFile = new XmlFile(new File(config));
			NodeList nodeList = xmlFile.findNodes("//ejbBindings");

			int len = nodeList.getLength();
			for (int i = 0; i < len; i++) {
				Element element = (Element) nodeList.item(i);
				modifyAttr("jndiName", appName, element);
				modifyAttr("activationSpecJndiName", appName, element);
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

	private void modifyAttr(String attrName, String appName, Element element) {
		String value = element.getAttribute(attrName);
		if (StringUtils.isEmpty(value)) {
			return;
		}
		int index = value.indexOf('/');
		if (index > -1) {
			value = appName + value.substring(index);
			element.setAttribute(attrName, value);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public static void main(String[] args) {
		UpdateWebsphereEarEJBBndConf config = new UpdateWebsphereEarEJBBndConf();
		InstallContext context = new InstallContext();
		context.setValue("DEFAULT_APP_NAME", "test");
		Map parameters = new HashMap();
		parameters.put("EAR_EJB_BND_CONF_PATH", "D:/ibm-ejb-jar-bnd.xmi");
		config.execute(context, parameters);
	}
}