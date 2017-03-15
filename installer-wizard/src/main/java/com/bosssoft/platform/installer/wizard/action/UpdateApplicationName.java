package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class UpdateApplicationName implements IAction {
	private static final String APPXML_PATH = "APPXML_PATH";
	private static final String APP_NAME = "APP_NAME";
	private static final long serialVersionUID = -2744273154600296214L;

	public void execute(IContext context, Map parameters) throws InstallException {
		String appXMLPath = parameters.get("APPXML_PATH").toString();
		String appName = parameters.get("APP_NAME").toString();
		try {
			XmlFile xmlFile = new XmlFile(new File(appXMLPath));

			Element element = (Element) xmlFile.findNode("/application");
			if (element != null) {
				element.setAttribute("id", appName);
			}

			element = (Element) xmlFile.findNode("/application/display-name");
			if (element != null) {
				element.setTextContent(appName);
			}

			element = (Element) xmlFile.findNode("/application/module[@id=\"DefaultWebModule\"]/web/web-uri");
			if (element != null) {
				String textContent = element.getTextContent();
				String weburi = appName;
				if ((textContent != null) && (textContent.toString().endsWith(".war"))) {
					weburi = appName + ".war";
				}
				element.setTextContent(weburi);
			}

			element = (Element) xmlFile.findNode("/application/module[@id=\"DefaultWebModule\"]/web/context-root");
			if (element != null) {
				element.setTextContent("/" + appName);
			}
			xmlFile.save();
		} catch (IOException e) {
			throw new InstallException(e.getMessage(), e);
		} catch (XPathExpressionException e) {
			throw new InstallException(e.getMessage(), e);
		} catch (TransformerException e) {
			throw new InstallException(e.getMessage(), e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}