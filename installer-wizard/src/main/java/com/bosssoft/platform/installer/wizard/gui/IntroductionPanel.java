package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Rectangle;

import javax.swing.JTextArea;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class IntroductionPanel extends AbstractSetupPanel {
	private StepTitleLabel line = new StepTitleLabel();

	private JTextArea introduction = new JTextArea();

	public IntroductionPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);

		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.line.setText(I18nUtil.getString("STEP.INTRODUCTION"));

		this.introduction.setOpaque(false);

		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setEditable(false);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBounds(new Rectangle(37, 43, 375, 184));

		add(this.line, null);
		setOpaque(false);

		add(this.introduction, null);
	}

	private String getIntroduction() {
		String introduction = ExpressionParser.parseString(I18nUtil.getString("INTRODUCTION"));
		String productName = I18nUtil.getString("PRODUCT." + getContext().getStringValue("EDITION").toUpperCase());
		productName = ExpressionParser.parseString(productName);
		MainFrameController.setTitle(productName);
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
	}

	public void afterActions() {
	}
}