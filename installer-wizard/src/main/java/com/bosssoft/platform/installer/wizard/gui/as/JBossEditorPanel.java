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

public class JBossEditorPanel extends AbstractASEditorPanel {
	Logger logger = Logger.getLogger(getClass());

	private JLabel lblJBossHome = new JLabel();

	protected XFileChooser jbossHomeChooser = new XFileChooser();

	public JBossEditorPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.lblJBossHome.setText("JBoss HOME:");
		this.lblJBossHome.setBounds(new Rectangle(0, 5, 80, 16));

		this.jbossHomeChooser.setBounds(new Rectangle(117, 3, 253, 21));

		this.jbossHomeChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE2"));

		add(this.lblJBossHome, null);

		add(this.jbossHomeChooser, null);
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.put("AS_JBOSS_HOME", this.jbossHomeChooser.getText());
		return p;
	}

	public boolean checkInput() {
		IContext context = getContext();
		if (this.jbossHomeChooser.getText().trim().length() == 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.JBOSS.MSG.JBOSSHOMENULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}
		boolean isValidHome = isJbossHome(this.jbossHomeChooser.getText().trim());
		if (!isValidHome) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.JBOSS.MSG.JBOSSHOMEERROR"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);

			return false;
		}
		return true;
	}

	public static boolean isJbossHome(String home) {
		return ValidatorHelper.isContainsFileOrDir(home, new String[] { "server", "bin", "lib", "client" });
	}

	public String getChooserAppSerHome() {
		return this.jbossHomeChooser.getText().trim();
	}

	public static void main(String[] args) {
		String tempDir = "C:\\Appserver\\jboss-4.0.5.GA\\jboss-4.0.5.GA";
	}
}