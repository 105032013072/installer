package com.bosssoft.platform.installer.core.gui;

import com.bosssoft.platform.installer.core.cfg.Step;

public class DefaultRenderer implements IRenderer {
	public AbstractControlPanel getControlPanel(Step step) {
		String className = step.getControlPanelClassName();
		if (className != null) {
			try {
				return (AbstractControlPanel) Class.forName(className).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public AbstractSetupPanel getSetupPanel(Step step) {
		String className = step.getSetupPanelClassName();
		if (className != null) {
			try {
				return (AbstractSetupPanel) Class.forName(className).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}