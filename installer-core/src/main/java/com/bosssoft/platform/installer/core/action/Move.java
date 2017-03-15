package com.bosssoft.platform.installer.core.action;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

import java.io.File;
import java.util.Map;
import java.util.Vector;

public class Move extends AbstractAction {
	protected String file = null;
	protected String destFile = null;
	protected String destDir = null;

	protected boolean forceOverwrite = false;
	protected Vector filesets = new Vector();

	public String getDestDir() {
		return this.destDir;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public String getDestFile() {
		return this.destFile;
	}

	public void setDestFile(String destFile) {
		this.destFile = destFile;
	}

	public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setOverwrite(boolean overwrite) {
		this.forceOverwrite = overwrite;
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		String filePath = this.file != null ? ExpressionParser.parseString(this.file) : null;
		String destFilePath = this.destFile != null ? ExpressionParser.parseString(this.destFile) : null;
		String destDirPath = this.destDir != null ? ExpressionParser.parseString(this.destDir) : null;

		if ((filePath == null) && (this.filesets.size() == 0))
			throw new InstallException("Specify at least one source--a file or a resource collection.");
		if ((destDirPath == null) && (destFilePath == null)) {
			throw new InstallException("Only one of tofile and todir may be set.");
		}
		File src = null;
		File dest = null;
		if (filePath != null) {
			src = new File(filePath);
		}

		if (destFilePath != null)
			dest = new File(destFilePath);
		else if (destDirPath != null) {
			dest = new File(destDirPath);
		}
		try {
			FileUtils.move(src, dest, FileOperationMessageListener.INSTANCE);
		} catch (OperationException e) {
			throw new InstallException("Failed to move " + filePath + " to " + destDirPath + " due to " + e.getMessage(), e);
		}
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}