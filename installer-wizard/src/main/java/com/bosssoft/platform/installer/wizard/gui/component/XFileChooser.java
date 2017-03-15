package com.bosssoft.platform.installer.wizard.gui.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class XFileChooser extends JPanel {
	private JTextField tfdPath = new JTextField();
	private JButton btnBrowse = new JButton();

	private int buttonWidth = 70;
	private int buttonHeight = 21;

	private JFileChooser fileChooser = new JFileChooser();

	private List<ActionListener> listeners = new ArrayList<ActionListener>();

	public XFileChooser() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints constraints = new GridBagConstraints();

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = -1;
		constraints.fill = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1.0D;
		add(this.tfdPath, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;

		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 5, 0, 0);
		add(this.btnBrowse, constraints);

		this.btnBrowse.setMargin(new Insets(2, 2, 2, 2));
		this.btnBrowse.setText("Browse");
		this.btnBrowse.setSize(buttonWidth, buttonHeight);
		this.btnBrowse.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
		this.btnBrowse.setOpaque(false);

		this.btnBrowse.addActionListener(new ButtonActionListener());
		this.fileChooser.setFileSelectionMode(1);
		this.fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
			}
		});
		this.tfdPath.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				String path = XFileChooser.this.tfdPath.getText().trim();
				if ((path != null) && (!"".equals(path))) {
					String[] fileNames = path.split(";");
					File[] files = new File[fileNames.length];
					List filesList = new ArrayList();
					for (int i = 0; i < fileNames.length; i++) {
						File file = new File(fileNames[i]);
						if (file.exists())
							filesList.add(file);
					}
				}
			}
		});
	}

	public void setInitText(String text) {
		this.tfdPath.setText(text);
	}

	public void setButtonText(String text) {
		this.btnBrowse.setText(text);
	}

	public void setButtonmnMnemonic(int mnemonic) {
		this.btnBrowse.setMnemonic(mnemonic);
	}

	public void setButtonmnMnemonic(char mnemonic) {
		this.btnBrowse.setMnemonic(mnemonic);
	}

	public String getFilePath() {
		return this.tfdPath.getText();
	}

	public String getText() {
		return this.tfdPath.getText();
	}

	public void setText(String path) {
		this.tfdPath.setText(path);
	}

	public File[] getSelectedFiles() {
		return this.fileChooser.getSelectedFiles();
	}

	public File getSelectedFile() {
		return this.fileChooser.getSelectedFile();
	}

	public void setCurrentDirectory(String dir) {
		this.fileChooser.setCurrentDirectory(new File(dir));
	}

	public void setButtonWidth(int width) {
		this.buttonWidth = width;
	}

	public JTextField getTextField() {
		return this.tfdPath;
	}

	public JButton getButton() {
		return this.btnBrowse;
	}

	private void buttonActionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.btnBrowse) {
			String currentPath = getText();
			File dir = new File(currentPath);
			if (dir.exists()) {
				this.fileChooser.setCurrentDirectory(dir);
			}
			int option = this.fileChooser.showOpenDialog(this);
			if (option == 0) {
				String path = "";
				if (this.fileChooser.getFileSelectionMode() == 1) {
					path = this.fileChooser.getSelectedFile().getAbsolutePath();
				} else if ((this.fileChooser.getFileSelectionMode() == 0) && (!this.fileChooser.isMultiSelectionEnabled())) {
					File file = this.fileChooser.getSelectedFile();
					path = path + file.getAbsolutePath();
				} else {
					File[] file = this.fileChooser.getSelectedFiles();
					for (int i = 0; i < file.length; i++) {
						path = path + file[i].getAbsolutePath();
						if (i != file.length - 1) {
							path = path + ";";
						}
					}
				}
				this.tfdPath.setText(path);

				ae.setSource(this);
				for (int i = 0; i < this.listeners.size(); i++) {
					ActionListener listener = (ActionListener) this.listeners.get(i);
					listener.actionPerformed(ae);
				}
			}
		}
		this.tfdPath.repaint();
	}

	public void setFileSelectionMode(int mode) {
		this.fileChooser.setFileSelectionMode(mode);
	}

	public void setMultiSelectionEnabled(boolean b) {
		this.fileChooser.setMultiSelectionEnabled(b);
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileChooser.setFileFilter(fileFilter);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.tfdPath.setEnabled(enabled);
		this.btnBrowse.setEnabled(enabled);
	}

	public void setEditable(boolean b) {
		this.tfdPath.setEditable(b);
	}

	public void addActionListener(ActionListener listener) {
		this.listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		this.listeners.remove(listener);
	}

	class ButtonActionListener implements ActionListener {
		ButtonActionListener() {
		}

		public void actionPerformed(ActionEvent ae) {
			XFileChooser.this.buttonActionPerformed(ae);
		}
	}
}