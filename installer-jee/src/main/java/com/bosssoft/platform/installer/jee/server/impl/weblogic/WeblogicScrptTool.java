package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.io.util.OS;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.internal.util.ProcessReader;
import com.bosssoft.platform.installer.jee.server.internal.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class WeblogicScrptTool {
	public static final String RESULT_SUCCEED = "com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_SUCCEED";
	public static final String RESULT_FAILED = "com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_FAILED";
	public static final String RESULT_EXCEPTION = "com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_EXCEPTION";
	public static final String RESULT_FILE_NAME = "wlst_install.log";
	private static Logger logger = Logger.getLogger(WeblogicScrptTool.class.getName());

	public static File getWlstCmd(String weblogicHome) {
		if (!new File(weblogicHome).exists()) {
			throw new IllegalArgumentException("the weblogic home is not exist!" + weblogicHome);
		}

		String cmdFile = OS.isMicroWindows() ? "wlst.cmd" : "wlst.sh";

		File wlstExc = new File(weblogicHome, "common" + File.separator + "bin" + File.separator + cmdFile);

		return wlstExc;
	}

	public static void runWLST(RunWlstCmdArgs arg) throws IOException, JEEServerOperationException {
		Runtime runtime = Runtime.getRuntime();

		String[] cmd = new String[3];
		if (OS.isMicroWindows()) {
			cmd[0] = "cmd.exe";
			cmd[1] = "/c";
		} else {
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
		}

		String wlst = arg.getWlstCmdFile().getAbsolutePath();
		String wlstPython = arg.getPythonFile().getAbsolutePath();
		String properties = arg.getPropertiesFile().getAbsolutePath();
		properties = "-loadProperties " + properties;
		cmd[2] = (wlst + ' ' + wlstPython + ' ' + properties);
		try {
			FileUtils.chmod(new File(wlst), 1, "+");
		} catch (OperationException localOperationException) {
		}
		Process process = runtime.exec(cmd);
		try {
			long timeout = 600000L;
			String str = ProcessReader.readAll(process, timeout);
			logger.info(str);

			if (!readResultLogFile())
				throw new JEEServerOperationException("The command excute failed!", new Exception(str));
		} finally {
			process.destroy();
		}
		process.destroy();
	}

	public static File getResultLogFile() {
		File tempDir = Util.getTempDir();
		return new File(tempDir, "wlst_install.log");
	}

	private static boolean readResultLogFile() throws JEEServerOperationException {
		File file = getResultLogFile();
		if (!file.exists()) {
			throw new JEEServerOperationException("The command excute failed!");
		}

		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			String str = IOUtils.toString(is);
			if ((str == null) || (str.trim().equals(""))) {
				throw new JEEServerOperationException("The command excute failed!");
			}
			if (str.indexOf("com.primeton.install.jee.server.impl.weblogic.WeblogicScrptTool#RESULT_SUCCEED") != -1) {
				return true;
			}

			return false;
		} catch (IOException e) {
			throw new JEEServerOperationException("Could not read the result file!", e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}