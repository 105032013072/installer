package com.bosssoft.platform.installer.io.operation.impl;

import java.io.File;

public abstract class UnpackOperation extends AbstractFileOperation {
	public UnpackOperation(File src, File dest) {
		super(src, dest);
	}
}