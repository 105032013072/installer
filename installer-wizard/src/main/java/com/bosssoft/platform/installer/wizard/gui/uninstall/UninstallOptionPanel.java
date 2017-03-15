package com.bosssoft.platform.installer.wizard.gui.uninstall;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class UninstallOptionPanel extends AbstractSetupPanel {
	Logger logger = Logger.getLogger(getClass());

	private JCheckBox chkKeepWorkspace = new JCheckBox();
	private JLabel lblKeepUserApp = new JLabel();

	private StepTitleLabel line = new StepTitleLabel();

	private JTextArea introduction = new JTextArea();

	public UninstallOptionPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);

		this.line.setBounds(new Rectangle(26, 5, 581, 27));
		this.line.setText(I18nUtil.getString("UNITL.STEP.OPTION"));
		this.introduction.setBackground(Color.white);
		this.introduction.setOpaque(false);

		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setEditable(false);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBounds(new Rectangle(37, 43, 375, 24));

		this.chkKeepWorkspace.setText(I18nUtil.getString("UNITL.INTRODUCTION.MSG.WORKSPACE"));
		this.chkKeepWorkspace.setBounds(new Rectangle(37, 73, 375, 24));
		this.chkKeepWorkspace.setOpaque(false);
		this.chkKeepWorkspace.setSelected(true);
		this.lblKeepUserApp.setText(I18nUtil.getString("UNITL.INTRODUCTION.MSG.USERAPPS"));
		this.lblKeepUserApp.setForeground(Color.RED);
		this.lblKeepUserApp.setBounds(new Rectangle(37, 103, 375, 24));
		this.lblKeepUserApp.setOpaque(false);

		add(this.line);
		add(this.introduction);
		add(this.lblKeepUserApp);
		add(this.chkKeepWorkspace);
	}

	private String getIntroduction() {
		String introduction = I18nUtil.getString("UNITL.OPTION.LABEL");
		return introduction;
	}

	public void afterShow() {
	}

	public void beforeNext() {
		if (this.chkKeepWorkspace.isSelected())
			getContext().setValue("KEEP_WORKSPACE", "true");
		else
			getContext().setValue("KEEP_WORKSPACE", "false");
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();

		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("previous", false);
		this.introduction.setText(getIntroduction());
	}

	public boolean checkInput() {
		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
		String title = I18nUtil.getString("UNITL.TITLE");
		String productName = I18nUtil.getString("PRODUCT.DE");
		productName = ExpressionParser.parseString(productName);
		title = title + " " + productName;
		MainFrameController.setTitle(title);
	}

	public void afterActions() {
	}

	private boolean existWorkspace() {
		boolean isNeed = false;
		String edition = getContext().getStringValue("EDITION").toUpperCase();
		if ("DE".equals(edition)) {
			String workspace = getContext().getStringValue("INSTALL_DIR").toUpperCase() + File.separator + "ide" + File.separator + "eclipse" + File.separator + "workspace";

			if (new File(workspace).exists()) {
				isNeed = true;
			}
		}
		return isNeed;
	}

	private boolean existUserCreatedApp() {
		String defaultAppName = this.context.getStringValue("DEFAULT_APP_NAME");
		return existUserCreatedAppName(defaultAppName);
	}

	private boolean existUserCreatedAppName(String defaultAppName) {
		return false;
	}
}