package com.bosssoft.platform.installer.io.operation;

import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public interface IOperation {
	public static final String TYPE_COPY = "copy";
	public static final String TYPE_DELETE = "delete";
	public static final String TYPE_MOVE = "move";
	public static final String TYPE_PACK = "pack";
	public static final String TYPE_UNPACK = "unpack";
	public static final String TYPE_CHMOD = "chmod";

	public void excute() throws OperationException;

	public boolean isCanceled();

	public void setCanceled(boolean canceled);
}