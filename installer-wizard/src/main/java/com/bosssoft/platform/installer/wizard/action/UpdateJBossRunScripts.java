package com.bosssoft.platform.installer.wizard.action;

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

public class UpdateJBossRunScripts implements IAction {
	transient Logger logger = Logger.getLogger(getClass());
	public static final String MODIFY_DOMAIN_LIB = "/uninstall/lib";

	public void execute(IContext context, Map parameters) throws InstallException {
		String[] fileNames = { "startServer.cmd", "stopServer.cmd", "startServer.sh", "stopServer.sh" };

		String jbossHome = context.getStringValue("AS_JBOSS_HOME");
		String externalConfigDir = context.getStringValue("INSTALL_DIR") + "/apps_config";

		String os = System.getProperty("os.name").toLowerCase();
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
		props.setProperty("RP_JAVA_HOME", javaHome);
		props.setProperty("RP_APPS_CONFIG", externalConfigDir);
		props.setProperty("RP_JBOSS_HOME", jbossHome);
		props.setProperty("RP_JDK_PROVIDER", jdkProvider);

		if (os.indexOf("window") >= 0) {
			String startCmdFile = installDir + "/" + fileNames[0];
			String stopCmdFile = installDir + "/" + fileNames[1];
			try {
				StringConvertor.replaceFile(startCmdFile, props);
				StringConvertor.replaceFile(stopCmdFile, props);
			} catch (IOException e) {
				this.logger.debug(e);
			}
		} else {
			String startShFile = installDir + "/" + fileNames[2];
			String stopShFile = installDir + "/" + fileNames[3];
			UpdateCmdScriptUtil.addExeMode(jbossHome + "/bin/*");
			UpdateCmdScriptUtil.addExeMode(startShFile);
			UpdateCmdScriptUtil.addExeMode(stopShFile);
			try {
				StringConvertor.replaceUnixFile(startShFile, props);
				StringConvertor.replaceUnixFile(stopShFile, props);
			} catch (IOException e) {
				this.logger.debug(e);
			}
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}