package com.bosssoft.platform.installer.wizard.gui;

import java.util.Properties;

public interface IEditorPanel {
	public Properties getProperties();

	public boolean checkInput();

	public void setParameter(String value);
}