package com.primeton.eos.install.util.jni;

import java.util.Enumeration;

class Win32RegKeyNameEnumeration implements Enumeration {
	private int root;
	private String path;
	private int index = -1;
	private int hkey = 0;
	private int maxsize;
	private int count;

	Win32RegKeyNameEnumeration(int theRoot, String thePath) {
		this.root = theRoot;
		this.path = thePath;
	}

	public native Object nextElement();

	public native boolean hasMoreElements();
}