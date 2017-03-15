package com.bosssoft.platform.installer.core.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class InstallFrame extends JFrame {
	public final Dimension DEFAULT_DIMENSION = new Dimension(640, 460);

	private Color mainBackColor = new Color(217, 240, 254);
	private JPanel centerPanel;
	private AbstractControlPanel buttonPanel;
	private JLabel line = new JLabel();

	public InstallFrame() {
		initUI();
	}

	public void initUI() {
		getContentPane().setLayout(new BorderLayout());

		this.centerPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				try {
					super.paintComponent(g);
					Graphics2D g2d = (Graphics2D) g;

					Point2D.Float p1 = new Point2D.Float(100.0F, 200.0F);
					Point2D.Float p2 = new Point2D.Float(400.0F, 200.0F);
					Rectangle2D.Float rect = new Rectangle2D.Float(getX(), getY(), getWidth(), getHeight());
					GradientPaint g2 = new GradientPaint(p1, Color.WHITE, p2, InstallFrame.this.mainBackColor, false);
					rect.setRect(getX(), getY(), getWidth(), getHeight());
					g2d.setPaint(g2);
					g2d.fill(rect);

					ImageIcon icon = new ImageIcon(InstallerFileManager.getImageDir() + "/bg_logo.gif");

					if (icon != null)
						g2d.drawImage(icon.getImage(), getWidth() - 175, getHeight() - 202, 172, 178, this);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		this.centerPanel.setLayout(null);
		this.centerPanel.setBackground(new Color(240, 245, 255));
		getContentPane().add(this.centerPanel, "Center");
		setUndecorated(false);

		setDefaultCloseOperation(0);

		this.line.setBorder(BorderFactory.createEtchedBorder());
		this.line.setText("");
		this.line.setBounds(new Rectangle(0, 394, 640, 2));

		this.centerPanel.add(this.line, null);
		String logoPath = null;
		logoPath = InstallerFileManager.getImageDir() + "/title_logo.png";

		setIconImage(new ImageIcon(logoPath).getImage());
		setResizable(false);
	}

	public void reset() {
		this.centerPanel.removeAll();
		this.centerPanel.add(this.line, null);
	}

	public void setSetupPanel(JPanel panel) {
		panel.setOpaque(false);
		panel.setBounds(new Rectangle(0, 0, 640, 392));
		this.centerPanel.add(panel, null);
	}

	public void setSetupPanel(JPanel panel, Rectangle rect) {
		panel.setBounds(rect);
		this.centerPanel.add(panel, null);
	}

	public void setControlPanel(JPanel panel) {
		this.buttonPanel = ((AbstractControlPanel) panel);
		this.buttonPanel.setBounds(new Rectangle(0, 390, 640, 66));
		this.centerPanel.add(panel, null);
	}

	public void setControlPanel(JPanel panel, Rectangle rect) {
		this.buttonPanel = ((AbstractControlPanel) panel);
		this.buttonPanel.setBounds(rect);
		this.centerPanel.add(panel, null);
	}

	public AbstractControlPanel getControlPanel() {
		return this.buttonPanel;
	}

	public void showMessage(String message, String title, int type) {
		JOptionPane.showMessageDialog(this, message, title, type);
	}

	public int showConfirmMessage(String message, String title) {
		return JOptionPane.showConfirmDialog(this, message, title, 0);
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
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
}