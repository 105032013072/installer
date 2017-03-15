package com.bosssoft.platform.installer.wizard.gui.db;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JFrame;

import com.bosssoft.platform.installer.wizard.gui.AbstractDBEditorPanel;

public class SqlServerEditorPanel extends AbstractDBEditorPanel {
	public static final String DB_TYPE_SQLSERVER = "sqlserver";

	public SqlServerEditorPanel() {
		this.tfdPort.setText("1433");
	}

	public String getDBUrl() {
		String strURL = "jdbc:microsoft:sqlserver://" + this.tfdIP.getText() + ":" + this.tfdPort.getText() + ";DatabaseName=" + this.tfdSID.getText() + ";SelectMethod=Cursor";

		return strURL;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(600, 400);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new SqlServerEditorPanel(), "Center");
		frame.setVisible(true);
	}

	public Properties getProperties() {
		Properties p = super.getProperties();
		p.put("DB_TYPE", DB_TYPE_SQLSERVER);
		return p;
	}

	public String getJDBCDriverClassName() {
		return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
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