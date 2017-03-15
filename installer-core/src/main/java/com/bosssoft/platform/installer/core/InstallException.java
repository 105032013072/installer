package com.bosssoft.platform.installer.core;

public class InstallException extends RuntimeException {
	public InstallException() {
	}

	public InstallException(String message) {
		super(message);
	}

	public InstallException(Throwable cause) {
		super(cause);
	}

	public InstallException(String message, Throwable cause) {
		super(message, cause);
	}
}