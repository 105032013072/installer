package com.bosssoft.platform.installer.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class TempSpaceChoosePanel extends AbstractSetupPanel implements ActionListener {
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel setupPane = new JPanel();
	private JLabel lblErrorImg = new JLabel();
	private JLabel lblInfo1 = new JLabel();
	private JLabel lblInfo2 = new JLabel();
	private JLabel lblDir = new JLabel();
	private JTextField tfdDir = new JTextField();
	private JButton btnBrowse = new JButton();

	private JFileChooser dirChooser = new JFileChooser();

	public TempSpaceChoosePanel() {
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
		this.lblErrorImg.setLabelFor(this);
		this.lblErrorImg.setHorizontalAlignment(0);
		this.lblErrorImg.setIcon(new ImageIcon(getClass().getResource("error3.gif")));
		this.lblErrorImg.setBounds(new Rectangle(19, 17, 51, 48));
		this.lblInfo1.setText(I18nUtil.getString("TEMPSPACECHOOSE.DISKSPACE.NOT.ENOUGH"));
		this.lblInfo1.setBounds(new Rectangle(86, 19, 249, 16));
		this.lblInfo2.setText(I18nUtil.getString("TEMPSPACECHOOSE.UNPACK.LOCATION"));
		this.lblInfo2.setBounds(new Rectangle(86, 45, 247, 16));
		this.lblDir.setText(I18nUtil.getString("TEMPSPACECHOOSE.TEMPSPACE"));
		this.lblDir.setBounds(new Rectangle(27, 83, 53, 16));
		this.tfdDir.setBounds(new Rectangle(86, 80, 262, 22));
		this.btnBrowse.setBounds(new Rectangle(355, 80, 70, 21));
		this.btnBrowse.setMargin(new Insets(2, 2, 2, 2));
		this.btnBrowse.setText(I18nUtil.getString("BUTTON.BROWSE"));
		this.btnBrowse.setMnemonic('R');
		add(this.setupPane, "Center");
		this.setupPane.add(this.lblErrorImg, null);
		this.setupPane.add(this.lblInfo1, null);
		this.setupPane.add(this.lblInfo2, null);
		this.setupPane.add(this.tfdDir, null);
		this.setupPane.add(this.btnBrowse, null);
		this.setupPane.add(this.lblDir, null);

		this.btnBrowse.addActionListener(this);
		this.dirChooser.setFileSelectionMode(1);
	}

	public void afterShow() {
	}

	public void beforeNext() {
		String dir = this.tfdDir.getText().trim();
		getContext().setValue("TEMP_DIR", dir);
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		controlPane.setButtonVisible("previous", false);
		MainFrameController.setTitle(I18nUtil.getString("DIALOG.TITLE.INFO"));
	}

	public boolean checkInput() {
		String dir = this.tfdDir.getText().trim();
		if (dir.length() == 0) {
			return false;
		}
		File file = new File(dir);
		if (!file.exists())
			return false;
		return true;
	}

	public void initialize(String[] parameters) {
	}

	public void actionPerformed(ActionEvent ae) {
		int option = this.dirChooser.showOpenDialog(this);
		if (option == 0)
			this.tfdDir.setText(this.dirChooser.getSelectedFile().getPath());
	}

	public void afterActions() {
	}
}