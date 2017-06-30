package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.ReflectUtil;
import com.bosssoft.platform.installer.wizard.action.CheckDataSourceExistAction;
import com.bosssoft.platform.installer.wizard.cfg.ProductInstallConfigs;
import com.bosssoft.platform.installer.wizard.cfg.Server;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class ConfigDBPanel extends AbstractSetupPanel implements ActionListener {
	Logger logger = Logger.getLogger(getClass());
	private StepTitleLabel line = new StepTitleLabel();

	private JLabel lblDB = new JLabel();
	private JComboBox cbxDb = new JComboBox();

	private JLabel lblDataSourceName = new JLabel();
	private JTextField tfdDataSourceName = new JTextField();

	private AbstractDBEditorPanel dbEditorPanel = null;
	private HashMap editorPanelMap = new HashMap();

	public ConfigDBPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//加载支持的数据库列表，添加默认选中的数据库的编辑面板
	void jbInit() throws Exception {
		setOpaque(false);
		setLayout(null);
		this.line.setText(I18nUtil.getString("STEP.DBCONFIG"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.lblDataSourceName.setText(I18nUtil.getString("STEP.DATASOURCE.NAME"));
		this.lblDataSourceName.setBounds(new Rectangle(30, 38, 130, 16));
		this.tfdDataSourceName.setText("defaultDataSource");
		this.tfdDataSourceName.setBounds(new Rectangle(162, 35, 240, 20));

		this.lblDB.setText(I18nUtil.getString("DBCONFIG.LABEL.DBTYPE"));
		this.lblDB.setBounds(new Rectangle(30, 61, 130, 16));

		this.cbxDb.setBounds(new Rectangle(162, 60, 240, 20));

		loadSupportedDBSvr();
		this.cbxDb.addActionListener(this);

		add(this.line, null);

		add(this.lblDataSourceName, null);
		add(this.tfdDataSourceName, null);

		add(this.lblDB, null);
		add(this.cbxDb, null);

		refreshSubPanel();
	}

	public void afterShow() {
	}

	//将数据库配置信息保存到context中
	public void beforeNext() {
	
		if (this.dbEditorPanel != null) {
			Properties properties = this.dbEditorPanel.getProperties();
			getContext().putAll(properties);
			Server dbServer = (Server) this.cbxDb.getSelectedItem();

			getContext().setValue("DB_TYPE", dbServer.getName());
			getContext().setValue("DB_VERSION", dbServer.getVersion());

			getContext().setValue("DB_DS_JNDI_NAME", this.tfdDataSourceName.getText().trim());
		}
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		if (this.dbEditorPanel != null)
			this.dbEditorPanel.resetTabSpaceText();
	}

	public boolean checkInput() {
		this.dbEditorPanel.setContext(getContext());
		getContext().putAll(this.dbEditorPanel.getProperties());
		if (this.dbEditorPanel == null) {
			return false;
		}

		if ("PE".equalsIgnoreCase(this.context.getStringValue("EDITION"))) {
			boolean exist = CheckDataSourceExistAction.checkDataSourceExist(this.context, this.tfdDataSourceName.getText().trim());
			if (exist) {
				String msg = I18nUtil.getString("CheckDataSourceExistAction.Exist.Error");
				showError(MessageFormat.format(msg, new Object[] { this.tfdDataSourceName.getText().trim() }));
				return false;
			}

		}

		String app_server_name = this.context.getStringValue("APP_SERVER_NAME");
		if (("WebLogic".indexOf(app_server_name) != -1) || ("WebSphere6.0".indexOf(app_server_name) != -1)) {
			String result = this.dbEditorPanel.testDBConnection();
			if (result != null) {
				showError(result);
				return false;
			}
		}
		return this.dbEditorPanel.checkInput();
	}

	public void initialize(String[] parameters) {
	}

	private void loadSupportedDBSvr() {
		List list = ProductInstallConfigs.getSupportedDBSvrs();
		int i = 0;
		for (int j = list.size(); i < j; i++)
			this.cbxDb.addItem(list.get(i));
	}

	public void afterActions() {
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.cbxDb)
			refreshSubPanel();
	}

	private void refreshSubPanel() {
		Server dbServer = (Server) this.cbxDb.getSelectedItem();

		clearDBSvrPanel();
		String className = dbServer.getEditorPanel();
		if (className != null) {
			if (this.editorPanelMap.containsKey(dbServer.getName())) {
				this.dbEditorPanel = ((AbstractDBEditorPanel) this.editorPanelMap.get(dbServer.getName()));
			} else {
				this.dbEditorPanel = ((AbstractDBEditorPanel) ReflectUtil.newInstanceBy(className));
				this.editorPanelMap.put(dbServer.getName(), this.dbEditorPanel);
			}
			this.dbEditorPanel.setBounds(new Rectangle(30, 85, 390, 320));
			add(this.dbEditorPanel, null);
			this.dbEditorPanel.chkInitDB.setEnabled(true);
		}

		validate();
		repaint();
	}

	private void clearDBSvrPanel() {
		if (this.dbEditorPanel != null)
			remove(this.dbEditorPanel);
	}
}