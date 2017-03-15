package com.bosssoft.platform.installer.core.util;

import java.io.IOException;
import org.apache.log4j.Logger;

public class JDKUtil {
	static transient Logger logger = Logger.getLogger(JDKUtil.class);

	public static String execJavaCmd(String javahome) throws IOException {
		String cmd = "";
		String os = System.getProperty("os.name");
		if (os.toLowerCase().indexOf("window") >= 0) {
			cmd = "\"" + javahome + "/bin/java.exe\" -version";
		} else
			cmd = javahome + "/bin/java -version";

		String result = null;
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			result = ProcessOutputReader.read(proc);
			proc.waitFor();
		} catch (IOException e) {
			logger.warn(e);
			throw e;
		} catch (InterruptedException e) {
			logger.warn(e);
			result = e.getMessage();
		}
		return result;
	}

	public static String getJavaVersion(String result) {
		int beginIndex = result.indexOf("\"1.");
		String str2 = result.substring(beginIndex + 1);
		int ednIndex = str2.indexOf("\"");
		String version = str2.substring(0, ednIndex);
		return version;
	}

	public static String getJDKProvider(String result) {
		String JDKProvider = "Sun";
		if (result.toUpperCase().indexOf("IBM") >= 0)
			JDKProvider = "IBM";
		else if (result.toLowerCase().indexOf("jrockit") >= 0) {
			JDKProvider = "BEA";
		}
		return JDKProvider;
	}
}