package com.bosssoft.platform.installer.wizard.gui.validate.impl;

import com.bosssoft.platform.installer.wizard.gui.validate.IValidator;

public abstract class AbstractStringValidator implements IValidator {
	private boolean ignoreCase = false;

	public AbstractStringValidator() {
	}

	public AbstractStringValidator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public final boolean isValid(Object value) {
		boolean validate = false;
		if (value != null)
			validate = onValidate(value.toString());
		else {
			validate = onValidate((String) value);
		}
		return validate;
	}

	protected abstract boolean onValidate(String value);

	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
}