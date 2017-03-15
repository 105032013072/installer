package com.bosssoft.platform.installer.wizard.gui.uninstall;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class IntroductionPanel extends AbstractSetupPanel {
	Logger logger = Logger.getLogger(getClass());

	private BorderLayout borderLayout = new BorderLayout();

	private StepTitleLabel line = new StepTitleLabel();

	private JPanel setupPane = new JPanel();

	private JTextArea introduction = new JTextArea();

	public IntroductionPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(this.borderLayout);
		setOpaque(false);
		this.setupPane.setLayout(null);
		this.setupPane.setOpaque(false);

		this.line.setBounds(new Rectangle(26, 5, 581, 27));
		this.line.setText(I18nUtil.getString("UNITL.STEP.INTRODUCTION"));
		this.introduction.setBackground(Color.white);
		this.introduction.setOpaque(false);

		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setEditable(false);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBounds(new Rectangle(37, 43, 375, 184));
		add(this.setupPane, "Center");
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.introduction, null);
	}

	private String getIntroduction() {
		String introduction = ExpressionParser.parseString(I18nUtil.getString("UNITL.INTRODUCTION.LABEL"));
		return introduction;
	}

	public void afterShow() {
	}

	public void beforeNext() {
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