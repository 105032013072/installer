package com.bosssoft.platform.installer.wizard.gui.db;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JFrame;

import com.bosssoft.platform.installer.wizard.gui.AbstractDBEditorPanel;

public class SybaseEditorPanel extends AbstractDBEditorPanel {
	public static final String DB_TYPE_SYBASE = "sybase";

	public SybaseEditorPanel() {
		this.tfdPort.setText("5000");
	}

	public String getDBUrl() {
		String strURL = "jdbc:sybase:Tds:" + this.tfdIP.getText() + ":" + this.tfdPort.getText() + "/" + this.tfdSID.getText() + "?CHARSET=utf8";

		return strURL;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(600, 400);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new SybaseEditorPanel(), "Center");
		frame.setVisible(true);
	}

	public Properties getProperties() {
		Properties p = super.getProperties();
		p.put("DB_TYPE", DB_TYPE_SYBASE);
		return p;
	}

	public String getJDBCDriverClassName() {
		return "com.sybase.jdbc2.jdbc.SybDriver";
	}

	public boolean checkInput() {
		boolean validate = super.checkInput();
		if (!validate) {
			return false;
		}
		return true;
	}

	public void initUI() {
	}
}