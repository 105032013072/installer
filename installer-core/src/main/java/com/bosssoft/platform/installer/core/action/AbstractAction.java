package com.bosssoft.platform.installer.core.action;

public abstract class AbstractAction implements IAction {
	private int scale = 1;
	public static final String STRATEGY_RETRY = "retry";
	public static final String STRATEGY_QUIT = "quit";
	public static final String STRATEGY_IGNORE = "ignore";
	private String strategy = "retry";

	public int getScale() {
		return this.scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getStrategy() {
		return this.strategy;
	}

	public void setStrategy(String s) {
		s = s.toLowerCase();
		if ((s.equals("retry")) || (s.equals("quit")) || (s.equals("ignore")))
			this.strategy = s;
		else
			throw new IllegalArgumentException("Can not identify the strategy：" + s);
	}
}