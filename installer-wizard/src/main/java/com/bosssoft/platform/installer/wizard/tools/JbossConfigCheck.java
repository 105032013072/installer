package com.bosssoft.platform.installer.wizard.tools;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.wizard.gui.as.JBossEditorPanel;

public class JbossConfigCheck extends AbstractConfigCheck {
	public boolean check(IContext context) {
		String jboss_home = context.getStringValue("AS_JBOSS_HOME");
		if (assertNull(new String[] { jboss_home })) {
			nullError("AS_JBOSS_HOME");
			return false;
		}
		if (!JBossEditorPanel.isJbossHome(jboss_home)) {
			homeInvalidError("JBOSS_HOME", jboss_home);
			return false;
		}

		return true;
	}
}