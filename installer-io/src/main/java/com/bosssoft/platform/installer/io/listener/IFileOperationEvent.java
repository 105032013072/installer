package com.bosssoft.platform.installer.io.listener;

public interface IFileOperationEvent {
	
	public String getSource();

	public String getDest();

	public String getOperationType();

	public String getMessage();
}