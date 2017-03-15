package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.core.util.JDKUtil;
import com.bosssoft.platform.installer.core.util.XmlHelper;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.wizard.ContextKeys;

public class DeployJDK implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String appsvrType = parameters.get("APPSVR_TYPE").toString().toLowerCase();

		String currentJavaHome = getCurrentJavaHome().replace('\\', '/');
		String installerJrePath = InstallerFileManager.getInstallerRoot() + "/jre";
		installerJrePath = installerJrePath.replace('\\', '/');
		String javaHome = null;

		if (appsvrType.equals("weblogic")) {
			String version = context.getStringValue("APP_SERVER_VERSION");
			String beaHome = context.getStringValue("AS_WL_BEA_HOME");
			javaHome = getWeblogicJavaHome(beaHome);
			if (javaHome == null) {
				javaHome = context.getStringValue("INSTALL_DIR") + "/jdk";
				String jdkZipPath = null;
				if (version.equals("10.3"))
					jdkZipPath = InstallerFileManager.getResourcesDir() + "/public_comps/jdk/1.6/jdk.zip";
				else
					jdkZipPath = InstallerFileManager.getResourcesDir() + "/public_comps/jdk/1.5/jdk.zip";
				try {
					FileUtils.unzip(new File(jdkZipPath), new File(javaHome), null, FileOperationMessageListener.INSTANCE);
					FileUtils.chmod(new File(javaHome), 1, "+");
				} catch (OperationException e) {
					this.logger.error(e);
				}
			}
		} else if (appsvrType.equals("websphere")) {
			String wasHome = context.getStringValue("AS_WAS_HOME");
			javaHome = wasHome + File.separator + "java";
		} else if (!currentJavaHome.equals(installerJrePath)) {
			if (currentJavaHome.endsWith("jre")) {
				currentJavaHome = currentJavaHome.substring(0, currentJavaHome.length() - 4);
			}
			if (isValidJDK(currentJavaHome))
				javaHome = currentJavaHome;
		} else {
			javaHome = context.getStringValue("INSTALL_DIR") + "/jdk";
			String jdkZipPath = InstallerFileManager.getResourcesDir() + "/public_comps/jdk/1.5/jdk.zip";
			try {
				FileUtils.unzip(new File(jdkZipPath), new File(javaHome), null, FileOperationMessageListener.INSTANCE);
				FileUtils.chmod(new File(javaHome), 1, "+");
			} catch (OperationException e) {
				this.logger.error(e);
			}
		}

		context.setValue(ContextKeys.JAVA_HOME, javaHome);
	}

	private boolean isValidJDK(String javahome) {
		boolean isValid = false;
		String result = "";
		try {
			result = JDKUtil.execJavaCmd(javahome);
		} catch (IOException e) {
			return false;
		}
		String version = JDKUtil.getJavaVersion(result);
		String jdkProvider = JDKUtil.getJDKProvider(result);
		String regex = "";
		if (jdkProvider.equalsIgnoreCase("SUN")) {
			regex = "^1.5.0_09$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(version);
			isValid = m.matches();

			if (!isValid) {
				regex = "^1.5.[0-9]_[1-9]{0,2}$";
				p = Pattern.compile(regex);
				m = p.matcher(version);
				isValid = m.matches();
			}
		} else if (jdkProvider.equalsIgnoreCase("IBM")) {
			regex = "^1.5.[0-9]+$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(version);
			isValid = m.matches();
		} else if (jdkProvider.equalsIgnoreCase("HP")) {
			regex = "^1.5.0.09$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(version);
			isValid = m.matches();
		} else {
			isValid = false;
		}
		return isValid;
	}

	private String getWeblogicJavaHome(String beaHome) {
		String javahome = null;
		File wlRegFile = new File(beaHome + "/registry.xml");
		Document doc = null;
		try {
			doc = XmlHelper.parse(wlRegFile);
		} catch (DocumentException doe) {
			return null;
		}

		List<Element> elems = doc.selectNodes("//bea-product-information/host/java-installation");
		String vendor = null;

		for (Element elem : elems) {
			vendor = elem.attributeValue("JavaVendor");
			if ("sun".equalsIgnoreCase(vendor)) {
				javahome = elem.attributeValue("JavaHome");

				break;
			}

		}

		if (javahome == null) {
			Element elem = (Element) doc.selectSingleNode("//bea-product-information/host/product/release");
			if ((elem == null) || (elem.attributeValue("JavaHome") == null))
				return null;
			javahome = elem.attributeValue("JavaHome").replace("\\", "/");
		}

		return javahome;
	}

	private String getCurrentJavaHome() {
		String currentJavaHome = System.getProperty("java.home");

		return currentJavaHome;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}