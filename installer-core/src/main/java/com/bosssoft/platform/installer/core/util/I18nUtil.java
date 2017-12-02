package com.bosssoft.platform.installer.core.util;

import java.io.File;
import java.util.Locale;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.i18n.ResourceMessageUtil;
import com.bosssoft.platform.installer.core.i18n.ResourceRegister;

public class I18nUtil {
	protected static Logger logger = Logger.getLogger(I18nUtil.class);
	private static Locale currentLocale = null;

	static {
		if (Locale.getDefault().toString().equals("zh_CN")){
			currentLocale = Locale.getDefault();
		}
			
		else {
			currentLocale = Locale.ENGLISH;
		}
		File i18N_Dir = new File(InstallerFileManager.getInstallerHome() + "/i18n");
		File[] i18nFiles = i18N_Dir.listFiles();
		for (int i = 0; i < i18nFiles.length; i++)
			if (i18nFiles[i].getName().endsWith(".properties"))
				ResourceRegister.registerResource("I18N", i18nFiles[i].getAbsolutePath());
	}

	public static Locale getCurrentLocale() {
		return currentLocale;
	}

	public static String getString(String cID) {
		return getString(cID, currentLocale);
	}

	public static String getString(String cID, String defaultValue) {
		return getString(cID, currentLocale, defaultValue);
	}

	private static String getString(String cID, Locale currentLocale) {
		String message = ResourceMessageUtil.getI18nResourceMessage(cID, currentLocale);

		return ExpressionParser.parseString(message);
	}

	public static String getString(String cID, Locale currentLocale, String defaultValue) {
		String value = ResourceMessageUtil.getI18nResourceMessage(cID, currentLocale);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
}