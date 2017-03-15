package com.bosssoft.platform.installer.io.permission;

public abstract interface PermissionConstant {
	public static final int UNKOWN = 0;
	public static final int EXECUTE = 1;
	public static final int WRITE = 2;
	public static final int READ = 4;
	public static final int DELETE = 8;
	public static final int ALL = 15;
	public static final String ADD = "+";
	public static final String REMOVE = "-";
	public static final String CHANGE = "=";
}