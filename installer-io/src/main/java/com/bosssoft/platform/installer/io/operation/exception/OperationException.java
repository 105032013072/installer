package com.bosssoft.platform.installer.io.operation.exception;

public class OperationException extends Exception {
	private static final long serialVersionUID = 2857899574677993281L;

	public OperationException() {
	}

	public OperationException(String message) {
		super(message);
	}

	public OperationException(Throwable cause) {
		super(cause);
	}

	public OperationException(String message, Throwable cause) {
		super(message, cause);
	}
}