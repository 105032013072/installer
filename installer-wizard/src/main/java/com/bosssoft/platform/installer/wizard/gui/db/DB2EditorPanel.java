package com.bosssoft.platform.installer.wizard.gui.db;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.JFrame;

import com.bosssoft.platform.installer.wizard.gui.AbstractDBEditorPanel;

public class DB2EditorPanel extends AbstractDBEditorPanel {
	public DB2EditorPanel() {
		this.tfdPort.setText("50000");
	}

	public String getDBUrl() {
		String strURL = "jdbc:db2://" + this.tfdIP.getText() + ":" + this.tfdPort.getText() + "/" + this.tfdSID.getText();

		return strURL;
	}

	public void initUI() {
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
		p.put("DB_TYPE", "db2");
		return p;
	}

	public String getJDBCDriverClassName() {
		return "com.ibm.db2.jcc.DB2Driver";
	}

	public boolean checkInput() {
		boolean validate = super.checkInput();

		return validate;
	}
}