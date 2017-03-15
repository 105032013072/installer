package com.bosssoft.platform.installer.core.message;

import com.bosssoft.platform.installer.io.listener.IFileOperationEvent;
import com.bosssoft.platform.installer.io.listener.IFileOperationListener;

public class FileOperationMessageListener implements IFileOperationListener {
	public static final FileOperationMessageListener INSTANCE = new FileOperationMessageListener();

	public void afterOperation(IFileOperationEvent event) {
		MessageManager.syncSendMessage("");
	}

	public void beforeOperation(IFileOperationEvent event) {
		String message = event.getMessage();
		String source = event.getSource();
		String dest = event.getDest();
		String type = event.getOperationType();

		if ("chmod".equals(type)) {
			message = "Chmod:" + source;
		} else if ("copy".equals(type)) {
			message = "Copy:" + source + "  to:" + dest;
		} else if ("delete".equals(type)) {
			message = "Delete:" + source;
		} else if ("move".equals(type)) {
			message = "Move:" + source + "  to:" + dest;
		} else if ("pack".equals(type)) {
			message = "pack:" + source + "  to:" + dest;
		} else if ("unpack".equals(type)) {
			message = "unpack:" + source + "  to:" + dest;
		}
		MessageManager.syncSendMessage(message + ":" + message);
	}
}