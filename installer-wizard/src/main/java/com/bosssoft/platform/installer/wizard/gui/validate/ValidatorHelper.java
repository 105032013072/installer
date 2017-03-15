package com.bosssoft.platform.installer.wizard.gui.validate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.dom4j.Element;
import org.w3c.dom.Node;

import com.bosssoft.platform.installer.core.util.JDKUtil;
import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.wizard.gui.validate.impl.LengthValidator;
import com.bosssoft.platform.installer.wizard.gui.validate.impl.PatternValidator;

public class ValidatorHelper {
	public static boolean isBlankOrNull(String value) {
		return (value == null) || (value.trim().length() == 0);
	}

	public static boolean isBetween(String value, int min, int max) {
		IValidator validator = new LengthValidator(min, max);
		return validator.isValid(value);
	}

	public static boolean isInteger(String value) {
		if (value == null) {
			return false;
		}
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	public static boolean isLong(String value) {
		if (value == null)
			return false;
		try {
			Long.parseLong(value);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean validateWeblogicVersion(String beaHome, String wlVersion) {
		File wlRegFile = new File(beaHome + "/registry.xml");

		if (!wlRegFile.exists())
			return false;
		XmlFile xmlFile = null;
		try {
			xmlFile = new XmlFile(wlRegFile);
		} catch (Exception doe) {
			return false;
		}
		Node node = null;
		try {
			node = xmlFile.findNode("//bea-product-information/host/product/release[@level='" + wlVersion + "']");
		} catch (XPathExpressionException e) {
			return false;
		}
		if (node == null) {
			return false;
		}

		return true;
	}

	public static boolean validateWeblogicHome(String wlHome) {
		String dirPath = "/server/native";
		File file = new File(wlHome + dirPath);
		return file.exists();
	}

	public static boolean isValidPatchAndJDK(String beaHome, String wlVersion) {
		if ((System.getProperty("os.name").toLowerCase().indexOf("aix") >= 0) && (wlVersion.equals("9.2"))) {
			String filePath = beaHome + File.separator + "registry.xml";

			XmlFile xmlFile = null;
			try {
				xmlFile = new XmlFile(new File(filePath));
			} catch (Exception ex) {
				return false;
			}

			Element element = null;
			try {
				element = (Element) xmlFile.findNode("/bea-product-information/host/product/release");
			} catch (Exception e1) {
				return false;
			}
			if (element == null) {
				return false;
			}
			String ServicePackLevel = element.attributeValue("ServicePackLevel");
			try {
				int i = Integer.parseInt(ServicePackLevel);
				if (i < 2)
					return false;
			} catch (Exception e) {
				return false;
			}

			String java_home = element.attributeValue("JavaHome");
			String result = "";
			try {
				result = JDKUtil.execJavaCmd(java_home);
			} catch (IOException e) {
				return false;
			}
			if (!"IBM".equals(JDKUtil.getJDKProvider(result))) {
				return false;
			}
		}

		return true;
	}

	public static boolean validateURLCoincident(String db_type, String ip, String port, String schema, String server, String dbUrl) {
		String url_tmp = "";

		if (db_type.startsWith("db2"))
			url_tmp = "jdbc:db2://" + ip + ":" + port + "/" + schema;
		else if (db_type.startsWith("oracle"))
			url_tmp = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + schema;
		else if (db_type.startsWith("informix"))
			url_tmp = "jdbc:informix-sqli://" + ip + ":" + port + ":INFORMIXSERVER=" + server + ";Database=" + schema;
		else if (db_type.startsWith("mysql"))
			url_tmp = "jdbc:mysql://" + ip + ":" + port + "/" + schema;
		else if ((db_type.startsWith("sqlserver")) || (db_type.startsWith("sql server")))
			url_tmp = "jdbc:microsoft:sqlserver://" + ip + ":" + port + ";DatabaseName=" + schema + ";SelectMethod=Cursor";
		else if (db_type.startsWith("sybase"))
			url_tmp = "jdbc:sybase:Tds:" + ip + ":" + port + "/" + schema + "?CHARSET=utf8";
		else {
			return false;
		}

		if (!url_tmp.trim().equals(dbUrl)) {
			return false;
		}
		return true;
	}

	public static boolean isPatternValid(String value, String pattern) {
		PatternValidator patternValidator = new PatternValidator();
		patternValidator.setPattern(pattern);
		return patternValidator.isValid(value);
	}

	public static boolean isContainsFileOrDir(String dir, String[] names) {
		File t_home = new File(dir);
		if ((!t_home.exists()) || (t_home.isFile())) {
			return false;
		}
		String[] fNames = t_home.list();
		List nameList = Arrays.asList(fNames);
		boolean flag = true;
		for (String name : names) {
			if (!nameList.contains(name)) {
				flag = false;
			}
		}
		return flag;
	}
}