package com.bosssoft.platform.installer.wizard.event;

import java.io.File;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.event.IStepInterceptor;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class OptionalComponentsInterceptor implements IStepInterceptor {
	public void beforeStep(IContext context) {
	}

	public boolean isIgnoreThis(IContext context) {
		return noOptions();
	}

	private static boolean noOptions() {
		String optionDirPath = InstallerFileManager.getOptionCompsDir();
		File optionDir = new File(optionDirPath);

		if ((!optionDir.exists()) || (optionDir.isFile())) {
			return true;
		}
		boolean noOption = true;
		File[] files = optionDir.listFiles();
		File optionDescFile = null;
		for (File f : files) {
			if (!f.isFile()) {
				optionDescFile = new File(f, "module_info.xml");
				if ((optionDescFile.exists()) && (optionDescFile.isFile())) {
					noOption = false;
					break;
				}
			}
		}
		return noOption;
	}
}