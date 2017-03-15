package com.bosssoft.platform.installer.core.gui;

import com.bosssoft.platform.installer.core.cfg.Step;

public interface IRenderer {
	public AbstractControlPanel getControlPanel(Step step);

	public AbstractSetupPanel getSetupPanel(Step step);
}