package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class SetXMLNodeValue implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String filePath = parameters.get("FILE_PATH").toString();
		String xql = parameters.get("XPATH").toString();
		String value = parameters.get("NODE_VALUE").toString();
		try {
			setNodeValue(filePath, xql, value);
		} catch (Exception e) {
			this.logger.error(e);
		}
	}

	protected void setNodeValue(String filePath, String xql, String value) throws Exception {
		XmlFile xf = new XmlFile(new File(filePath));

		xf.setNodeValue(xql, value);
		xf.save();
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public static void main(String[] args) {
		SetXMLNodeValue action = new SetXMLNodeValue();

		String filePath = "D:\\primeton\\BPS1498wl\\bpsserver\\working\\default\\config\\user-config.xml";
		String xql = "//module[@name='Access-Http']/group[@name='Accessed-Mode']/configValue[@key='Portal']";
		String value = "true";
		try {
			action.setNodeValue(filePath, xql, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}