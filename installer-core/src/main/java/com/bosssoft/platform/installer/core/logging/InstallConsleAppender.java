package com.bosssoft.platform.installer.core.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class InstallConsleAppender extends ConsoleAppender {
	private Level maxLevel;

	public Level getMaxLevel() {
		return this.maxLevel;
	}

	public void setMaxLevel(Level lessEqual) {
		this.maxLevel = lessEqual;
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