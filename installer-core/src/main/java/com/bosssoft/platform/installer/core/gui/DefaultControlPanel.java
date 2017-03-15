package com.bosssoft.platform.installer.core.gui;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class DefaultControlPanel extends AbstractControlPanel implements ActionListener {
	private JButton btnPrevious = new JButton();
	private JButton btnNext = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnHelp = new JButton();
	private JButton btnFinish = new JButton();
	public static final String BUTTON_PREVIOUS = "previous";
	public static final String BUTTON_NEXT = "next";
	public static final String BUTTON_CANCEL = "cancel";
	public static final String BUTTON_HELP = "help";
	public static final String BUTTON_FINISH = "finish";

	public DefaultControlPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);

		this.btnPrevious.setText(I18nUtil.getString("BUTTON_PREVIOUS"));
		this.btnPrevious.setMnemonic('B');
		this.btnPrevious.setOpaque(false);
		this.btnNext.setText(I18nUtil.getString("BUTTON_NEXT"));
		this.btnNext.setMnemonic('N');
		this.btnNext.setOpaque(false);
		this.btnCancel.setText(I18nUtil.getString("BUTTON_CANCEL"));
		this.btnCancel.setOpaque(false);
		this.btnHelp.setText("");
		this.btnHelp.setOpaque(false);
		this.btnHelp.setVisible(false);

		this.btnFinish.setText(I18nUtil.getString("BUTTON_FINISH"));
		this.btnFinish.setOpaque(false);
		this.btnFinish.setVisible(false);
		this.btnPrevious.addActionListener(this);
		this.btnNext.addActionListener(this);
		this.btnCancel.addActionListener(this);
		this.btnHelp.addActionListener(this);
		this.btnFinish.addActionListener(this);

		this.btnCancel.setBounds(new Rectangle(549, 10, 75, 21));
		this.btnNext.setBounds(new Rectangle(460, 10, 77, 21));
		this.btnNext.setMargin(new Insets(2, 2, 2, 2));
		this.btnPrevious.setBounds(new Rectangle(377, 10, 77, 21));
		this.btnPrevious.setMargin(new Insets(2, 2, 2, 2));
		this.btnFinish.setMargin(new Insets(2, 2, 2, 2));
		this.btnFinish.setBounds(new Rectangle(549, 10, 75, 21));
		setOpaque(false);
		add(this.btnHelp, null);
		add(this.btnPrevious, null);
		add(this.btnNext, null);
		add(this.btnCancel, null);
		add(this.btnFinish, null);
		MainFrameController.setDefaultButton(this.btnNext);
	}

	public void setButtonEnabled(String key, boolean enable) {
		if (key == null) {
			this.btnNext.setEnabled(enable);
			this.btnPrevious.setEnabled(enable);
			this.btnCancel.setEnabled(enable);
			this.btnHelp.setEnabled(enable);
		} else if (key.equalsIgnoreCase(BUTTON_NEXT)) {
			this.btnNext.setEnabled(enable);
		} else if (key.equalsIgnoreCase(BUTTON_PREVIOUS)) {
			this.btnPrevious.setEnabled(enable);
		} else if (key.equalsIgnoreCase(BUTTON_CANCEL)) {
			this.btnCancel.setEnabled(enable);
		} else if (key.equalsIgnoreCase(BUTTON_HELP)) {
			this.btnHelp.setEnabled(enable);
		} else if (key.equalsIgnoreCase(BUTTON_FINISH)) {
			this.btnHelp.setEnabled(enable);
		}
	}

	public void setButtonVisible(String key, boolean visible) {
		if (key == null) {
			this.btnNext.setVisible(visible);
			this.btnPrevious.setVisible(visible);
			this.btnCancel.setVisible(visible);
			this.btnHelp.setVisible(visible);
		} else if (key.equalsIgnoreCase(BUTTON_NEXT)) {
			this.btnNext.setVisible(visible);
		} else if (key.equalsIgnoreCase(BUTTON_PREVIOUS)) {
			this.btnPrevious.setVisible(visible);
		} else if (key.equalsIgnoreCase(BUTTON_CANCEL)) {
			this.btnCancel.setVisible(visible);
		} else if (key.equalsIgnoreCase(BUTTON_HELP)) {
			this.btnHelp.setVisible(visible);
		} else if (key.equalsIgnoreCase(BUTTON_FINISH)) {
			this.btnFinish.setVisible(visible);
		}
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.btnPrevious)
			this.cmdListener.doCommand("PREVIOUS", null);
		else if (source == this.btnNext)
			this.cmdListener.doCommand("NEXT", null);
		else if (source == this.btnCancel)
			this.cmdListener.doCommand("CANCEL", null);
		else if (source == this.btnHelp)
			this.cmdListener.doCommand("HELP", null);
		else if (source == this.btnFinish)
			this.cmdListener.doCommand("FINISH", null);
	}

	public void beforeShow() {
		this.btnNext.requestFocus();
	}

	public void setDefaultButton(String key) {
		if (key.equalsIgnoreCase(BUTTON_NEXT))
			MainFrameController.setDefaultButton(this.btnNext);
		else if (key.equalsIgnoreCase(BUTTON_PREVIOUS))
			MainFrameController.setDefaultButton(this.btnPrevious);
		else if (key.equalsIgnoreCase(BUTTON_CANCEL))
			MainFrameController.setDefaultButton(this.btnCancel);
		else if (key.equalsIgnoreCase(BUTTON_HELP))
			MainFrameController.setDefaultButton(this.btnHelp);
		else if (key.equalsIgnoreCase(BUTTON_FINISH))
			MainFrameController.setDefaultButton(this.btnHelp);
	}

	public void setButtonText(String key, String text) {
		if (key == null)
			return;
		if (key.equalsIgnoreCase(BUTTON_NEXT))
			this.btnNext.setText(text);
		else if (key.equalsIgnoreCase(BUTTON_PREVIOUS))
			this.btnPrevious.setText(text);
		else if (key.equalsIgnoreCase(BUTTON_CANCEL))
			this.btnCancel.setText(text);
		else if (key.equalsIgnoreCase(BUTTON_HELP))
			this.btnHelp.setText(text);
		else if (key.equalsIgnoreCase(BUTTON_FINISH))
			this.btnFinish.setText(text);
	}
}