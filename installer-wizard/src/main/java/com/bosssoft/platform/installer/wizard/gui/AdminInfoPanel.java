package com.bosssoft.platform.installer.wizard.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;

public class AdminInfoPanel extends AbstractSetupPanel {
	private StepTitleLabel line = new StepTitleLabel();

	private JTextArea introduction = new JTextArea();
	private JLabel lblInputUserInfo = new JLabel();

	private JLabel lblUser = new JLabel();
	private JTextField tfdUser = new JTextField();
	private JLabel lblCompany = new JLabel();
	private JTextField tfdCompany = new JTextField();
	private JLabel lblAdmin = new JLabel();
	private JTextField tfdAdmin = new JTextField();
	private JLabel lblPwd1 = new JLabel();
	private JPasswordField tfdPwd1 = new JPasswordField();
	private JLabel lblPwd2 = new JLabel();
	private JPasswordField tfdPwd2 = new JPasswordField();
	private JLabel lblPwdTip = new JLabel();

	public AdminInfoPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(new GridBagLayout());

		this.line.setText(I18nUtil.getString("STEP.ADMININFO"));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = 1;
		constraints.gridx = (constraints.gridy = 0);
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(5, 26, 0, 5);
		constraints.ipady = 5;
		constraints.weightx = (constraints.weighty = 0.0D);
		add(this.line, constraints);

		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("ADMININFO.LABEL"));
		this.introduction.setLineWrap(true);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setEditable(false);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 0;
		constraints.gridheight = 3;
		constraints.insets.left = 37;
		constraints.weightx = (constraints.weighty = 1.0D);
		add(this.introduction, constraints);

		this.lblAdmin.setText(I18nUtil.getString("ADMININFO.LABEL.ADMIN"));
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.ipady = 0;
		constraints.insets.left = 37;
		constraints.weightx = (constraints.weighty = 0.0D);
		add(this.lblAdmin, constraints);

		int rightInsers = 100;
		this.tfdAdmin.setEditable(false);
		this.tfdAdmin.setText("sysadmin");
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 10;
		constraints.insets.right = rightInsers;
		add(this.tfdAdmin, constraints);
		constraints.insets.right = -1;

		this.lblPwd1.setText(I18nUtil.getString("ADMININFO.LABEL.PWD1"));
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets.left = 37;
		add(this.lblPwd1, constraints);

		this.tfdPwd1.setText("000000");
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 10;
		constraints.insets.right = rightInsers;
		add(this.tfdPwd1, constraints);
		constraints.insets.right = -1;

		this.lblPwd2.setText(I18nUtil.getString("ADMININFO.LABEL.PWD2"));
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets.left = 37;
		add(this.lblPwd2, constraints);

		this.tfdPwd2.setText("000000");
		constraints.gridx = 1;
		constraints.gridy = 7;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 10;
		constraints.insets.right = rightInsers;
		add(this.tfdPwd2, constraints);
		constraints.insets.right = -1;

		this.lblPwdTip.setText(I18nUtil.getString("ADMININFO.LABEL.PWDTIP"));
		constraints.gridx = 1;
		constraints.gridy = 8;
		constraints.gridwidth = 0;
		constraints.gridheight = 3;
		constraints.anchor = 18;
		constraints.ipady = 5;
		add(this.lblPwdTip, constraints);

		this.lblInputUserInfo.setOpaque(false);
		this.lblInputUserInfo.setText(I18nUtil.getString("ADMININFO.LABEL2"));
		constraints.gridx = 0;
		constraints.gridy = 11;
		constraints.insets.left = 45;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		add(this.lblInputUserInfo, constraints);

		this.lblUser.setText(I18nUtil.getString("ADMININFO.LABEL.USER"));
		constraints.gridx = 0;
		constraints.gridy = 12;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.ipady = 0;
		constraints.insets.left = 37;
		constraints.weightx = (constraints.weighty = 0.0D);
		add(this.lblUser, constraints);

		this.tfdUser.setText(System.getProperty("user.name"));
		constraints.gridx = 1;
		constraints.gridy = 12;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 10;
		constraints.insets.right = rightInsers;
		add(this.tfdUser, constraints);
		constraints.insets.right = -1;

		this.lblCompany.setText(I18nUtil.getString("ADMININFO.LABEL.COMPANY"));
		constraints.gridx = 0;
		constraints.gridy = 13;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets.left = 37;
		add(this.lblCompany, constraints);

		constraints.gridx = 1;
		constraints.gridy = 13;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 10;
		constraints.insets.right = rightInsers;
		add(this.tfdCompany, constraints);

		JLabel label = new JLabel();
		constraints.gridx = 1;
		constraints.gridy = 14;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = (constraints.weighty = 1.0D);
		add(label, constraints);
		label.setOpaque(false);
		setOpaque(false);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		getContext().setValue("USER_NAME", this.tfdUser.getText());
		getContext().setValue("USER_COMPANY", this.tfdCompany.getText());

		getContext().setValue("SYSADMIN_PWD", new String(this.tfdPwd1.getPassword()));
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		if (getContext().getStringValue("EDITION").equalsIgnoreCase("PE")) {
			this.lblInputUserInfo.setVisible(false);
			this.lblUser.setVisible(false);
			this.tfdUser.setVisible(false);
			this.lblCompany.setVisible(false);
			this.tfdCompany.setVisible(false);
		}
	}

	public boolean checkInput() {
		String pwd1 = new String(this.tfdPwd1.getPassword());
		String pwd2 = new String(this.tfdPwd2.getPassword());

		if (!pwd1.equals(pwd2)) {
			showError(I18nUtil.getString("ADMININFO.MSG.PWDDIFFER"));
			return false;
		}

		this.context.setValue("SYSADMIN_PWD", pwd1);
		getContext().setValue("USER_NAME", this.tfdUser.getText());
		getContext().setValue("USER_COMPANY", this.tfdCompany.getText());

		if (ValidatorHelper.isBlankOrNull(pwd1)) {
			showError(I18nUtil.getString("ADMININFO.MSG.PWDLENGTH"));
			return false;
		}

		if (!ValidatorHelper.isBetween(pwd1, 6, 20)) {
			showError(I18nUtil.getString("ADMININFO.MSG.PWDLENGTH"));
			return false;
		}

		String reg = "[a-zA-Z0-9_\\-\\.一-鿿()（）]*";
		if ((!ValidatorHelper.isPatternValid(this.tfdUser.getText(), reg)) || (!ValidatorHelper.isPatternValid(this.tfdCompany.getText(), reg))) {
			showError(I18nUtil.getString("ADMININFO.MSG.USERORG.ILLEGAL"));
			return false;
		}

		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void afterActions() {
	}
}