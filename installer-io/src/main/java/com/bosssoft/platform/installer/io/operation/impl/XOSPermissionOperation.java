package com.bosssoft.platform.installer.io.operation.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.io.util.OS;

public class XOSPermissionOperation extends AbstractPermissionOperation {
	public XOSPermissionOperation(File file, int permission, String action) {
		super(file, permission, action);
	}

	protected void doExcute() throws OperationException {
		File file = getFile();
		String userDir = System.getProperty("user.dir");
		File dir = null;
		if ((userDir != null) && (!userDir.trim().equals(""))) {
			dir = new File(userDir);
		}
		Runtime runtime = Runtime.getRuntime();
		String[] cmd = new String[3];
		cmd[0] = "/bin/sh";
		cmd[1] = "-c";
		if (file.isDirectory()) {
			cmd[2] = ("chmod -R " + getPermissionStr() + " * " + file.getAbsolutePath());
		} else
			cmd[2] = ("chmod " + getPermissionStr() + " " + file.getAbsolutePath());

		Process process = null;
		try {
			process = runtime.exec(cmd, null, dir);
			String msg = readProcess(process);
			appendMessage(msg);
		} catch (IOException e) {
			throw new OperationException(e);
		} finally {
			if (process != null)
				process.destroy();
		}
	}

	private String readProcess(Process process) throws IOException {
		BufferedInputStream is = new BufferedInputStream(process.getInputStream());
		return IOUtils.toString(is);
	}

	private String getPermissionStr() {
		String action = getAction();

		int mask = getPermission();

		StringBuilder sb = new StringBuilder();
		sb.append(action);

		if ((mask & 0x4) == 4) {
			sb.append("r");
		}

		if ((mask & 0x2) == 2) {
			sb.append("w");
		}

		if ((mask & 0x1) == 1) {
			sb.append("x");
		}

		return sb.toString();
	}

	public static boolean validateOS() {
		return OS.isFamily("unix");
	}
}