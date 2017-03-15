package com.bosssoft.platform.installer.wizard.tools;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.wizard.gui.as.TomcatEditorPanel;

public class TomcatConfigCheck extends AbstractConfigCheck {
	public boolean check(IContext context) {
		String tomcat_home = context.getStringValue("AS_TOMCAT_HOME");
		if (assertNull(new String[] { tomcat_home })) {
			nullError("AS_TOMCAT_HOME");
			return false;
		}
		if (!TomcatEditorPanel.isTomcatHome(tomcat_home)) {
			homeInvalidError("TOMCAT_HOME", tomcat_home);
			return false;
		}

		return true;
	}
}