package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.ReflectUtil;
import com.bosssoft.platform.installer.wizard.cfg.ProductInstallConfigs;
import com.bosssoft.platform.installer.wizard.cfg.Server;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class AppSvrPanel extends AbstractSetupPanel implements ActionListener {
	Logger logger = Logger.getLogger(getClass());

	private StepTitleLabel line = new StepTitleLabel();

	private JTextArea introduction = new JTextArea();

	private JComboBox cbxAS = new JComboBox();

	private JCheckBox chkCluster = new JCheckBox();

	private AbstractASEditorPanel appsvrPanel = null;

	private JLabel lblAppsvr = new JLabel();

	private HashMap editorPanelMap = new HashMap();

	public AppSvrPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);

		this.line.setText(I18nUtil.getString("STEP.APPSVR"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.introduction.setOpaque(false);
		this.introduction.setEditable(false);
		this.introduction.setLineWrap(true);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBounds(new Rectangle(37, 43, 375, 97));

		this.cbxAS.setBounds(new Rectangle(155, 143, 182, 20));
		loadSupportedAppsvr();
		this.cbxAS.addActionListener(this);

		this.lblAppsvr.setText(I18nUtil.getString("APPSVR.LABEL.AS"));
		this.lblAppsvr.setBounds(new Rectangle(37, 145, 100, 16));

		this.chkCluster.setText(I18nUtil.getString("APPSVR.ISCLUSTER"));
		this.chkCluster.setBounds(new Rectangle(350, 143, 90, 20));
		this.chkCluster.setOpaque(false);
		this.chkCluster.setEnabled(false);
		this.chkCluster.setVisible(false);

		this.chkCluster.addActionListener(this);

		setOpaque(false);
		add(this.line, null);
		add(this.introduction, null);
		add(this.cbxAS, null);
		add(this.chkCluster);
		add(this.lblAppsvr, null);
	}

	private void loadSupportedAppsvr() {
		List list = ProductInstallConfigs.getSupportedAppSvrs();
		for (int i = 0; i < list.size(); i++)
			this.cbxAS.addItem(list.get(i));
	}

	public void afterShow() {
	}

	public void beforeNext() {
		if (this.appsvrPanel != null) {
			Properties properties = this.appsvrPanel.getProperties();
			getContext().putAll(properties);
		}
		Server s = (Server) this.cbxAS.getSelectedItem();
		String asType = this.cbxAS.getSelectedItem().toString();

		getContext().setValue("APP_SERVER_TYPE", asType);
		getContext().setValue("APP_SERVER_NAME", s.getName());
		getContext().setValue("APP_SERVER_VERSION", s.getVersion());

		if ((s.supportedCluterDeploy()) && (this.chkCluster.isSelected()))
			getContext().setValue("IS_CLUSTER", "true");
		else
			getContext().setValue("IS_CLUSTER", "false");
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		refreshSubPanel();
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		String edition = getContext().getStringValue("EDITION");
		if ("DE".equalsIgnoreCase(edition)) {
			this.cbxAS.setEnabled(false);
			this.introduction.setText(I18nUtil.getString("APPSVR.LABEL_DE"));
		} else if ("PE".equalsIgnoreCase(edition)) {
			this.introduction.setText(I18nUtil.getString("APPSVR.LABEL_PE"));
		} else if ("CE".equalsIgnoreCase(edition)) {
			this.introduction.setText(I18nUtil.getString("APPSVR.LABEL_CE"));
		}
	}

	public boolean checkInput() {
		String asType = this.cbxAS.getSelectedItem().toString();
		getContext().setValue("APP_SERVER_TYPE", asType);
		if ((this.appsvrPanel != null) && (!this.appsvrPanel.checkInput())) {
			return false;
		}

		Boolean isInstalled = (Boolean) getContext().getValue("IS_REPEAT_INSTALLED");
		if ((isInstalled != null) && (isInstalled.booleanValue())) {
			String appServerHome = getContext().getStringValue("appServerHome");
			File f1 = new File(this.appsvrPanel.getChooserAppSerHome());
			File f2 = new File(appServerHome);
			if (!f1.getAbsolutePath().equals(f2.getAbsolutePath())) {
				String msg = I18nUtil.getString("APPSVR.HOME.ERROR");
				msg = MessageFormat.format(msg, new Object[] { f2.getAbsolutePath() });
				MainFrameController.showMessageDialog(msg, I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
				return false;
			}
		}

		return true;
	}

	public void initialize(String[] parameters) {
	}

	private void clearAppsvrPanel() {
		if (this.appsvrPanel != null)
			remove(this.appsvrPanel);
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();

		if (source == this.cbxAS) {
			refreshSubPanel();
		}

		if (source == this.chkCluster)
			refreshSubPanel();
	}

	public void afterActions() {
	}

	private void refreshSubPanel() {
		Server appSvr = (Server) this.cbxAS.getSelectedItem();
		if (appSvr.supportedCluterDeploy()) {
			this.chkCluster.setEnabled(true);
			this.chkCluster.setVisible(true);
		} else {
			this.chkCluster.setEnabled(false);
			this.chkCluster.setSelected(false);
			this.chkCluster.setVisible(false);
		}

		clearAppsvrPanel();
		String appSvrName = appSvr.getName();

		if (this.chkCluster.isSelected()) {
			appSvrName = appSvrName + "Cluster";
		}

		if (this.editorPanelMap.containsKey(appSvrName)) {
			this.appsvrPanel = ((AbstractASEditorPanel) this.editorPanelMap.get(appSvrName));
		} else {
			String className = null;
			if (this.chkCluster.isSelected())
				className = appSvr.getClusterEditorPanel();
			else {
				className = appSvr.getEditorPanel();
			}
			if (className != null) {
				this.appsvrPanel = ((AbstractASEditorPanel) ReflectUtil.newInstanceBy(className));
				this.appsvrPanel.setParameter(appSvr.getVersion());
				this.appsvrPanel.setContext(getContext());
				this.editorPanelMap.put(appSvrName, this.appsvrPanel);
				this.appsvrPanel.setBounds(new Rectangle(37, 166, 373, 350));
			} else {
				this.appsvrPanel = null;
			}
		}
		if (this.appsvrPanel != null) {
			add(this.appsvrPanel, null);
			validate();
			repaint();
		}
	}
}