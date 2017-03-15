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
import com.bosssoft.platform.installer.core.util.UpdateCmdScriptUtil;
import com.bosssoft.platform.installer.wizard.ContextKeys;

public class UpdateTomcatRunScripts implements IAction {
	transient Logger logger = Logger.getLogger(getClass());
	public static final String MODIFY_DOMAIN_LIB = "/uninstall/lib";
	public static final String STA_SH = "startServer.sh";
	public static final String WLCLUSTER_SH = "install_wlcluster.sh";
	public static final String STOP_SH = "stopServer.sh";
	public static final String UNINSTALL_SH = "uninstall.sh";
	public static final String STA_CMD = "startServer.cmd";
	public static final String WLCLUSTER_CMD = "install_wlcluster.cmd";
	public static final String STOP_CMD = "stopServer.cmd";
	public static final String UNINSTALL_CMD = "uninstall.cmd";

	public void execute(IContext context, Map parameters) throws InstallException {
		String tomcat_home = context.getStringValue("AS_TOMCAT_HOME");
		String externalConfigDir = context.getStringValue("INSTALL_DIR") + "/apps_config";
		String installDir = context.getStringValue("INSTALL_DIR").replace('\\', '/');

		String os = System.getProperty("os.name").toLowerCase();

		String java_home = context.getStringValue(ContextKeys.JAVA_HOME);
		String lib_dir = installDir + "/uninstall/lib";
		String result = "";
		try {
			result = JDKUtil.execJavaCmd(java_home);
		} catch (IOException e) {
			throw new InstallException(e);
		}
		String jdkProvider = JDKUtil.getJDKProvider(result);

		Properties props = new Properties();
		props.setProperty("RP_JAVA_HOME", java_home);
		props.setProperty("RP_APPS_CONFIG", externalConfigDir);
		props.setProperty("RP_TOMCAT_HOME", tomcat_home);
		props.setProperty("RP_LIB_BASE", lib_dir);

		if ((jdkProvider.equalsIgnoreCase("sun")) && ("DE".equalsIgnoreCase(context.getStringValue("EDITION"))))
			props.setProperty("RP_HeapDump", "-XX:+HeapDumpOnOutOfMemoryError");
		else {
			props.setProperty("RP_HeapDump", " ");
		}

		if (jdkProvider.equalsIgnoreCase("ibm")) {
			props.setProperty("RP_GcPOLICY", "-Xgcpolicy:gencon");
			this.logger.info("[Replace] RP_GcPolicy=-Xgcpolicy:gencon");
		} else {
			props.setProperty("RP_GcPolicy", " ");
		}

		replaceVariable(installDir, props);

		if (os.indexOf("window") < 0)
			addExecutePermission(installDir, tomcat_home);
	}

	private void addExecutePermission(String installDir, String tomcat_home) {
		String[] configFiles = { installDir + "/" + "startServer.sh", installDir + "/" + "stopServer.sh", tomcat_home + "/bin/*" };

		UpdateCmdScriptUtil.addExecutePermission(configFiles);
	}

	private void replaceVariable(String installDir, Properties props) {
		String os = System.getProperty("os.name").toLowerCase();
		String[] configFiles = (String[]) null;
		if (os.indexOf("window") >= 0) {
			if (new File(installDir + "/uninstall/" + "uninstall.cmd").exists())
				configFiles = new String[] { installDir + "/" + "startServer.cmd", installDir + "/" + "stopServer.cmd", installDir + "/uninstall/" + "uninstall.cmd" };
			else
				configFiles = new String[] { installDir + "/" + "startServer.cmd", installDir + "/" + "stopServer.cmd" };
		} else {
			configFiles = new String[] { installDir + "/" + "startServer.sh", installDir + "/" + "stopServer.sh" };
		}
		UpdateCmdScriptUtil.replaceVariable(configFiles, props);
	}

	public boolean checkFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			this.logger.error("File " + filePath + " doesn't exist!");
			return false;
		}
		return true;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}