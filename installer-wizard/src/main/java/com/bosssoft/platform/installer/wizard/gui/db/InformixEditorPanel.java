package com.bosssoft.platform.installer.wizard.gui.db;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.AbstractDBEditorPanel;

public class InformixEditorPanel extends AbstractDBEditorPanel {
	protected JLabel lblInformixName = new JLabel();

	public InformixEditorPanel() {
		initUI2();
	}

	public String getDBUrl() {
		String strURL = "jdbc:informix-sqli://" + this.tfdIP.getText() + ":" + this.tfdPort.getText() + ":INFORMIXSERVER=" + this.tfdInformixName.getText() + ";Database="
				+ this.tfdSID.getText();

		return strURL;
	}

	void initUI2() {
		this.tfdPort.setText("1528");

		this.tfdUser.setBounds(new Rectangle(133, 118, 237, 21));
		this.tfdPassword.setBounds(new Rectangle(133, 148, 237, 21));
		this.tfdIP.setBounds(new Rectangle(133, 0, 237, 21));
		//this.fileChooser.setBounds(new Rectangle(133, 206, 237, 21));
		this.btnDBTest.setBounds(new Rectangle(250, 262, 120, 21));
		this.tfdPort.setBounds(new Rectangle(133, 29, 54, 21));
		//this.cbxDrivers.setBounds(new Rectangle(133, 235, 237, 21));
		this.lblSID.setBounds(new Rectangle(0, 60, 94, 16));
		this.lblUser.setBounds(new Rectangle(0, 119, 96, 16));
		this.lbPassword.setBounds(new Rectangle(0, 151, 97, 16));
		this.tfdSID.setBounds(new Rectangle(133, 58, 237, 21));
		this.chkInitDB.setBounds(new Rectangle(0, 262, 137, 25));
		this.lblTabSpace.setBounds(new Rectangle(0, 281, 380, 80));
		this.lblPort.setBounds(new Rectangle(0, 31, 70, 16));
		this.lblUrl.setBounds(new Rectangle(0, 179, 101, 16));
		this.lblIP.setBounds(new Rectangle(0, 2, 64, 16));
		this.tfdUrl.setBounds(new Rectangle(133, 177, 237, 21));

		this.lblInformixName.setText(I18nUtil.getString("DBCONFIG.LABEL.SERVERNAME"));
		this.lblInformixName.setBounds(new Rectangle(0, 89, 96, 16));
		this.tfdInformixName.setBounds(new Rectangle(133, 88, 237, 21));
		add(this.tfdInformixName, null);
		add(this.lblInformixName, null);

		this.tfdInformixName.getDocument().addDocumentListener(this.documentListener);

		//this.fileChooser.setFileSelectionMode(0);
		//this.fileChooser.setMultiSelectionEnabled(true);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(600, 400);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new InformixEditorPanel(), "Center");
		frame.setVisible(true);
	}

	public Properties getProperties() {
		Properties p = super.getProperties();
		p.put("DB_TYPE", "informix");
		p.put("DB_INFORMIX_SERVER", this.tfdInformixName.getText());
		return p;
	}

	public String getJDBCDriverClassName() {
		return "com.informix.jdbc.IfxDriver";
	}

	public boolean checkInput() {
		boolean validate = super.checkInput();
		if (!validate) {
			return false;
		}
		if (this.tfdInformixName.getText().trim().length() == 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("DBCONFIG.MSG.INFORMIXNAMENULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}
		return true;
	}

	public void initUI() {
	}
}