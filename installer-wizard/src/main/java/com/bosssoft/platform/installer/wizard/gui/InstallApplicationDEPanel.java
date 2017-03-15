package com.bosssoft.platform.installer.wizard.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.StringUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;

public class InstallApplicationDEPanel extends AbstractSetupPanel implements ActionListener {
	private static final long serialVersionUID = -4758824139883607860L;
	private StepTitleLabel line = new StepTitleLabel();

	private JTextArea introduction = new JTextArea();
	private JLabel lblDefaultName = new JLabel();
	private JTextField tfApplication = new JTextField();
	private JCheckBox chbDeployGovenor = new JCheckBox();

	private JLabel jLabel = new JLabel();

	public InstallApplicationDEPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setOpaque(false);
		setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 5;
		constraints.gridheight = 1;
		constraints.insets = new Insets(5, 26, 5, 5);
		constraints.fill = 2;
		this.line.setText(I18nUtil.getString("STEP.APPLICATION.CREATE"));
		add(this.line, constraints);
		this.line.setOpaque(false);

		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("STEP.APPLICATION.INTRODUCE"));
		this.introduction.setLineWrap(true);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setEditable(false);
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = 1;
		constraints.insets = new Insets(5, 37, 5, 5);
		constraints.gridwidth = 5;
		constraints.gridheight = 12;
		constraints.weightx = (constraints.weighty = 0.5D);
		add(this.introduction, constraints);

		this.lblDefaultName.setText(I18nUtil.getString("STEP.APPLICATION.DEFAULT"));
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 13;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 37, 5, 5);
		constraints.gridheight = 1;
		constraints.anchor = 18;
		add(this.lblDefaultName, constraints);

		this.tfApplication.setEditable(true);
		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 13;
		constraints.insets = new Insets(5, 10, 5, 5);
		constraints.fill = 2;
		constraints.gridwidth = 0;
		constraints.anchor = 18;
		constraints.gridheight = 1;
		add(this.tfApplication, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 14;
		constraints.fill = 2;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(5, 37, 5, 5);
		constraints.anchor = 18;

		this.chbDeployGovenor.setText(I18nUtil.getString("STEP.APPLICATION.DEPLOYGOVENOR"));

		this.chbDeployGovenor.addActionListener(this);
		this.chbDeployGovenor.setOpaque(false);
		this.chbDeployGovenor.setEnabled(false);
		this.chbDeployGovenor.setSelected(true);
		add(this.chbDeployGovenor, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 15;
		constraints.gridwidth = 5;
		constraints.gridheight = 1;
		constraints.weightx = (constraints.weighty = 0.5D);
		add(this.jLabel, constraints);
		this.jLabel.setOpaque(false);
		setOpaque(false);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		getContext().setValue("IS_DEPLOY_DEFAULTAPP", "true");

		if (this.chbDeployGovenor.isSelected())
			getContext().setValue("IS_DEPLOY_GOVERNOR", "true");
		else {
			getContext().setValue("IS_DEPLOY_GOVERNOR", "false");
		}
		String appName = this.tfApplication.getText();
		getContext().setValue("DEFAULT_APP_NAME", appName);
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		if (StringUtil.isNullOrBlank(this.tfApplication.getText()))
			this.tfApplication.setText(getContext().getStringValue("default.app.name"));
	}

	public boolean checkInput() {
		String appName = this.tfApplication.getText().trim();
		this.context.setVariableValue("DEFAULT_APP_NAME", appName);

		if (ValidatorHelper.isBlankOrNull(appName)) {
			showError(I18nUtil.getString("STEP.APPLICATION.NAMEINVALID"));
			return false;
		}

		if ((!ValidatorHelper.isPatternValid(appName, "^[a-zA-Z][-_.\\w]*[\\w]*$")) || ((this.chbDeployGovenor.isSelected()) && ("governor".equalsIgnoreCase(appName)))) {
			showError(I18nUtil.getString("STEP.APPLICATION.NAMEINVALID"));
			return false;
		}

		if (!ValidatorHelper.isBetween(appName, 3, 64)) {
			showError(I18nUtil.getString("STEP.APPLICATION.NAMELENLIMIT"));
			return false;
		}

		return true;
	}

	public void initialize(String[] parameters) {
	}

	public void actionPerformed(ActionEvent ae) {
	}

	public void afterActions() {
	}
}