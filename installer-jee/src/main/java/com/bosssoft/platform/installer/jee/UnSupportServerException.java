package com.bosssoft.platform.installer.jee;

public class UnSupportServerException extends RuntimeException {
	private static final long serialVersionUID = 8139664900689421604L;

	public UnSupportServerException() {
	}

	public UnSupportServerException(String message) {
		super(message);
	}

	public UnSupportServerException(Throwable cause) {
		super(cause);
	}

	public UnSupportServerException(String message, Throwable cause) {
		super(message, cause);
	}
}