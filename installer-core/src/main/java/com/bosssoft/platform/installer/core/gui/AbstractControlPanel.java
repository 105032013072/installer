package com.bosssoft.platform.installer.core.gui;

import javax.swing.JPanel;

import com.bosssoft.platform.installer.core.ICommandListener;

public abstract class AbstractControlPanel extends JPanel {
	protected ICommandListener cmdListener = null;

	public void setCommandListener(ICommandListener listener) {
		this.cmdListener = listener;
	}

	public ICommandListener getCommandListener() {
		return this.cmdListener;
	}

	public abstract void beforeShow();

	public abstract void setButtonVisible(String key, boolean visible);

	public abstract void setButtonEnabled(String key, boolean enable);

	public abstract void setButtonText(String key, String text);

	public abstract void setDefaultButton(String key);
}