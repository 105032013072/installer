package com.bosssoft.platform.installer.jee.server.spi.manager.internal;

public class ServiceConfigurationError extends Error {
	private static final long serialVersionUID = -3065130771363336641L;

	public ServiceConfigurationError() {
	}

	public ServiceConfigurationError(String message) {
		super(message);
	}

	public ServiceConfigurationError(Throwable cause) {
		super(cause);
	}

	public ServiceConfigurationError(String message, Throwable cause) {
		super(message, cause);
	}
}