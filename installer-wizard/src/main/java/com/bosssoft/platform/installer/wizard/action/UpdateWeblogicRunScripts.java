package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.JDKUtil;
import com.bosssoft.platform.installer.core.util.StringConvertor;
import com.bosssoft.platform.installer.core.util.UpdateCmdScriptUtil;
import com.bosssoft.platform.installer.wizard.ContextKeys;

public class UpdateWeblogicRunScripts implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String[] fileNames = { "startServer.cmd", "startServer.sh", "stopServer.cmd", "stopServer.sh" };

		String beaHome = context.getStringValue("AS_WL_BEA_HOME");
		String wlHome = context.getStringValue("AS_WL_HOME");
		String domainHome = context.getStringValue("AS_WL_DOMAIN_HOME");
		String externalConfigDir = context.getStringValue("INSTALL_DIR") + "/apps_config";

		String javaHome = context.getStringValue(ContextKeys.JAVA_HOME);
		String jdkDescription = "";
		try {
			jdkDescription = JDKUtil.execJavaCmd(javaHome);
		} catch (IOException e1) {
			throw new InstallException(e1);
		}
		String jdkProvider = JDKUtil.getJDKProvider(jdkDescription);

		String installDir = context.getStringValue("INSTALL_DIR");

		Properties props = new Properties();

		props.setProperty("RP_BEA_HOME", beaHome);
		props.setProperty("RP_APPS_CONFIG", externalConfigDir);
		props.setProperty("RP_WEBLOGIC_HOME", wlHome);
		props.setProperty("RP_JAVA_VENDOR", jdkProvider);
		props.setProperty("RP_JAVA_HOME", javaHome);
		props.setProperty("RP_DOMAIN_HOME", domainHome);

		String adminUrl = context.getStringValue("USER_IP");
		String port = context.getStringValue("AS_WL_WEBSVR_PORT");
		adminUrl = "t3://" + adminUrl + ":" + port;
		props.setProperty("RP_ADMIN_URL", adminUrl);

		for (int i = 0; i < fileNames.length; i++) {
			String cmdFile = installDir + "/" + fileNames[i];
			if (new File(cmdFile).exists()) {
				try {
					if (cmdFile.endsWith(".sh"))
						StringConvertor.replaceUnixFile(cmdFile, props);
					else
						StringConvertor.replaceFile(cmdFile, props);
				} catch (IOException e) {
					this.logger.debug(e);
				}
			}
		}

		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("window") == -1)
			for (int i = 0; i < fileNames.length; i++) {
				String fileName = fileNames[i];
				if (fileName.endsWith(".sh"))
					UpdateCmdScriptUtil.addExeMode(installDir + File.separator + fileName);
			}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}