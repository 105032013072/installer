package com.primeton.eos.install.util.jni;

class Win32RegKeyException extends RuntimeException {
	public Win32RegKeyException() {
	}

	public Win32RegKeyException(String why) {
		super(why);
	}
}