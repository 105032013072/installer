package com.bosssoft.platform.installer.wizard;

import com.bosssoft.platform.installer.core.launch.LaunchException;
import com.bosssoft.platform.installer.core.launch.Launcher;

public class ProductInstaller {
	public static void main(String[] args) {
		try {
			Launcher launcher = new Launcher();
			launcher.run(args);
		} catch (LaunchException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
}