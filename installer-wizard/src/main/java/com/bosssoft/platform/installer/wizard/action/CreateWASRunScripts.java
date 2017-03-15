package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.FileUtils;

public class CreateWASRunScripts implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String userInstallDir = context.getStringValue("INSTALL_DIR").replace('\\', '/');
		String asHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String serverName = context.getStringValue("AS_WAS_SERVER_NAME");
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.indexOf("window") >= 0) {
				BufferedWriter bos = new BufferedWriter(new FileWriter(userInstallDir + "/" + "startServer.cmd"));
				bos.write("\"" + asHome + "\\bin\\startServer.bat\" " + serverName);

				bos.flush();
				bos.close();

				bos = new BufferedWriter(new FileWriter(userInstallDir + "/" + "stopServer.cmd"));

				bos.write("\"" + asHome + "\\bin\\stopServer.bat\" " + serverName + " %1 %2 %3 %4");

				bos.flush();
				bos.close();
			} else {
				BufferedWriter bos = new BufferedWriter(new FileWriter(userInstallDir + "/startServer.sh"));
				bos.write("cd \"");
				bos.write(asHome);
				bos.write("/bin\"");
				bos.write("\n");

				bos.write("\"" + asHome + "/bin/startServer.sh\" " + serverName);

				bos.flush();
				bos.close();

				FileUtils.chmod(new File(userInstallDir + "/startServer.sh"), 1, "+");

				bos = new BufferedWriter(new FileWriter(userInstallDir + "/stopServer.sh"));
				bos.write("cd \"");
				bos.write(asHome);
				bos.write("/bin\"");
				bos.write("\n");

				bos.write("\"" + asHome + "/bin/stopServer.sh\" " + serverName + " $1 $2 $3 $4");

				bos.flush();
				bos.close();

				FileUtils.chmod(new File(userInstallDir + "/stopServer.sh"), 1, "+");
			}
		} catch (Exception localException) {
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}