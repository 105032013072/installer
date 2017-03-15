package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;

import com.bosssoft.platform.installer.io.listener.DefaultFileOperationEvent;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public abstract class AbstractPermissionOperation extends AbstractOperation {
	private File file;
	private StringBuffer message = new StringBuffer();
	private int permission;
	private String action = "+";

	public AbstractPermissionOperation() {
	}

	public AbstractPermissionOperation(File file, int permission, String action) {
		this.file = file;
		this.permission = permission;
		this.action = action;
	}

	public void excute() throws OperationException {
		checkArgument();

		checkCanceled();

		String path = this.file.getAbsolutePath();
		DefaultFileOperationEvent event = new DefaultFileOperationEvent(path, path, "chmod");
		fireOperationStarted(event);

		checkCanceled();

		doExcute();

		event = new DefaultFileOperationEvent(path, path, "chmod");
		event.setMessage(getMessage());
		fireOperationFinished(event);
	}

	protected void appendMessage(String msg) {
		this.message.append(msg);
	}

	public String getMessage() {
		return this.message.toString();
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public int getPermission() {
		return this.permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	protected void checkArgument() {
		File file = getFile();

		if ((file == null) || (!file.exists())) {
			throw new IllegalArgumentException("The file must be exist!" + file.getAbsolutePath());
		}

		int permission = getPermission();

		if (((permission & 0x4) != 4) && ((permission & 0x2) != 2) && ((permission & 0x1) != 1) && ((permission & 0x8) != 8)) {
			throw new IllegalArgumentException("The permission is not valid,You can get the permission from PermissionConstant." + permission);
		}

		String action = getAction();
		if ((!"+".equals(action)) && (!"-".equals(action)) && (!"=".equals(action)))
			throw new IllegalArgumentException("The action is not valid,You can get the action from PermissionConstant." + action);
	}
}