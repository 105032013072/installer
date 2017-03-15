package com.bosssoft.platform.installer.wizard.gui.db;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JFrame;

import com.bosssoft.platform.installer.wizard.gui.AbstractDBEditorPanel;

public class MySqlEditorPanel extends AbstractDBEditorPanel {
	public static final String DB_TYPE_MYSQL = "mysql";

	public MySqlEditorPanel() {
		this.tfdPort.setText("3306");
	}

	public String getDBUrl() {
		String strURL = "jdbc:mysql://" + this.tfdIP.getText() + ":" + this.tfdPort.getText() + "/" + this.tfdSID.getText();

		return strURL;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(600, 400);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new DB2EditorPanel(), "Center");
		frame.setVisible(true);
	}

	public Properties getProperties() {
		Properties p = super.getProperties();
		p.put("DB_TYPE", "mysql");
		return p;
	}

	public String getJDBCDriverClassName() {
		return "com.mysql.jdbc.Driver";
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