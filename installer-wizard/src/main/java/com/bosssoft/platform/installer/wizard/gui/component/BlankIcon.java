package com.bosssoft.platform.installer.wizard.gui.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;

public class BlankIcon implements Icon {
	private Dimension dim = null;

	public BlankIcon(Dimension dim) {
		this.dim = dim;
	}

	public int getIconWidth() {
		return (int) this.dim.getWidth();
	}

	public int getIconHeight() {
		return (int) this.dim.getHeight();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	}
}