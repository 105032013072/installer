package com.bosssoft.platform.installer.wizard.gui.as;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.AbstractASEditorPanel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.util.WasHelper;

public class WASClusterEditorPanel extends AbstractASEditorPanel {
	transient Logger logger = Logger.getLogger(WASClusterEditorPanel.class);
	private JLabel lblWasNdHome = new JLabel();
	private JLabel lblCluster = new JLabel();
	private JLabel lblHost = new JLabel();

	private JLabel lblCellName = new JLabel();
	private JTextField tfdUserName = new JTextField();
	private JPasswordField tfdPassWord = new JPasswordField();

	private JLabel lbUserName = new JLabel();
	private JLabel lbPassWord = new JLabel();

	private JComboBox cmbCluster = new JComboBox();
	private JComboBox cmbCell = new JComboBox();

	protected XFileChooser wasNdHomeChooser = new XFileChooser();

	private String userName = "";
	private String passWord = "";

	private ActionListener listener = new SetupListener();

	private WasHelper wasHelper = null;
	private String wasHome = null;

	public WASClusterEditorPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.lblWasNdHome.setText("<html>Deployment Manager<br>Home:</html>");
		this.lblWasNdHome.setFont(new Font("Dialog", 1, 10));
		this.lblWasNdHome.setBounds(new Rectangle(0, 5, 116, 26));

		this.wasNdHomeChooser.setBounds(new Rectangle(117, 3, 253, 21));
		this.wasNdHomeChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE"));

		this.lblCellName.setText("Cell Name:");
		this.lblCellName.setBounds(new Rectangle(0, 30, 116, 16));
		this.cmbCell.setBounds(new Rectangle(117, 28, 183, 21));

		this.lblCluster.setText("Cluster Name:");
		this.lblCluster.setBounds(new Rectangle(0, 55, 116, 16));
		this.cmbCluster.setBounds(new Rectangle(117, 53, 183, 21));

		this.lbUserName.setText("UserName:");
		this.lbUserName.setBounds(new Rectangle(0, 80, 116, 16));
		this.tfdUserName.setBounds(new Rectangle(117, 80, 183, 21));

		this.lbPassWord.setText("PassWord:");
		this.lbPassWord.setBounds(new Rectangle(0, 105, 116, 16));
		this.tfdPassWord.setBounds(new Rectangle(117, 105, 183, 21));

		add(this.wasNdHomeChooser, null);
		add(this.lblCellName);
		add(this.cmbCell);

		add(this.lbUserName);
		add(this.lbPassWord);

		add(this.lblWasNdHome, null);
		add(this.lblCluster, null);
		add(this.cmbCluster, null);

		add(this.tfdUserName);
		add(this.tfdPassWord);

		this.wasNdHomeChooser.addActionListener(this.listener);

		this.cmbCell.addActionListener(this.listener);
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.put("AS_WAS_HOME", this.wasHome);
		p.put("AS_WAS_PROFILE_HOME", this.wasNdHomeChooser.getFilePath());
		p.put("AS_WAS_CELL_NAME", this.cmbCell.getSelectedItem().toString());
		p.put("AS_WAS_CLUSTER_NAME", this.cmbCluster.getSelectedItem().toString());

		p.put("AS_WAS_USERNAME", this.userName);
		p.put("AS_WAS_PASSWORD", this.passWord);

		return p;
	}

	public boolean checkInput() {
		if (this.wasNdHomeChooser.getFilePath().trim().length() == 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.WASNDHOMENULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}
		if ((this.wasHome = this.wasHelper.setWasHome(this.wasNdHomeChooser.getFilePath())) == null) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.WASHOMEERROR"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		if (this.cmbCell.getSelectedIndex() < 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.CELLNULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		if (this.cmbCluster.getSelectedIndex() < 0) {
			MainFrameController.showMessageDialog(I18nUtil.getString("APPSVR.WAS.MSG.CLUSTERNULL"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
			return false;
		}

		IContext context = getContext();

		this.userName = this.tfdUserName.getText();
		this.passWord = new String(this.tfdPassWord.getPassword());

		context.setValue("AS_WAS_USERNAME", this.userName);
		context.setValue("AS_WAS_PASSWORD", this.passWord);
		context.setValue("AS_WAS_PROFILE_HOME", this.wasNdHomeChooser.getFilePath());
		context.setValue("AS_WAS_CELL_NAME", this.cmbCell.getSelectedItem().toString());
		context.setValue("APP_SERVER_TYPE", "WebSphere6.1");
		context.setValue("AS_WAS_IS_CHECKSECURITY", Boolean.valueOf(false));
		context.setValue("AS_WAS_IS_ND", "true");
		context.setValue("AS_WL_IS_CLUSTER", "true");

		if ((this.userName == null) || ("".equals(this.userName)))
			this.userName = "anonymous";
		if ((this.passWord == null) || ("".equals(this.passWord)))
			this.passWord = "000000";
		context.setValue("AS_WAS_USERNAME", this.userName);
		context.setValue("AS_WAS_PASSWORD", this.passWord);

		return true;
	}

	private void initWasHelper() {
		if (this.wasHelper != null)
			return;
		String version = getParameter();

		if (version.indexOf("6.1") >= 0)
			this.wasHelper = new WasHelper(6);
		else
			throw new InstallException("Not support version as " + version);
	}

	private void homeAction() {
		initWasHelper();
		String homePath = this.wasNdHomeChooser.getText();
		int i = this.wasHelper.matches(homePath);
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
		String homePath = this.wasNdHomeChooser.getText();
		String[] cells = this.wasHelper.getCells(homePath);

		int i = 0;
		for (int j = cells.length; i < j; i++) {
			this.cmbCell.addItem(cells[i]);
		}
		this.cmbCell.repaint();
	}

	private void refreshClusters() {
		initWasHelper();
		this.cmbCluster.removeAllItems();
		String path = this.wasNdHomeChooser.getText();

		if (this.cmbCell.getSelectedIndex() < 0)
			return;
		String cell = this.cmbCell.getSelectedItem().toString();

		path = path + "/config/cells/" + cell;

		String[] nodes = this.wasHelper.getClusters(path);
		int i = 0;
		for (int j = nodes.length; i < j; i++) {
			this.cmbCluster.addItem(nodes[i]);
		}

		this.cmbCluster.repaint();
	}

	class SetupListener implements ActionListener {
		SetupListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			if (WASClusterEditorPanel.this.wasNdHomeChooser == source)
				WASClusterEditorPanel.this.homeAction();
			else if (WASClusterEditorPanel.this.cmbCell == source)
				WASClusterEditorPanel.this.refreshClusters();
		}
	}
	
}