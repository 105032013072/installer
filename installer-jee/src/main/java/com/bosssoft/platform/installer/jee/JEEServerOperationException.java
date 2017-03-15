package com.bosssoft.platform.installer.jee;

public class JEEServerOperationException extends Exception {
	private static final long serialVersionUID = -2976181566157286749L;

	public JEEServerOperationException() {
	}

	public JEEServerOperationException(String message) {
		super(message);
	}

	public JEEServerOperationException(Throwable cause) {
		super(cause);
	}

	public JEEServerOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}