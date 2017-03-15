package com.bosssoft.platform.installer.io.operation.impl;

import com.bosssoft.platform.installer.io.listener.FileOperationListenerContainer;
import com.bosssoft.platform.installer.io.operation.IOperation;
import com.bosssoft.platform.installer.io.operation.exception.OperationCanceledException;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public abstract class AbstractOperation extends FileOperationListenerContainer implements IOperation {
	private boolean canceled = false;

	public void excute() throws OperationException {
		checkArgument();
		checkCanceled();
		doExcute();
	}

	protected abstract void doExcute() throws OperationException;

	public boolean isCanceled() {
		return this.canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	protected abstract void checkArgument();

	protected void checkCanceled() {
		if (isCanceled())
			throw new OperationCanceledException("The operation has been canceled!");
	}
}