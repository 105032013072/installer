package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class UninstallIDE implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map parameters) throws InstallException {
		String ideDir = parameters.get("IDE_DIR").toString();
		String isKeepWSStr = parameters.get("IS_KEEP_WORKSPACE").toString().toLowerCase();
		String installDir = parameters.get("INSTALL_DIR").toString();
		boolean keepWS = isKeepWSStr.equals("true");
		try {
			deleteIDE(ideDir, keepWS, installDir);
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}

	private void deleteIDE(String ideDir, boolean keepWS, String installDir) throws OperationException {
		if (!keepWS) {
			FileUtils.delete(new File(ideDir), null, null);
			return;
		}

		String srcPath = ideDir + "/eclipse/workspace";

		File workspace = new File(srcPath);
		if (workspace.exists()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			Date now = new Date();
			String newName = "workspace" + sdf.format(now);

			String targetFile = installDir + File.separator + newName;
			FileUtils.move(workspace, new File(targetFile), null);

			FileUtils.delete(new File(ideDir), null, null);
		} else {
			FileUtils.delete(new File(ideDir), null, null);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}