package com.bosssoft.platform.installer.wizard.gui.component;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MultiLabel extends JComponent implements SwingConstants {
	int num_lines;
	int line_height;
	int line_ascent;
	int max_width = -1;
	int text_height = -1;
	int[] line_widths;
	int btnMarginWidth = 1;

	private String[] lines = null;

	private int horizontalAlignment = 10;
	private int textAlignment = 10;
	private int verticalAlignment = 0;

	public MultiLabel(String text) {
		this(text, 10, 10, 0);
	}

	public MultiLabel(String text, int horizontalAlignment) {
		this(text, horizontalAlignment, 10, 0);
	}

	public MultiLabel(String text, int horizontalAlignment, int textAlignment) {
		this(text, horizontalAlignment, textAlignment, 0);
	}

	public MultiLabel(String str, int horizontalAlignment, int textAlignment, int verticalAlignment) {
		setForeground(UIManager.getColor("Label.foreground"));
		setBackground(UIManager.getColor("Label.background"));
		setFont(UIManager.getFont("Label.font"));

		setText(str);
		this.horizontalAlignment = horizontalAlignment;
		this.textAlignment = textAlignment;
		this.verticalAlignment = verticalAlignment;
	}

	public void setText(String text) {
		if (text == null)
			text = "";

		StringTokenizer tkn = new StringTokenizer(text, "\n");

		this.num_lines = tkn.countTokens();
		this.lines = new String[this.num_lines];
		this.line_widths = new int[this.num_lines];

		for (int i = 0; i < this.num_lines; i++) {
			this.lines[i] = tkn.nextToken();
		}
		recalculateDimension();
	}

	private void recalculateDimension() {
		FontMetrics fontmetrics = getFontMetrics(getFont());

		this.line_height = fontmetrics.getHeight();
		this.line_ascent = fontmetrics.getAscent();

		this.max_width = 0;
		for (int i = 0; i < this.num_lines; i++) {
			this.line_widths[i] = fontmetrics.stringWidth(this.lines[i]);

			this.max_width = Math.max(this.max_width, this.line_widths[i]);
		}

		this.max_width += 2 * this.btnMarginWidth;
		this.text_height = (this.num_lines * this.line_height);
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public Dimension getMaximumSize() {
		return new Dimension(32767, 32767);
	}

	public Dimension getMinimumSize() {
		if ((this.max_width == -1) || (this.text_height == -1))
			recalculateDimension();

		Insets insets = getInsets();

		return new Dimension(this.max_width + insets.left + insets.right, this.text_height + insets.top + insets.bottom);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension d = getSize();

		if ((d.width != this.max_width) || (d.height != this.text_height)) {
			recalculateDimension();
		}
		Insets insets = getInsets();

		int y = 0;

		if (this.verticalAlignment == 1) {
			y = insets.top + this.line_ascent;
		} else if (this.verticalAlignment == 0) {
			y = insets.top + this.line_ascent;

			int clientAreaHeight = d.height - insets.top - insets.bottom;
			y += (clientAreaHeight - this.text_height) / 2;
		} else if (this.verticalAlignment == 3) {
			int clientAreaBottom = d.height - insets.bottom;

			y = clientAreaBottom - this.text_height;

			y += this.line_ascent;
		}

		for (int i = 0; i < this.num_lines; i++) {
			int ha = getBidiHorizontalAlignment(this.horizontalAlignment);

			int x = 0;

			if (ha == 2) {
				ha = getBidiHorizontalAlignment(this.textAlignment);
				if (ha == 2)
					x = insets.left;
				else if (ha == 4)
					x = this.max_width - this.line_widths[i] + insets.left;
				else if (ha == 0)
					x = insets.left + (this.max_width - this.line_widths[i]) / 2;
			} else if (ha == 4) {
				ha = getBidiHorizontalAlignment(this.textAlignment);
				if (ha == 2)
					x = d.width - this.max_width - insets.right;
				else if (ha == 4)
					x = d.width - this.line_widths[i] - insets.right;
				else if (ha == 0)
					x = d.width - this.max_width - insets.right + (this.max_width - this.line_widths[i]) / 2;
			} else if (ha == 0) {
				ha = getBidiHorizontalAlignment(this.textAlignment);

				int clientAreaWidth = d.width - insets.left - insets.right;
				if (ha == 2)
					x = insets.left + (clientAreaWidth - this.max_width) / 2;
				else if (ha == 4)
					x = insets.left + (clientAreaWidth - this.max_width) / 2 + (this.max_width - this.line_widths[i]);
				else if (ha == 0)
					x = insets.left + (clientAreaWidth - this.line_widths[i]) / 2;
			}
			x += this.btnMarginWidth;
			g.drawString(this.lines[i], x, y);

			y += this.line_height;
		}
	}

	private int getBidiHorizontalAlignment(int ha) {
		if (ha == 10) {
			if (getComponentOrientation().isLeftToRight())
				ha = 2;
			else
				ha = 4;
		} else if (ha == 11) {
			if (getComponentOrientation().isLeftToRight())
				ha = 4;
			else
				ha = 2;
		}
		return ha;
	}

	public int getVerticalAlignment() {
		return this.verticalAlignment;
	}

	public void setVerticalAlignment(int verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		repaint();
	}

	public int getHorizontalAlignment() {
		return this.horizontalAlignment;
	}

	public void setHorizontalAlignment(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		repaint();
	}

	public int getTextAlignment() {
		return this.textAlignment;
	}

	public void setTextAlignment(int textAlignment) {
		this.textAlignment = textAlignment;
		repaint();
	}

	public static void main(String[] args) throws Exception {
		String wiseText = "This is a true example\nOf the MultiLine\nLabel class";

		MultiLabel x0 = new MultiLabel(wiseText);
		MultiLabel x1 = new MultiLabel(wiseText, 4, 4);
		MultiLabel x2 = new MultiLabel(wiseText, 0);
		MultiLabel x3 = new MultiLabel(wiseText, 0, 4);
		MultiLabel x4 = new MultiLabel(wiseText, 0, 0);
		MultiLabel x5 = new MultiLabel(wiseText, 4, 2, 3);
		MultiLabel x6 = new MultiLabel(wiseText);

		JPanel mainPanel = new JPanel(new GridLayout(3, 2, 15, 15));
		mainPanel.add(x0);
		mainPanel.add(x1);
		mainPanel.add(x2);
		mainPanel.add(x3);
		mainPanel.add(x4);
		mainPanel.add(x5);
		mainPanel.setBackground(SystemColor.control);
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		for (int i = 0; i < mainPanel.getComponentCount(); i++) {
			((JComponent) mainPanel.getComponent(i)).setBorder(new LineBorder(Color.red));
		}

		JFrame fr = new JFrame("MultiLabel Example");
		fr.getContentPane().add(mainPanel);
		fr.setDefaultCloseOperation(3);
		fr.setSize(460, 350);
		fr.setLocationRelativeTo(null);
		fr.show();
	}
}