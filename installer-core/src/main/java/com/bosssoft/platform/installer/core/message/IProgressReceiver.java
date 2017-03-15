package com.bosssoft.platform.installer.core.message;

public interface IProgressReceiver {
	public void messageChanged(String message);

	public void beginWork(String message, int count);

	public void worked(String message, int count);

	public void worked(int count);
}