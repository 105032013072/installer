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

public class Delete extends AbstractAction {
	private String file = null;

	private String dir = null;

	protected Vector<Fileset> filesets = new Vector();

	public void setFile(String f) {
		this.file = f;
	}

	public void setDir(String d) {
		this.dir = d;
	}

	public boolean addFileset(Fileset fs) {
		return this.filesets.add(fs);
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		if ((this.file == null) && (this.dir == null) && (this.filesets.size() == 0)) {
			throw new InstallException("At least one of the file or dir attributes, or a nested resource collection, must be set.");
		}

		File destFile = null;
		if (this.file != null) {
			String filePath = ExpressionParser.parseString(this.file);
			destFile = new File(filePath);
			if (destFile.exists()) {
				try {
					FileUtils.delete(destFile, null, FileOperationMessageListener.INSTANCE);
				} catch (OperationException e) {
					throw new InstallException("Unable to delete " + (destFile.isDirectory() ? "directory " : "file ") + destFile, e);
				}
			}
		}

		if (this.dir != null) {
			String dirPath = ExpressionParser.parseString(this.dir);
			destFile = new File(dirPath);
			if (destFile.exists())
				try {
					FileUtils.delete(destFile, null, FileOperationMessageListener.INSTANCE);
				} catch (OperationException e) {
					throw new InstallException("Unable to delete " + (destFile.isDirectory() ? "directory " : "file ") + destFile, e);
				}
		}
	}
}