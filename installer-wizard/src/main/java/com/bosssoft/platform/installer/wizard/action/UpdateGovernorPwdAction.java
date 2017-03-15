package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class UpdateGovernorPwdAction implements IAction {
	private static final String GOVERNOR_XML_PATH = "GOVERNOR_XML_PATH";
	private static final String PASSWORD = "PASSWORD";

	public void execute(IContext context, Map parameters) throws InstallException {
		String xmlpath = parameters.get("GOVERNOR_XML_PATH").toString();
		String password = parameters.get("PASSWORD").toString();
		try {
			XmlFile xmlFile = new XmlFile(new File(xmlpath));
			Node node = xmlFile.findNode("/governor/module[@name=\"UserManager\"]/group[@name=\"Default\"]/configValue[@key=\"Password\"]");
			if (node == null) {
				throw new InstallException("修改系统密码失败!", new NullPointerException("Could not find the node for Password!"));
			}
			node.setTextContent(password);
			xmlFile.save();
		} catch (IOException e) {
			throw new InstallException("修改系统密码失败!", e);
		} catch (XPathExpressionException e) {
			throw new InstallException("修改系统密码失败!", e);
		} catch (TransformerException e) {
			throw new InstallException("修改系统密码失败!", e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public static void main(String[] args) {
		UpdateGovernorPwdAction action = new UpdateGovernorPwdAction();
		Map parameters = new HashMap();
		parameters.put("GOVERNOR_XML_PATH", "D:/governor.xml");
		parameters.put("PASSWORD", "dfsdfs12345");
		action.execute(null, parameters);
	}
}