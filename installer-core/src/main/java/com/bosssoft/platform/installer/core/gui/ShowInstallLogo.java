package com.bosssoft.platform.installer.core.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class ShowInstallLogo extends JWindow implements Runnable {
	private static final long serialVersionUID = 1L;

	public void run() {
		String filename = null;
		setVisible(false);

		filename = InstallerFileManager.getImageDir() + "/loading.gif";

		ImageIcon ig = new ImageIcon(filename);
		JLabel btn = new JLabel(ig);
		getContentPane().add(btn);
		centerScreen();
		setSize(ig.getIconWidth(), ig.getIconHeight());
		toFront();
		setVisible(true);
	}

	public void setNotVisible() {
		setVisible(false);
	}

	public static void main(String[] args) {
		ShowInstallLogo ss = new ShowInstallLogo();
		ss.run();
	}

	public void centerScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 4, (screenSize.height - frameSize.height) / 4);
	}
}