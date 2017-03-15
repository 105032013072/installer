package com.bosssoft.platform.installer.core.action;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

import java.io.File;
import java.util.Map;

public class Zip extends AbstractAction {
	private String destFilePath = null;
	private String baseDirPath = null;
	private String includes = null;
	private String excludes = null;

	public void setDestFile(String path) {
		this.destFilePath = path;
	}

	public void setBaseDir(String path) {
		this.baseDirPath = path;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		String srcPath = ExpressionParser.parseString(this.baseDirPath);
		String destPath = ExpressionParser.parseString(this.destFilePath);

		File srcDir = new File(srcPath);
		File destFile = new File(destPath);
		try {
			FileUtils.zip(srcDir, destFile, null, FileOperationMessageListener.INSTANCE);
		} catch (OperationException e) {
			throw new InstallException("Failed to zip " + this.baseDirPath + " to " + this.destFilePath, e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}