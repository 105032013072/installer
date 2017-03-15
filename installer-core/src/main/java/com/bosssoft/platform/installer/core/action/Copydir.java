package com.bosssoft.platform.installer.core.action;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

import java.io.File;
import java.util.Map;

public class Copydir extends AbstractAction {
	private String srcDir = null;
	private String destDir = null;

	private String includes = null;
	private String excludes = null;

	protected boolean forceOverwrite = false;

	public void setSrcDir(String dir) {
		this.srcDir = dir;
	}

	public void setDestDir(String dir) {
		this.destDir = dir;
	}

	public void setIncludes(String i) {
		this.includes = i;
	}

	public void setExcludes(String e) {
		this.excludes = e;
	}

	public void setOverwrite(boolean overwrite) {
		this.forceOverwrite = overwrite;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		if (this.srcDir == null) {
			throw new InstallException("src attribute must be set!");
		}

		if (this.destDir == null) {
			throw new InstallException("dest attribute must be set!");
		}

		String srcPath = ExpressionParser.parseString(this.srcDir);
		String destPath = ExpressionParser.parseString(this.destDir);

		File src = new File(srcPath);
		File dest = new File(destPath);

		if (!src.exists()) {
			throw new InstallException("The file not be found!" + srcPath);
		}

		if (src.equals(dest))
			return;
		try {
			FileUtils.copy(src, dest, null, FileOperationMessageListener.INSTANCE);
		} catch (OperationException e) {
			throw new InstallException("Failed to copy " + this.srcDir + " to " + this.destDir, e);
		}
	}
}