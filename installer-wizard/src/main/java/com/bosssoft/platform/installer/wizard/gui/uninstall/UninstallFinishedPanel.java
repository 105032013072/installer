package com.bosssoft.platform.installer.wizard.gui.uninstall;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class UninstallFinishedPanel extends AbstractSetupPanel {
	private BorderLayout borderLayout1 = new BorderLayout();
	private StepTitleLabel line = new StepTitleLabel();

	private JPanel setupPane = new JPanel();

	private JTextArea introduction = new JTextArea();

	public UninstallFinishedPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(this.borderLayout1);
		setOpaque(false);
		this.setupPane.setLayout(null);
		this.setupPane.setOpaque(false);
		this.line.setText(I18nUtil.getString("UNITL.STEP.FINISH"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));
		this.introduction.setOpaque(false);

		this.introduction.setLineWrap(true);
		this.introduction.setRows(1);
		this.introduction.setBounds(new Rectangle(37, 43, 375, 184));

		add(this.setupPane, "Center");
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.introduction, null);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", true);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("next", false);
		controlPane.setButtonVisible("cancel", false);
		controlPane.setButtonVisible("previous", false);
		controlPane.setDefaultButton("finish");
		this.introduction.setText(I18nUtil.getString(new StringBuilder("PRODUCT.").append(getContext().getStringValue("EDITION").toUpperCase()).toString()) + " "
				+ I18nUtil.getString("UNITL.FINISH.COMPLETEINFO"));
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