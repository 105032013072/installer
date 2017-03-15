package com.bosssoft.platform.installer.wizard.tools;

import java.io.File;

import com.bosssoft.platform.installer.core.IContext;

public class WebSphereConfigCheck extends AbstractConfigCheck {
	public boolean check(IContext context) {
		if (assertNull(new String[] { context.getStringValue("AS_WAS_PROFILE_HOME") })) {
			nullError("AS_WAS_PROFILE_HOME");
			return false;
		}
		if (assertNull(new String[] { context.getStringValue("AS_WAS_CELL_NAME") })) {
			nullError("AS_WAS_CELL_NAME");
			return false;
		}

		if (Boolean.TRUE.toString().equalsIgnoreCase(context.getStringValue("IS_CLUSTER"))) {
			if (assertNull(new String[] { context.getStringValue("CLUSTER_NAME") })) {
				nullError("CLUSTER_NAME");
				return false;
			}
		} else {
			if (assertNull(new String[] { "AS_WAS_NODE_NAME" })) {
				nullError("AS_WAS_NODE_NAME");
				return false;
			}
			if (assertNull(new String[] { "AS_WAS_SERVER_NAME" })) {
				nullError("AS_WAS_SERVER_NAME");
				return false;
			}
		}

		File profile = new File("AS_WAS_PROFILE_HOME");
		context.setValue("AS_WAS_PROFILE", profile.getName());

		return true;
	}
}