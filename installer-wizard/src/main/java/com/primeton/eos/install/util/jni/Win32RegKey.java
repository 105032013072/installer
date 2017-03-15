package com.primeton.eos.install.util.jni;

import java.util.Enumeration;

public class Win32RegKey {
	public static final int HKEY_CLASSES_ROOT = -2147483648;
	public static final int HKEY_CURRENT_USER = -2147483647;
	public static final int HKEY_LOCAL_MACHINE = -2147483646;
	public static final int HKEY_USERS = -2147483645;
	public static final int HKEY_CURRENT_CONFIG = -2147483643;
	public static final int HKEY_DYN_DATA = -2147483642;
	private int root;
	private String path;

	static {
		System.loadLibrary("Win32RegKey");
	}

	public Win32RegKey(int theRoot, String thePath) {
		this.root = theRoot;
		this.path = thePath;
	}

	public Enumeration names() {
		return new Win32RegKeyNameEnumeration(this.root, this.path);
	}

	public native Object getValue(String paramString);

	public native void setValue(String paramString, Object paramObject);
}