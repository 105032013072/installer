package com.bosssoft.platform.installer.io.listener;

public interface IFileOperationListener {
	
	public void beforeOperation(IFileOperationEvent event);

	public void afterOperation(IFileOperationEvent event);
}