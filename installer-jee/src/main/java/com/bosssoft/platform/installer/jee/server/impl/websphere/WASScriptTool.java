package com.bosssoft.platform.installer.jee.server.impl.websphere;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.bosssoft.platform.installer.io.util.OS;
import com.bosssoft.platform.installer.jee.server.internal.util.ProcessReader;
import com.bosssoft.platform.installer.jee.server.internal.util.Util;

public class WASScriptTool {
	public static final String SCRIPT_APP_START = "app_start.jacl";
	public static final String SCRIPT_APP_STOP = "app_stop.jacl";
	public static final String SCRIPT_APP_UNINSTALL = "app_uninstall.jacl";
	public static final String SCRIPT_CHECK_SECURITY = "checkSecurity.jacl";
	public static final String SCRIPT_CONFIG_STANDALONE_JMS = "config_standalone_jms.jacl";
	public static final String SCRIPT_CONFIG_CLUSTER_JMS = "config_cluster_jms.jacl";
	public static final String SCRIPT_CONFIG_CLUSTER_QUEUE = "config_cluster_queue.jacl";
	public static final String SCRIPT_CREATE_QUEUE = "create_queue.jacl";
	public static final String SCRIPT_CUSTOM_SERVICE = "customService.jacl";
	public static final String SCRIPT_DELETE_DS = "delete_ds.jacl";
	public static final String SCRIPT_DELETE_QUEUE = "delete_queue.jacl";
	public static final String SCRIPT_DEPLOY_APP = "deploy_app.jacl";
	public static final String SCRIPT_DEPLOY_CLUSTER_APP = "deploy_cluster_app.jacl";
	public static final String SCRIPT_CREATE_DATASOURCE = "create_datasource.jacl";
	public static final String SCRIPT_CREATE_CLUSTER_DATASOURCE = "create_cluster_datasource.jacl";
	public static final String SCRIPT_SEARCH_DS = "search_ds.jacl";
	public static final String SCRIPT_SHAREDLIB = "sharedLib.jacl";
	public static final String SCRIPT_JVM_PARAM = "set_jvm.jacl";
	public static final String SCRIPT_WEBCONTAINER_PARAM = "set_webcontainer_param.jacl";
	public static final String SCRIPT_CLUSTER_JVM_PARAM = "set_cluster_jvm.jacl";
	private static String Result_START_SUCC = "ADMU3000I";

	private static String Result_START_WARN = "ADMU3011E";

	private static String Result_START_FAIL = "ADMU3007E";

	private static String Result_STOP_SUCC = "ADMU4000I";

	private static String Result_STOP_FAIL = "ADMU3007E";

	private static String Result_SERVER_STARTED = "ADMU0508I";

	private static String Result_SERVER_STOPED = "ADMU0509I";

	public static int EXEC_SUCC = 100;

	public static int EXEC_WARN = 0;

	public static int EXEC_FAIL = -1;

	public static int STATUS_STARTED = 8;

	public static int STATUS_STOPED = 9;

	private static File scriptDir = null;

	public static int startServer(String profilesHome, String serverName) {
		String[] cmdLine = new String[2];
		cmdLine[0] = getCmd0(profilesHome, "startServer");
		cmdLine[1] = serverName;

		logCmdlines("Start websphere. Execute command:", cmdLine, -1);
		String result = executeCmd(cmdLine);
		return execStatus(result);
	}

	public static int startNDServer(String profileHome, String serverName) {
		String[] cmdLine = new String[2];
		cmdLine[0] = getCmd0(profileHome, "startManager");
		cmdLine[1] = serverName;

		logCmdlines("Start websphere. Execute command:", cmdLine, -1);
		String result = executeCmd(cmdLine);
		return execStatus(result);
	}

	public static int stopServer(String profilesHome, String serverName) {
		String[] cmdLine = new String[2];
		cmdLine[0] = getCmd0(profilesHome, "stopServer");
		cmdLine[1] = serverName;

		logCmdlines("Stop websphere. Execute command:", cmdLine, -1);

		String result = executeCmd(cmdLine);
		return execStatus(result);
	}

	public static int stopServer(String profilesHome, String serverName, String username, String password) {
		String[] cmdLine = new String[6];

		cmdLine[0] = getCmd0(profilesHome, "stopServer");
		cmdLine[1] = serverName;
		cmdLine[2] = "-username";
		cmdLine[3] = username;
		cmdLine[4] = "-password";
		cmdLine[5] = password;

		logCmdlines("Stop websphere. Execute command:", cmdLine, 5);
		String result = executeCmd(cmdLine);
		return execStatus(result);
	}

	public static int stopNDServer(String profilesHome, String serverName, String username, String password) {
		String[] cmdLine = new String[6];

		cmdLine[0] = getCmd0(profilesHome, "stopManager");
		cmdLine[1] = serverName;
		cmdLine[2] = "-username";
		cmdLine[3] = username;
		cmdLine[4] = "-password";
		cmdLine[5] = password;

		logCmdlines("Stop websphere. Execute command:", cmdLine, 5);
		String result = executeCmd(cmdLine);
		return execStatus(result);
	}

	public static int execBat(String profilesHome, String batName, String[] params) {
		String[] cmdLine = new String[params.length + 1];
		cmdLine[0] = getCmd0(profilesHome, batName);

		System.arraycopy(params, 0, cmdLine, 1, params.length);
		logCmdlines("Execute command:", cmdLine, -1);
		String result = executeCmd(cmdLine);
		if (StringUtils.contains(result, "CWPKI0308I")) {
			return EXEC_SUCC;
		}
		return EXEC_FAIL;
	}

	public static int execScript(String profilesHome, String username, String password, String scriptFilePath) {
		String[] cmdLine = new String[9];

		cmdLine[0] = getCmd0(profilesHome, "wsadmin");
		cmdLine[1] = "-user";
		cmdLine[2] = username;
		cmdLine[3] = "-password";
		cmdLine[4] = password;
		cmdLine[5] = "-lang";
		cmdLine[6] = "jacl";
		cmdLine[7] = "-f";
		cmdLine[8] = scriptFilePath;

		logCmdlines("Execute websphere jacl script. Execute command:", cmdLine, 4);
		String result = executeCmd(cmdLine);
		if (StringUtils.contains(result, "@!Script Executed Succeed!@")) {
			return EXEC_SUCC;
		}
		return EXEC_FAIL;
	}

	public static int execScript(String profilesHome, String username, String password, String scriptFilePath, String[] jaclParameters) {
		int len = 7;
		if (jaclParameters != null) {
			len = 7 + jaclParameters.length;
		}
		String[] cmdLine = new String[len];

		cmdLine[0] = getCmd0(profilesHome, "wsadmin");
		cmdLine[1] = "-user";
		cmdLine[2] = username;
		cmdLine[3] = "-password";
		cmdLine[4] = password;
		cmdLine[5] = "-f";
		cmdLine[6] = scriptFilePath;

		if (jaclParameters != null) {
			for (int i = 0; i < jaclParameters.length; i++) {
				cmdLine[(7 + i)] = jaclParameters[i];
			}
		}

		logCmdlines("Execute websphere jacl script. Execute command:", cmdLine, 4);
		String result = executeCmd(cmdLine);

		if (StringUtils.contains(result, "@!Script Executed Succeed!@")) {
			return EXEC_SUCC;
		}
		return EXEC_FAIL;
	}

	public static boolean isStarted(String profilesHome, String servername, String username, String password) {
		String[] cmdLine = new String[6];
		cmdLine[0] = getCmd0(profilesHome, "serverStatus");
		cmdLine[1] = servername;
		cmdLine[2] = "-username";
		cmdLine[3] = username;
		cmdLine[4] = "-password";
		cmdLine[5] = password;

		logCmdlines("Query websphere status. Execute command:", cmdLine, 5);

		String result = executeCmd(cmdLine);

		int status = execStatus(result);
		if (status == STATUS_STARTED) {
			return true;
		}
		return false;
	}

	private static int execStatus(String execRestult) {
		int status = 100;
		if ((execRestult.indexOf(Result_START_SUCC) >= 0) || (execRestult.indexOf(Result_STOP_SUCC) >= 0))
			status = EXEC_SUCC;
		else if ((execRestult.indexOf(Result_START_FAIL) >= 0) || (execRestult.indexOf(Result_STOP_FAIL) >= 0))
			status = EXEC_FAIL;
		else if (execRestult.indexOf(Result_START_WARN) >= 0)
			status = EXEC_FAIL;
		else if (execRestult.indexOf(Result_SERVER_STARTED) >= 0)
			status = STATUS_STARTED;
		else if (execRestult.indexOf(Result_SERVER_STOPED) >= 0)
			status = STATUS_STOPED;
		else {
			status = EXEC_WARN;
		}
		return status;
	}

	private static String getCmd0(String profilesHome, String cmdname) {
		String cmd0 = "";
		if (OS.isMicroWindows()) {
			cmd0 = "\"" + profilesHome + File.separator + "bin" + File.separator + cmdname + ".bat\"";
		} else
			cmd0 = profilesHome + File.separator + "bin" + File.separator + cmdname + ".sh";

		return cmd0;
	}

	private static String logCmdlines(String preStr, String[] strs, int ignoreIndex) {
		StringBuilder sb = new StringBuilder();
		sb.append(preStr);
		for (int i = 0; i < strs.length; i++) {
			if (i == ignoreIndex)
				sb.append("*******").append(" ");
			else {
				sb.append(strs[i]).append(" ");
			}
		}
		return sb.toString();
	}

	private static String executeCmd(String[] cmdLine) {
		String result = null;
		try {
			Process proc = Runtime.getRuntime().exec(cmdLine);
			result = ProcessReader.readAll(proc, 20000L);
			proc.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			result = e.getCause().getMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
			result = e.getCause().getMessage();
		} catch (Throwable e) {
			e.printStackTrace();
			result = e.getCause().getMessage();
		}
		System.out.println("???" + result);
		return result;
	}

	public static File getScriptDir() throws IOException {
		if (scriptDir != null) {
			return scriptDir;
		}
		File tempDir = Util.getTempDir();
		File dir = new File(tempDir, String.valueOf(System.currentTimeMillis()));
		dir.mkdirs();
		scriptDir = dir;

		String[] files = { "app_start.jacl", "app_stop.jacl", "app_uninstall.jacl", "checkSecurity.jacl", "create_queue.jacl", "config_standalone_jms.jacl", "customService.jacl",
				"delete_ds.jacl", "delete_queue.jacl", "deploy_app.jacl", "create_datasource.jacl", "create_cluster_datasource.jacl", "search_ds.jacl", "deploy_cluster_app.jacl",
				"sharedLib.jacl", "config_cluster_jms.jacl", "config_cluster_queue.jacl", "set_jvm.jacl", "set_cluster_jvm.jacl", "set_webcontainer_param.jacl" };

		for (int i = 0; i < files.length; i++) {
			String string = files[i];
			copyContent(dir, string);
		}
		return dir;
	}

	private static void copyContent(File dir, String fileName) throws FileNotFoundException, IOException {
		InputStream input = WASScriptTool.class.getResourceAsStream(fileName);
		OutputStream output = new FileOutputStream(new File(dir, fileName));
		try {
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}
}