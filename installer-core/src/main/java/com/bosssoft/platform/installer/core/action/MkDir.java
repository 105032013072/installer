package com.bosssoft.platform.installer.core.action;

import java.io.File;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.ExpressionParser;

public class MkDir extends AbstractAction {
	private static final int MKDIR_RETRY_SLEEP_MILLIS = 10;
	private String dir = null;

	public void setDir(String dir) {
		this.dir = dir;
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		if (this.dir == null) {
			throw new InstallException("dir attribute is required");
		}

		String dirPath = ExpressionParser.parseString(this.dir);

		File destDir = new File(dirPath);

		if (destDir.isFile()) {
			throw new InstallException("Unable to create directory as a file already exists with that name: " + this.dir);
		}

		if (!destDir.exists()) {
			boolean result = mkdirs(destDir);
			if (!result) {
				String msg = "Directory " + dirPath + " creation was not successful for an unknown reason";
				throw new InstallException(msg);
			}
		}
	}

	private boolean mkdirs(File f) {
		if (!f.mkdirs()) {
			try {
				Thread.sleep(10L);
				return f.mkdirs();
			} catch (InterruptedException ex) {
				return f.mkdirs();
			}
		}
		return true;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}
}