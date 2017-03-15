package com.bosssoft.platform.installer.wizard.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StepTitleLabel extends JPanel {
	private BorderLayout borderLayout = new BorderLayout();
	private JLabel label = new JLabel();
	private Line line = new Line();

	public StepTitleLabel() {
		init();
	}

	private void init() {
		setLayout(this.borderLayout);
		this.label.setText("Label");
		setOpaque(false);
		this.label.setOpaque(false);
		this.line.setOpaque(false);
		add(this.label, "West");
		add(this.line, "Center");
	}

	public void setText(String text) {
		this.label.setText(text);
		this.label.repaint();
	}

	public void setFont(Font font) {
		if(label!=null)
			this.label.setFont(font);
	}

	public void setBackGround(Color bg) {
		super.setBackground(bg);
		if (this.label != null)
			this.label.setBackground(bg);
		if (this.line != null)
			this.line.setBackground(bg);
	}

	public void setOpaque(boolean isOpaque) {
		super.setOpaque(isOpaque);
		if (this.label != null)
			this.label.setOpaque(isOpaque);
		if (this.line != null)
			this.line.setOpaque(isOpaque);
	}

	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (this.label != null)
			this.label.setForeground(fg);
		if (this.line != null)
			this.line.setForeground(fg);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		frame.getContentPane().add(new StepTitleLabel());
		frame.setVisible(true);
	}

	class Line extends JPanel {
		Line() {
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			int middle = getHeight() / 2;

			g.setColor(new Color(4, 158, 208));
			g.drawLine(0, middle - 1, getWidth(), middle - 1);
			g.setColor(Color.WHITE);
			g.drawLine(0, middle, getWidth(), middle);
		}
	}
}