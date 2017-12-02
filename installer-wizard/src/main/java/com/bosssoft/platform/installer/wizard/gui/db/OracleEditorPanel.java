package com.bosssoft.platform.installer.wizard.gui.db;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JFrame;

import com.bosssoft.platform.installer.wizard.gui.AbstractDBEditorPanel;

public class OracleEditorPanel extends AbstractDBEditorPanel {
	public OracleEditorPanel() {
		initUI();
	}

	public String getDBUrl() {
		String strURL = "jdbc:oracle:thin:@" + this.tfdIP.getText() + ":" + this.tfdPort.getText() + ":" + this.tfdSID.getText();

		return strURL;
	}

	public void initUI() {
		this.lblSID.setText("SID:");
		this.tfdPort.setText("1521");
		this.tfdSID.setText("bosssoftdb");
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(600, 400);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new OracleEditorPanel(), "Center");
		frame.setVisible(true);
	}

	public Properties getProperties() {
		Properties p = super.getProperties();
		p.put("DB_IS_INSTALL", false);
		return p;
	}

	public String getJDBCDriverClassName() {
		return "oracle.jdbc.driver.OracleDriver";
	}

	public boolean checkInput() {
		boolean validate = super.checkInput();
		if (!validate) {
			return false;
		}
		return true;
	}
}