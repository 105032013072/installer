package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.io.util.OS;
import com.bosssoft.platform.installer.wizard.util.DiskUtil;

public class CheckDiskSpace implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String dir = null;
		long minSpace = 0L;
		String rtnVariable = null;
		dir = parameters.get("DIR").toString();

		dir = getExistDir(dir);

		if (dir == null) {
			throw new InstallException("path is error");
		}
		if (parameters.containsKey("SPACESIZE"))
			minSpace = Long.parseLong(parameters.get("SPACESIZE").toString());
		else {
			throw new IllegalArgumentException();
		}
		if (parameters.containsKey("VARIABLE_INDEX"))
			rtnVariable = parameters.get("VARIABLE_INDEX").toString();
		else {
			throw new IllegalArgumentException();
		}
		minSpace *= 1024L;
		String tempDir = getStandardPath(dir);

		long freeSpace = 0L;
		try {
			freeSpace = DiskUtil.freeSpaceKb(tempDir);
		} catch (IOException ioe) {
			this.logger.error(tempDir + "no exits!");
		}

		if (freeSpace > minSpace) {
			context.setVariableValue(rtnVariable, "true");
		} else {
			context.setVariableValue(rtnVariable, "false");
			if (!OS.isMicroWindows())
				throw new InstallException(I18nUtil.getString("DISK.SPACE.MSG"));
		}
	}

	private String getExistDir(String dir) {
		File file = new File(dir);

		while ((!file.exists()) && (file != null)) {
			file = file.getParentFile();
		}

		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public String getStandardPath(String strPath) {
		if (strPath == null) {
			return null;
		}
		return strPath.replace('\\', '/');
	}
}