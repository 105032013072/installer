package com.bosssoft.platform.installer.wizard.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.action.CheckAppExistAction;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.component.TextEditorComponent;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;

public class InstallApplicationPEPanel extends AbstractSetupPanel implements ActionListener {
	private static final long serialVersionUID = 4334378310592895024L;
	private StepTitleLabel line = new StepTitleLabel();

	private JTextArea txtintroduction = new JTextArea();

	private JRadioButton rbtnNODefaultApp = new JRadioButton();

	private JRadioButton rbtnDefaultApp = new JRadioButton();

	private TextEditorComponent txtDefaultApp = new TextEditorComponent();

	private JCheckBox cbtnPublishGovernor = new JCheckBox();
	public static final String NO_DEPLOY_APP = "no";
	public static final String DEFAULT_APP = "default";
	public static final String USER_APP = "userapp";

	public InstallApplicationPEPanel() {
		panelInit();
	}

	private void panelInit() {
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 0;
		constraints.insets = new Insets(0, 26, 0, 5);
		constraints.gridheight = 1;
		constraints.weightx = 1.0D;
		constraints.fill = 1;
		constraints.anchor = 18;
		this.line.setText(I18nUtil.getString("STEP.APPLICATION.CREATE"));
		this.line.setOpaque(false);
		add(this.line, constraints);

		this.txtintroduction.setOpaque(false);
		this.txtintroduction.setText(I18nUtil.getString("STEP.APPLICATION.INTRODUCE"));
		this.txtintroduction.setLineWrap(true);
		this.txtintroduction.setWrapStyleWord(true);
		this.txtintroduction.setEditable(false);
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 0;
		constraints.insets = new Insets(0, 37, 0, 0);
		constraints.gridheight = 11;
		constraints.weightx = 1.0D;
		constraints.weighty = 0.5D;
		constraints.anchor = 18;
		constraints.fill = 1;
		add(this.txtintroduction, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 12;
		constraints.gridwidth = 0;
		constraints.insets = new Insets(5, 37, 0, 0);
		constraints.gridheight = 1;
		constraints.fill = 1;
		constraints.anchor = 18;
		this.rbtnNODefaultApp.setText(I18nUtil.getString("STEP.APPLICATION.NODEFAULTAPP"));
		this.rbtnNODefaultApp.setSelected(false);
		this.rbtnNODefaultApp.addActionListener(this);
		this.rbtnNODefaultApp.setOpaque(false);
		add(this.rbtnNODefaultApp, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 13;
		constraints.gridwidth = 0;
		constraints.fill = 1;
		constraints.anchor = 18;
		constraints.insets = new Insets(5, 37, 0, 15);
		constraints.gridheight = 1;
		this.rbtnDefaultApp.setText(I18nUtil.getString("STEP.APPLICATION.CREATEDEFAULTAPP"));
		this.rbtnDefaultApp.setSelected(true);
		this.rbtnDefaultApp.addActionListener(this);
		this.rbtnDefaultApp.setOpaque(false);
		add(this.rbtnDefaultApp, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 14;
		constraints.gridwidth = 0;
		constraints.fill = 1;
		constraints.anchor = 18;
		constraints.insets = new Insets(5, 47, 0, 15);
		constraints.gridheight = 1;
		this.txtDefaultApp.setLabel(I18nUtil.getString("STEP.APPLICATION.DEFAULT"));
		this.txtDefaultApp.setOpaque(false);
		add(this.txtDefaultApp, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 17;
		constraints.gridwidth = 0;
		constraints.fill = 1;
		constraints.anchor = 18;
		constraints.insets = new Insets(10, 37, 0, 15);
		constraints.gridheight = 1;
		constraints.weightx = (constraints.weighty = 1.0D);
		this.cbtnPublishGovernor.setSelected(true);
		this.cbtnPublishGovernor.setText(I18nUtil.getString("STEP.APPLICATION.DEPLOYGOVENOR"));
		add(this.cbtnPublishGovernor, constraints);
		this.cbtnPublishGovernor.setOpaque(false);
	}

	public void afterActions() {
	}

	public void beforeNext() {
		if (this.rbtnNODefaultApp.isSelected()) {
			getContext().setValue("IS_DEPLOY_APP", "false");
		} else if (this.rbtnDefaultApp.isSelected()) {
			getContext().setValue("IS_DEPLOY_APP", "true");
			getContext().setValue("IS_DEPLOY_DEFAULTAPP", "true");
			getContext().setValue("DEFAULT_APP_NAME", this.txtDefaultApp.getValue());
		}

		if (this.cbtnPublishGovernor.isSelected())
			getContext().setValue("IS_DEPLOY_GOVERNOR", "true");
		else
			getContext().setValue("IS_DEPLOY_GOVERNOR", "false");
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		this.txtDefaultApp.setValue(getContext().getStringValue("default.app.name"));
		if ("true".equalsIgnoreCase(getContext().getStringValue("IS_CLUSTER"))) {
			this.cbtnPublishGovernor.setSelected(false);
			this.cbtnPublishGovernor.setEnabled(false);
			this.rbtnDefaultApp.setSelected(true);
			this.rbtnNODefaultApp.setEnabled(false);
		} else {
			this.cbtnPublishGovernor.setEnabled(true);
			this.rbtnNODefaultApp.setEnabled(true);
		}
	}

	public boolean checkInput() {
		String appsvrType = this.context.getStringValue("APP_SERVER_TYPE");

		if ((getContext().getStringValue("IS_REPEAT_INSTALLED").equalsIgnoreCase("true")) && (!this.cbtnPublishGovernor.isSelected()) && (!this.rbtnDefaultApp.isSelected())) {
			String msg = I18nUtil.getString("INSTALL.APPLICATION_DO_NOTHING");
			showError(msg);
			return false;
		}

		if (this.cbtnPublishGovernor.isSelected()) {
			String appname_governor = "governor";
			if (CheckAppExistAction.checkAppExist(this.context, appsvrType, appname_governor)) {
				String msg = I18nUtil.getString("CheckAppExistAction.Exist.Error");
				msg = MessageFormat.format(msg, new Object[] { appname_governor });
				showError(msg);
				return false;
			}
		}

		if (!this.rbtnDefaultApp.isSelected()) {
			return true;
		}
		String appName = this.txtDefaultApp.getValue();
		this.context.setVariableValue("DEFAULT_APP_NAME", appName);

		if (ValidatorHelper.isBlankOrNull(appName)) {
			showError(I18nUtil.getString("STEP.APPLICATION.NAMEINVALID"));
			return false;
		}

		if ((!ValidatorHelper.isPatternValid(appName, "^[a-zA-Z][-_.\\w]*[\\w]*$")) || ((this.cbtnPublishGovernor.isSelected()) && ("governor".equalsIgnoreCase(appName)))) {
			showError(I18nUtil.getString("STEP.APPLICATION.NAMEINVALID"));
			return false;
		}

		if (!ValidatorHelper.isBetween(appName, 3, 64)) {
			showError(I18nUtil.getString("STEP.APPLICATION.NAMELENLIMIT"));
			return false;
		}

		String appname_default = this.txtDefaultApp.getValue();
		if (CheckAppExistAction.checkAppExist(this.context, appsvrType, appname_default)) {
			String msg = I18nUtil.getString("CheckAppExistAction.Exist.Error");
			msg = MessageFormat.format(msg, new Object[] { appname_default });
			showError(msg);
			return false;
		}
		return true;
	}

	public void initialize(String[] parameters) {
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (!(source instanceof JRadioButton)) {
			return;
		}
		if (source == this.rbtnNODefaultApp) {
			this.rbtnNODefaultApp.setSelected(true);

			this.rbtnDefaultApp.setSelected(false);
			this.txtDefaultApp.setEnabled(false);

			if ("true".equalsIgnoreCase(getContext().getStringValue("IS_CLUSTER"))) {
				this.cbtnPublishGovernor.setEnabled(false);
				this.cbtnPublishGovernor.setSelected(false);
			}

		} else if (source == this.rbtnDefaultApp) {
			this.rbtnNODefaultApp.setSelected(false);

			this.rbtnDefaultApp.setSelected(true);
			this.txtDefaultApp.setEnabled(true);

			if ("true".equalsIgnoreCase(getContext().getStringValue("IS_CLUSTER"))) {
				this.cbtnPublishGovernor.setEnabled(false);
				this.cbtnPublishGovernor.setSelected(false);
			} else {
				this.cbtnPublishGovernor.setEnabled(true);
			}
		}
	}
}