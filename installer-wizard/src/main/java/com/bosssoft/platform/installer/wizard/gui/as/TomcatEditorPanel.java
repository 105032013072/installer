package com.bosssoft.platform.installer.wizard.gui.as;

import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.AbstractASEditorPanel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;

public class TomcatEditorPanel extends AbstractASEditorPanel {
	Logger logger = Logger.getLogger(getClass());

	private JLabel lblTCHome = new JLabel();

	protected XFileChooser tcHomeChooser = new XFileChooser();

	public TomcatEditorPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.lblTCHome.setText("Tomcat Home:");
		this.lblTCHome.setBounds(new Rectangle(0, 5, 90, 16));

		this.tcHomeChooser.setBounds(new Rectangle(117, 3, 253, 21));
		this.tcHomeChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE2"));
		add(this.lblTCHome, null);
		add(this.tcHomeChooser, null);
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.put("AS_TOMCAT_HOME", this.tcHomeChooser.getText());
		p.put("AS_TOMCAT_VERSION", getParameter());
		return p;
	}

	public boolean checkInput() {
		IContext context = getContext();

		if (this.tcHomeChooser.getText().trim().length() == 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.TC.MSG.TCHOMENULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}
		if (!isTomcatHome(this.tcHomeChooser.getText().trim())) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.TC.MSG.TCHOMEERROR"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		context.setValue("AS_TOMCAT_HOME", this.tcHomeChooser.getText().trim());

		return true;
	}

	public static boolean isTomcatHome(String home) {
		return ValidatorHelper.isContainsFileOrDir(home, new String[] { "webapps", "bin", "conf", "work" });
	}

	public String getChooserAppSerHome() {
		return this.tcHomeChooser.getText().trim();
	}
}