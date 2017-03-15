package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class GetTempDir implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		File tempFile = getSystemTempDir();
		context.setValue("TEMP_DIR", tempFile.getAbsolutePath());
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public static File getSystemTempDir() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		File dir = new File(tmpdir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}