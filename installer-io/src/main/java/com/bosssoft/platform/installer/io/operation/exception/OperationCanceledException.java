package com.bosssoft.platform.installer.io.operation.exception;

public class OperationCanceledException extends RuntimeException {
	private static final long serialVersionUID = 3828230590017721829L;

	public OperationCanceledException() {
	}

	public OperationCanceledException(String message) {
		super(message);
	}

	public OperationCanceledException(Throwable cause) {
		super(cause);
	}

	public OperationCanceledException(String message, Throwable cause) {
		super(message, cause);
	}
}