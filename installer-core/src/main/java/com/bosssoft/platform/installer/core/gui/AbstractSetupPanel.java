package com.bosssoft.platform.installer.core.gui;

import javax.swing.JPanel;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public abstract class AbstractSetupPanel extends JPanel {
	protected IContext context = null;

	public IContext getContext() {
		return this.context;
	}

	public void setContext(IContext c) {
		this.context = c;
	}

	public abstract void initialize(String[] paramArrayOfString);

	public abstract void beforeShow();

	public abstract boolean checkInput();

	public abstract void beforePrevious();

	public abstract void beforeNext();

	public abstract void afterActions();

	public void showError(String errorMessage) {
		MainFrameController.showMessageDialog(errorMessage, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
	}
}