package com.bosssoft.platform.installer.core;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.InstallFrame;

public class MainFrameController {
	private static InstallFrame frame;

	public static void setFrameInstance(InstallFrame f) {
		if (frame == null)
			frame = f;
	}

	public static void setTitle(String title) {
		frame.setTitle(title);
	}

	public static void repaint() {
		frame.repaint();
	}

	public static void refresh() {
		frame.validate();
		frame.repaint();
	}

	public static void setResizable(boolean resizable) {
		frame.setResizable(resizable);
	}

	public static void setSize(int width, int height) {
		frame.setSize(width, height);
		frame.centerScreen();
	}

	public static void showMessageDialog(String message, String title, int type) {
		JOptionPane.showMessageDialog(frame, message, title, type);
	}

	public static int showConfirmDialog(String message, String title, int optionType, int messageType) {
		return JOptionPane.showConfirmDialog(frame, message, title, optionType, messageType);
	}

	public static String showInputDialog(String message, String title, int messageType) {
		return JOptionPane.showInputDialog(frame, message, title, messageType);
	}

	public static AbstractControlPanel getControlPanel() {
		return frame.getControlPanel();
	}

	public static void setDefaultButton(JButton button) {
		frame.getRootPane().setDefaultButton(button);
	}
}