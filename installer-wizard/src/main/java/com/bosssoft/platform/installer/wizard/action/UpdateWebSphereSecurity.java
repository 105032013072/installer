package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.XmlHelper;

public class UpdateWebSphereSecurity implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String profile = parameters.get("PROFILE_HOME").toString();
		String cellName = parameters.get("CELL_NAME").toString();
		String value = parameters.get("VALUE").toString();
		String secFilePath = profile + "/config/cells/" + cellName + "/security.xml";

		File file = new File(secFilePath);
		if (!file.exists()) {
			this.logger.debug("WAS Security file not found!" + secFilePath);
			return;
		}

		if ((value.equals("true")) && (context.getStringValue("AS_IS_SECURITY").equals("true"))) {
			modifySecurity(secFilePath, "true");
		} else if (isSecurity(secFilePath)) {
			context.setValue("AS_IS_SECURITY", "true");
			modifySecurity(secFilePath, "false");
		}
	}

	public boolean isSecurity(String filePath) {
		boolean tag = false;
		try {
			Document document = XmlHelper.parse(new File(filePath));
			Element root = document.getRootElement();
			if (root == null) {
				this.logger.info("node is null!");
			} else {
				String value = root.attributeValue("enabled");
				if ("true".equalsIgnoreCase(value))
					tag = true;
			}
			XmlHelper.saveAs(filePath, document, "UTF-8");
		} catch (Exception e) {
			this.logger.warn(e);
			e.printStackTrace();
		}
		return tag;
	}

	public void modifySecurity(String filePath, String value) {
		this.logger.info("modify filePath:" + filePath);
		Document xml = null;
		try {
			xml = XmlHelper.parse(new File(filePath));
			Element root = xml.getRootElement();
			if (root == null) {
				this.logger.warn("root is null");
			} else {
				root.addAttribute("enabled", value);
				root.addAttribute("issuePermissionWarning", value);
				XmlHelper.saveAs(filePath, xml, "UTF-8");
			}
		} catch (Exception e) {
			throw new InstallException("Modify Security file failed!", e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}