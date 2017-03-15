package com.bosssoft.platform.installer.wizard.gui.as;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.AbstractASEditorPanel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.util.WasHelper;

public class WASEditorPanel extends AbstractASEditorPanel {
	transient Logger logger = Logger.getLogger(WASEditorPanel.class);
	private JLabel lblProfileHome = new JLabel();
	private JLabel lblCellName = new JLabel();
	private JLabel lblNodeName = new JLabel();
	private JLabel lblServerName = new JLabel();
	private JLabel lbUserName = new JLabel();
	private JLabel lbPassWord = new JLabel();
	private JLabel lbSecurity = new JLabel();

	private XFileChooser profileHomeChooser = new XFileChooser();
	private JComboBox cmbCell = new JComboBox();
	private JComboBox cmbNode = new JComboBox();
	private JComboBox cmbServerName = new JComboBox();
	private JTextField tfdUserName = new JTextField();
	private JPasswordField tfdPassWord = new JPasswordField();

	private ActionListener listener = new SetupListener();

	private WasHelper wasHelper = new WasHelper(3);
	private String wasHome = null;
	private String userName = "";
	private String passWord = "";

	public WASEditorPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);

		this.lblProfileHome.setText("Profile Home:");
		this.lblProfileHome.setBounds(new Rectangle(0, 5, 116, 16));

		this.lblCellName.setText("Cell Name:");
		this.lblCellName.setBounds(new Rectangle(0, 30, 116, 16));

		this.lblNodeName.setText("Node Name:");
		this.lblNodeName.setBounds(new Rectangle(0, 55, 116, 16));

		this.lblServerName.setText("Server Name:");
		this.lblServerName.setBounds(new Rectangle(0, 80, 116, 16));

		this.lbUserName.setText("UserName:");
		this.lbUserName.setBounds(new Rectangle(0, 105, 116, 16));

		this.lbPassWord.setText("PassWord:");
		this.lbPassWord.setBounds(new Rectangle(0, 130, 116, 16));

		this.profileHomeChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE"));
		this.profileHomeChooser.setBounds(new Rectangle(117, 3, 253, 21));
		this.profileHomeChooser.setEditable(false);

		this.cmbCell.setBounds(new Rectangle(117, 28, 183, 21));
		this.cmbNode.setBounds(new Rectangle(117, 53, 183, 21));
		this.cmbServerName.setBounds(new Rectangle(117, 78, 183, 21));

		this.tfdUserName.setBounds(new Rectangle(117, 105, 183, 21));
		this.tfdPassWord.setBounds(new Rectangle(117, 130, 183, 21));
		add(this.lblProfileHome);
		add(this.lblCellName);
		add(this.lblNodeName);
		add(this.lblServerName);

		add(this.lbUserName);
		add(this.lbPassWord);

		add(this.profileHomeChooser, null);
		add(this.cmbCell);
		add(this.cmbNode, null);
		add(this.cmbServerName, null);
		add(this.tfdUserName);
		add(this.tfdPassWord);

		this.profileHomeChooser.addActionListener(this.listener);
		this.cmbCell.addActionListener(this.listener);
		this.cmbNode.addActionListener(this.listener);
	}

	public Properties getProperties() {
		Properties p = new Properties();

		p.put("AS_WAS_PROFILE_HOME", this.profileHomeChooser.getFilePath());
		File profile = new File(this.profileHomeChooser.getFilePath());
		p.put("AS_WAS_PROFILE", profile.getName());
		p.put("AS_WAS_CELL_NAME", this.cmbCell.getSelectedItem().toString());
		p.put("AS_WAS_NODE_NAME", this.cmbNode.getSelectedItem().toString());
		p.put("AS_WAS_SERVER_NAME", this.cmbServerName.getSelectedItem().toString());
		p.put("AS_WAS_USERNAME", this.userName);
		p.put("AS_WAS_PASSWORD", this.passWord);
		return p;
	}

	public boolean checkInput() {
		if (this.profileHomeChooser.getFilePath().trim().length() == 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.PROFILEHOMENULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		if (this.cmbCell.getSelectedIndex() < 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.CELLNULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		if (this.cmbServerName.getSelectedIndex() < 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.SERVERNULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		IContext context = getContext();

		this.userName = this.tfdUserName.getText();
		this.passWord = new String(this.tfdPassWord.getPassword());

		context.setValue("AS_WAS_USERNAME", this.userName);
		context.setValue("AS_WAS_PASSWORD", this.passWord);
		context.setValue("AS_WAS_PROFILE_HOME", this.profileHomeChooser.getFilePath());
		context.setValue("AS_WAS_CELL_NAME", this.cmbCell.getSelectedItem().toString());
		context.setValue("AS_WAS_NODE_NAME", this.cmbNode.getSelectedItem().toString());
		context.setValue("AS_WAS_SERVER_NAME", this.cmbServerName.getSelectedItem().toString());
		context.setValue("AS_WAS_IS_CHECKSECURITY", Boolean.valueOf(false));
		context.setValue("AS_WAS_IS_ND", "false");
		context.setValue("AS_WL_IS_CLUSTER", "false");
		String profileHome = context.getStringValue("AS_WAS_PROFILE_HOME");
		String nodeName = context.getStringValue("AS_WAS_NODE_NAME");
		String cellName = context.getStringValue("AS_WAS_CELL_NAME");

		if ((this.userName == null) || ("".equals(this.userName)))
			this.userName = "anonymous";
		if ((this.passWord == null) || ("".equals(this.passWord)))
			this.passWord = "000000";
		context.setValue("AS_WAS_USERNAME", this.userName);
		context.setValue("AS_WAS_PASSWORD", this.passWord);

		return true;
	}

	private void homeAction() {
		String profileHome = this.profileHomeChooser.getText();
		int i = this.wasHelper.matches(profileHome);
		if (i == 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.WASHOMEERROR"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return;
		}
		if (i == 2) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.EDITION"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return;
		}

		refreshCells();
	}

	private void refreshCells() {
		this.cmbCell.removeAllItems();
		String profileHome = this.profileHomeChooser.getText();
		String[] cells = this.wasHelper.getCells(profileHome);
		if (cells == null) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.PROFILEHOMEERROR.CELL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return;
		}
		int i = 0;
		for (int j = cells.length; i < j; i++) {
			this.cmbCell.addItem(cells[i]);
		}
		this.cmbCell.repaint();
	}

	private void refreshNodes() {
		this.cmbNode.removeAllItems();
		String path = this.profileHomeChooser.getText();

		if (this.cmbCell.getSelectedIndex() < 0)
			return;
		String cell = this.cmbCell.getSelectedItem().toString();

		path = path + "/config/cells/" + cell;

		String[] nodes = this.wasHelper.getNodes(path);
		if (nodes == null) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.PROFILEHOMEERROR.NODE"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return;
		}
		int i = 0;
		for (int j = nodes.length; i < j; i++) {
			this.cmbNode.addItem(nodes[i]);
		}

		this.cmbNode.repaint();
	}

	private void refreshServers() {
		this.cmbServerName.removeAllItems();
		String path = this.profileHomeChooser.getText();

		if ((this.cmbCell.getSelectedIndex() < 0) || (this.cmbNode.getSelectedIndex() < 0))
			return;
		String cell = this.cmbCell.getSelectedItem().toString();
		String node = this.cmbNode.getSelectedItem().toString();

		path = path + "/config/cells/" + cell + "/nodes/" + node;

		String[] servers = this.wasHelper.getServers(path);
		if (servers == null) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.PROFILEHOMEERROR.SERVER"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return;
		}
		int i = 0;
		for (int j = servers.length; i < j; i++) {
			this.cmbServerName.addItem(servers[i]);
		}

		this.cmbServerName.repaint();
	}

	public String getChooserAppSerHome() {
		return this.profileHomeChooser.getText().trim();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new WASEditorPanel());
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	class SetupListener implements ActionListener {
		SetupListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();

			if (WASEditorPanel.this.profileHomeChooser == source) {
				WASEditorPanel.this.homeAction();
			} else if (WASEditorPanel.this.cmbCell == source)
				WASEditorPanel.this.refreshNodes();
			else if (WASEditorPanel.this.cmbNode == source)
				WASEditorPanel.this.refreshServers();
		}
	}
}