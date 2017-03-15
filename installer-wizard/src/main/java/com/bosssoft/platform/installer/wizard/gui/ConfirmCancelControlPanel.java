package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class ConfirmCancelControlPanel extends AbstractControlPanel implements ActionListener {
	private JButton btnConfirm = new JButton();
	private JButton btnCancel = new JButton();
	public static final String BUTTON_NEXT = "confirm";
	public static final String BUTTON_CANCEL = "cancel";

	public ConfirmCancelControlPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.btnConfirm.setText(I18nUtil.getString("CHOOSE_PANEL.CONFIRM"));
		this.btnConfirm.setOpaque(false);

		this.btnCancel.setText(I18nUtil.getString("CHOOSE_PANEL.CANCEL"));
		this.btnCancel.setOpaque(false);
		this.btnConfirm.addActionListener(this);
		this.btnCancel.addActionListener(this);

		this.btnCancel.setBounds(new Rectangle(355, 15, 70, 21));
		this.btnConfirm.setBounds(new Rectangle(270, 15, 70, 21));
		this.btnConfirm.setMargin(new Insets(2, 2, 2, 2));

		add(this.btnConfirm, null);
		add(this.btnCancel, null);
		MainFrameController.setDefaultButton(this.btnConfirm);
	}

	public void setButtonEnabled(String key, boolean enable) {
		if (key == null) {
			this.btnConfirm.setEnabled(enable);
			this.btnCancel.setEnabled(enable);
		} else if (key.equalsIgnoreCase("confirm")) {
			this.btnConfirm.setEnabled(enable);
		} else if (key.equalsIgnoreCase("cancel")) {
			this.btnCancel.setEnabled(enable);
		}
	}

	public void setButtonVisible(String key, boolean visible) {
		if (key == null) {
			this.btnConfirm.setVisible(visible);
			this.btnCancel.setVisible(visible);
		} else if (key.equalsIgnoreCase("next")) {
			this.btnConfirm.setVisible(visible);
		} else if (key.equalsIgnoreCase("cancel")) {
			this.btnCancel.setVisible(visible);
		}
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.btnConfirm)
			this.cmdListener.doCommand("NEXT", null);
		else if (source == this.btnCancel)
			this.cmdListener.doCommand("CANCEL", null);
		else
			throw new IllegalStateException();
	}

	public void beforeShow() {
		setBounds(0, 110, 460, 56);

		MainFrameController.setSize(460, 180);
	}

	public void setDefaultButton(String key) {
		if (key.equalsIgnoreCase("confirm"))
			MainFrameController.setDefaultButton(this.btnConfirm);
		else if (key.equalsIgnoreCase("cancel"))
			MainFrameController.setDefaultButton(this.btnCancel);
	}

	public void setButtonText(String key, String text) {
		if (key == null)
			return;
		if (key.equalsIgnoreCase("next")) {
			this.btnConfirm.setText(text);
		} else if (key.equalsIgnoreCase("cancel"))
			this.btnCancel.setText(text);
	}
}