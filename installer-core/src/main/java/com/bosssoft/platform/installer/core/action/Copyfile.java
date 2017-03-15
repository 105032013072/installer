package com.bosssoft.platform.installer.core.action;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

import java.io.File;
import java.util.Map;

public class Copyfile extends AbstractAction {
	private String src = null;
	private String dest = null;

	private boolean forceOverwrite = false;

	public void setSrc(String src) {
		this.src = src;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setOverwrite(boolean overwrite) {
		this.forceOverwrite = overwrite;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		if (this.src == null) {
			throw new InstallException("The src attribute must be present.");
		}
		if (this.dest == null) {
			throw new InstallException("The dest attribute must be present.");
		}
		String srcPath = ExpressionParser.parseString(this.src);
		String destPath = ExpressionParser.parseString(this.dest);

		File srcFile = new File(srcPath);
		File destFile = new File(destPath);

		if (!srcFile.exists()) {
			throw new InstallException("src " + srcPath + " does not exist.");
		}

		if (srcFile.equals(destFile)) {
			return;
		}
		if ((!destFile.exists()) || (this.forceOverwrite))
			try {
				FileUtils.copy(srcFile, destFile, null, FileOperationMessageListener.INSTANCE);
			} catch (OperationException e) {
				throw new InstallException("Failed to copy " + srcPath + " to " + destPath, e);
			}
	}
}