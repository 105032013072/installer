package com.bosssoft.platform.installer.core.action;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

import java.io.File;
import java.util.Map;

public class Unzip extends AbstractAction {
	private String src = null;
	private String dest = null;

	protected boolean forceOverwrite = false;

	public void setSrc(String path) {
		this.src = path;
	}

	public void setDest(String dir) {
		this.dest = dir;
	}

	public void setOverwrite(boolean overwrite) {
		this.forceOverwrite = overwrite;
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		String srcPath = ExpressionParser.parseString(this.src);
		String destPath = ExpressionParser.parseString(this.dest);

		File srcFile = new File(srcPath);
		File destDir = new File(destPath);
		
		try {
			if(destDir.exists()) FileUtils.delete(destDir, null, null);
			FileUtils.unzip(srcFile, destDir, null, FileOperationMessageListener.INSTANCE);
		} catch (OperationException e) {
			throw new InstallException("Failed to unzip " + srcPath + " to " + destPath, e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}