package com.bosssoft.platform.installer.wizard.gui.validate.impl;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class PatternValidator extends AbstractStringValidator {
	private String pattern;
	private Pattern rexPattern;

	private void buildPattern() {
		if ((this.rexPattern == null) && (this.pattern != null)) {
			this.rexPattern = Pattern.compile(this.pattern);
		}
	}

	public String getPattern() {
		return this.pattern;
	}

	public final void setPattern(String r_Pattern) {
		this.pattern = r_Pattern;
	}

	protected boolean onValidate(String r_Value) {
		if (!StringUtils.isEmpty(r_Value)) {
			buildPattern();

			return this.rexPattern.matcher(r_Value).matches();
		}

		return true;
	}
}