package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;
import java.io.FileFilter;

public class NullFileFilter implements FileFilter {
	public boolean accept(File pathname) {
		return true;
	}
}