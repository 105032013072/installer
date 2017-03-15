package com.bosssoft.platform.installer.wizard.gui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class TextEditorComponent extends JPanel {
	private static final long serialVersionUID = -742486726768167099L;
	private JLabel label = new JLabel();
	private JTextComponent txtField = null;

	public TextEditorComponent() {
		panelInit(false);
	}

	public TextEditorComponent(boolean multi) {
		panelInit(multi);
	}

	private void panelInit(boolean multi) {
		if (multi)
			this.txtField = new JTextArea();
		else {
			this.txtField = new JTextField();
		}

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = 18;
		constraints.fill = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		add(this.label, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.anchor = 18;
		constraints.fill = 1;

		constraints.insets = new Insets(0, 5, 0, 0);
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.weightx = 1.0D;
		if (multi) {
			constraints.weighty = 1.0D;
			this.txtField.setAutoscrolls(true);
		}
		add(this.txtField, constraints);
	}

	public void setEditable(boolean editable) {
		this.txtField.setEditable(editable);
	}

	public boolean isEditable() {
		return this.txtField.isEditable();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.txtField.setEnabled(enabled);
	}

	public void setValue(String value) {
		this.txtField.setText(value);
	}

	public String getValue() {
		return this.txtField.getText();
	}

	public void setToolTip(String toolTip) {
		this.txtField.setToolTipText(toolTip);
	}

	public String getToolTip() {
		return this.txtField.getToolTipText();
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public String getLabel() {
		return this.label.getText();
	}

	public synchronized void addFocusListener(FocusListener l) {
		this.txtField.addFocusListener(l);
	}

	public synchronized void removeFocusListener(FocusListener l) {
		this.txtField.removeFocusListener(l);
	}

	public synchronized void addKeyListener(KeyListener l) {
		this.txtField.addKeyListener(l);
	}

	public synchronized void removeKeyListener(KeyListener l) {
		this.txtField.removeKeyListener(l);
	}

	public synchronized void addMouseListener(MouseListener l) {
		this.txtField.addMouseListener(l);
	}

	public synchronized void removeMouseListener(MouseListener l) {
		this.txtField.removeMouseListener(l);
	}
}