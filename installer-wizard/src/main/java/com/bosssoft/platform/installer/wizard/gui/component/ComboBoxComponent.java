package com.bosssoft.platform.installer.wizard.gui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ComboBoxComponent extends JPanel {
	private static final long serialVersionUID = 5942635185895253773L;
	private JLabel label = new JLabel();

	private JComboBox cbBox = new JComboBox();

	private boolean editable = false;

	public ComboBoxComponent() {
		uiInit();
	}

	private void uiInit() {
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
		this.cbBox.setAutoscrolls(true);
		this.cbBox.setEditable(this.editable);
		add(this.cbBox, constraints);
	}

	public void setEditable(boolean editable) {
		this.cbBox.setEditable(editable);
	}

	public boolean isEditable() {
		return this.cbBox.isEditable();
	}

	public void setItems(Object[] items) {
		for (int i = 0; i < items.length; i++) {
			Object object = items[i];
			this.cbBox.addItem(object);
		}
	}

	public void addItem(Object item) {
		this.cbBox.addItem(item);
	}

	public void removeItem(Object item) {
		this.cbBox.removeItem(item);
	}

	public void removeItemAt(int index) {
		this.cbBox.removeItemAt(index);
	}

	public void setSelectedItem(Object item) {
		this.cbBox.setSelectedItem(item);
	}

	public Object getSeletedItem() {
		return this.cbBox.getSelectedItem();
	}

	public void setMaximumRowCount(int count) {
		this.cbBox.setMaximumRowCount(count);
	}

	public int getMaximumRowCount() {
		return this.cbBox.getMaximumRowCount();
	}

	public void setSelectedIndex(int index) {
		this.cbBox.setSelectedIndex(index);
	}

	public int getSelectedIndex() {
		return this.cbBox.getSelectedIndex();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.label.setEnabled(enabled);
		this.cbBox.setEnabled(enabled);
	}

	public void setToolTip(String toolTip) {
		this.cbBox.setToolTipText(toolTip);
	}

	public String getToolTip() {
		return this.cbBox.getToolTipText();
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public String getLabel() {
		return this.label.getText();
	}

	public synchronized void addFocusListener(FocusListener l) {
		this.cbBox.addFocusListener(l);
	}

	public synchronized void removeFocusListener(FocusListener l) {
		this.cbBox.removeFocusListener(l);
	}

	public synchronized void addKeyListener(KeyListener l) {
		this.cbBox.addKeyListener(l);
	}

	public synchronized void removeKeyListener(KeyListener l) {
		this.cbBox.removeKeyListener(l);
	}

	public synchronized void addMouseListener(MouseListener l) {
		this.cbBox.addMouseListener(l);
	}

	public synchronized void removeMouseListener(MouseListener l) {
		this.cbBox.removeMouseListener(l);
	}
}