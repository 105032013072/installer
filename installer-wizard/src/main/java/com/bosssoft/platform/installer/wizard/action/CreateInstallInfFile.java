package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CreateInstallInfFile implements IAction {
	private Logger logger = Logger.getLogger(getClass());
	public static final String INSTALL_INF_FILE = "install.inf";

	public void execute(IContext context, Map parameters) throws InstallException {
		String sh_file = context.getStringValue("INSTALL_DIR") + "/tools/bin/deployResources.sh";
		try {
			FileUtils.chmod(new File(sh_file), 1, "+");
		} catch (OperationException localOperationException) {
		}

		if (Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_CLUSTER"))) {
			return;
		}

		String filePath = context.getStringValue("INSTALL_DIR") + File.separator + "install.inf";
		String appServerType = context.getStringValue("APP_SERVER_NAME");
		String appServerHome = "";
		if (appServerType.equalsIgnoreCase("Tomcat")) {
			appServerHome = context.getStringValue("AS_TOMCAT_HOME");
		}
		if (appServerType.equalsIgnoreCase("JBoss")) {
			appServerHome = context.getStringValue("AS_JBOSS_HOME");
		}
		if (appServerType.equalsIgnoreCase("WebLogic")) {
			appServerHome = context.getStringValue("AS_WL_DOMAIN_HOME");
		}
		if (appServerType.equalsIgnoreCase("WebSphere")) {
			appServerHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		}
		createInstallInfFile(filePath, appServerType, appServerHome.replaceAll("\\\\", "/"));
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private void createInstallInfFile(String filePath, String appServerType, String appServerHome) {
		File file = new File(filePath);
		if (file.exists()) {
			return;
		}
		Properties p = new Properties();
		p.setProperty("appServerType", appServerType);
		p.setProperty("appServerHome", appServerHome);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filePath);
			p.store(out, null);
		} catch (IOException e) {
			this.logger.error(e);
			try {
				if (out != null)
					out.close();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		}
	}
}