package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;

import com.bosssoft.platform.installer.io.listener.DefaultFileOperationEvent;
import com.bosssoft.platform.installer.io.listener.IFileOperationListener;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class MoveFileOperation extends AbstractFileOperation {
	public MoveFileOperation(File src, File dest) {
		super(src, dest);
	}

	protected void doExcute() throws OperationException {
		File src = getSrc();
		File dest = getDest();
		DefaultFileOperationEvent event = new DefaultFileOperationEvent(src.getAbsolutePath(), dest.getAbsolutePath(), "move");
		fireOperationStarted(event);
		checkCanceled();
		boolean succes = src.renameTo(dest);
		if (!succes) {
			CopyOperation copyOperation = new CopyOperation(getSrc(), getDest());
			DeleteOperation deleteOperation = new DeleteOperation(getSrc());

			IFileOperationListener[] listeners = getAllListeners();
			for (int i = 0; i < listeners.length; i++) {
				IFileOperationListener listener = listeners[i];
				copyOperation.addListeter(listener);
				deleteOperation.addListeter(listener);
			}
			copyOperation.excute();
			deleteOperation.excute();
		}
		fireOperationFinished(event);
	}
}