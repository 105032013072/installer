package com.bosssoft.platform.installer.wizard.gui;

import javax.swing.JPanel;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public abstract class AbstractASEditorPanel extends JPanel implements IEditorPanel {
	private String parameter = null;

	private IContext context = null;

	public void setParameter(String p) {
		this.parameter = p;
	}

	public String getParameter() {
		return this.parameter;
	}

	public IContext getContext() {
		return this.context;
	}

	public void setContext(IContext c) {
		this.context = c;
	}

	public void showErrorMessage(String errorMessage) {
		MainFrameController.showMessageDialog(errorMessage, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
	}

	public String getChooserAppSerHome() {
		return "";
	}
}