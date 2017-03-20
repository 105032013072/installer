package com.bosssoft.platform.installer.wizard.gui.as;

import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.AbstractASEditorPanel;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;

public class TomcatEditorDEPanel extends AbstractASEditorPanel {
	Logger logger = Logger.getLogger(getClass());

	private JLabel lblTCHome = new JLabel();
	private JTextField txtintroduction = new JTextField();

	public TomcatEditorDEPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.lblTCHome.setText(I18nUtil.getString("CHOOSEIP.PORT"));
		this.lblTCHome.setBounds(new Rectangle(0, 5, 90, 16));

		this.txtintroduction.setText("8080");
		this.txtintroduction.setBounds(new Rectangle(117, 3, 183, 21));
		add(this.lblTCHome, null);
		add(this.txtintroduction, null);
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.put("AS_TOMCAT_VERSION", getParameter());
		return p;
	}

	public boolean checkInput() {
		IContext context = getContext();
		String port = this.txtintroduction.getText().toString();
		if (this.txtintroduction.getText().trim().length() == 0) {
			showErrorMessage(I18nUtil.getString("CHOOSEIP.PORT.EMPTY"));
			return false;
		}
		if (!ValidatorHelper.isInteger(port)) {
			showErrorMessage(I18nUtil.getString("CHOOSEIP.PORT.INVALID"));
			return false;
		}

		context.setValue("APP_SERVER_PORT_DE", this.txtintroduction.getText().toString().trim());
		return true;
	}
}