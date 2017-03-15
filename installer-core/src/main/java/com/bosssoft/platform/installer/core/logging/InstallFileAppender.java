package com.bosssoft.platform.installer.core.logging;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class InstallFileAppender extends FileAppender {
	private Level maxLevel;
	private Level minLevel;

	public Level getMaxLevel() {
		return this.maxLevel;
	}

	public void setMinLevel(Level lessEqual) {
		this.maxLevel = lessEqual;
	}

	public Level getMinLevel() {
		return this.minLevel;
	}

	public void setMaxLevel(Level lessEqual) {
		this.minLevel = lessEqual;
	}

	public synchronized void doAppend(LoggingEvent event) {
		if (!isLessOrEqual(this.maxLevel, event.getLevel())) {
			return;
		}

		super.doAppend(event);
	}

	private static boolean isLessOrEqual(Level r1, Level r2) {
		if (r1 == null) {
			return true;
		}
		if (r2 == null) {
			return false;
		}
		if (r1.toInt() >= r2.toInt()) {
			return true;
		}
		return false;
	}
}