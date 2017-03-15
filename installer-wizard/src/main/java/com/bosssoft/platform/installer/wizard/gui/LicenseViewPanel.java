package com.bosssoft.platform.installer.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;

public class LicenseViewPanel extends AbstractSetupPanel implements ActionListener {
	private static final String LICENSE_FILE_PREFIX = "Bosssoft License_";
	private StepTitleLabel line = new StepTitleLabel();

	private BorderLayout borderLayout1 = new BorderLayout();

	private JPanel setupPane = new JPanel();

	private JTextArea licenseLabel = new JTextArea();

	private JScrollPane jScrollPane1 = new JScrollPane();

	private JEditorPane txtLicense = new JEditorPane();

	private JRadioButton rbtnAccept = new JRadioButton();

	private JRadioButton rbtnRefuse = new JRadioButton();

	private ButtonGroup buttonGroup = new ButtonGroup();

	public LicenseViewPanel() {
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
		this.line.setText(I18nUtil.getString("STEP.LICENSE"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.jScrollPane1.getViewport().add(this.txtLicense, null);
		this.licenseLabel.setOpaque(false);
		this.licenseLabel.setEditable(false);
		this.licenseLabel.setBounds(new Rectangle(37, 35, 373, 33));
		this.jScrollPane1.setBounds(new Rectangle(36, 68, 410, 260));
		this.jScrollPane1.setOpaque(false);
		this.txtLicense.setEditable(false);
		this.txtLicense.setOpaque(false);
		this.rbtnAccept.setText(I18nUtil.getString("LICENSE.ACCEPT"));
		this.rbtnAccept.setOpaque(false);
		this.rbtnAccept.setBounds(new Rectangle(37, 339, 372, 25));
		this.rbtnRefuse.setBounds(new Rectangle(37, 360, 372, 25));
		this.rbtnRefuse.setSelected(true);
		this.rbtnRefuse.setText(I18nUtil.getString("LICENSE.NOTACCEPT"));
		this.rbtnRefuse.setOpaque(false);
		this.buttonGroup.add(this.rbtnAccept);
		this.buttonGroup.add(this.rbtnRefuse);
		add(this.setupPane, "Center");
		this.setupPane.setOpaque(false);
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.licenseLabel, null);
		this.setupPane.add(this.jScrollPane1, null);
		this.setupPane.add(this.rbtnRefuse, null);
		this.setupPane.add(this.rbtnAccept, null);

		this.rbtnAccept.addActionListener(this);
		this.rbtnRefuse.addActionListener(this);
	}

	private String getLabelText() {
		String text = I18nUtil.getString("LICENSE.LABEL");
		return text;
	}

	public void afterShow() {
	}

	public void beforeNext() {
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		this.licenseLabel.setText(getLabelText());

		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		if (this.rbtnAccept.isSelected())
			controlPane.setButtonEnabled("next", true);
		else
			controlPane.setButtonEnabled("next", false);
		controlPane = null;

		String licenseFile = getLicenseFilePath();
		try {
			this.txtLicense.setPage(new File(licenseFile).toURL());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getLicenseFilePath() {
		String fileName = LICENSE_FILE_PREFIX + getContext().getStringValue("current.locale") + ".htm";

		String path = InstallerFileManager.getInstallerHome() + File.separator + fileName;
		return path;
	}

	public boolean checkInput() {
		return true;
	}

	public String getNextBranchID() {
		return "";
	}

	public void initialize(String[] parameters) {
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.rbtnAccept) {
			AbstractControlPanel controlPane = MainFrameController.getControlPanel();
			controlPane.setButtonEnabled("next", true);
		} else if (source == this.rbtnRefuse) {
			AbstractControlPanel controlPane = MainFrameController.getControlPanel();
			controlPane.setButtonEnabled("next", false);
		}
	}

	public void afterActions() {
	}
}