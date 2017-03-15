package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;

public abstract class PackOperation extends AbstractFileOperation {
	public PackOperation(File src, File dest) {
		super(src, dest);
	}
}