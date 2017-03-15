package com.bosssoft.platform.installer.wizard.gui.validate.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateValidator extends AbstractStringValidator {
	private String datePattern;
	private boolean strict;
	private Locale locale;

	public DateValidator(String datePattern, boolean strict) {
		this.datePattern = datePattern;
		this.strict = strict;
	}

	public DateValidator(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("the locale must not be null!");
		}
		this.locale = locale;
	}

	private boolean isValid(String value, String datePattern, boolean strict) {
		if ((value == null) || (datePattern == null) || (datePattern.length() <= 0)) {
			return false;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
		formatter.setLenient(false);
		try {
			formatter.parse(value);
		} catch (ParseException e) {
			return false;
		}

		if ((strict) && (datePattern.length() != value.length())) {
			return false;
		}

		return true;
	}

	private boolean isValid(String value, Locale locale) {
		if (value == null) {
			return false;
		}

		DateFormat formatter = null;
		if (locale != null)
			formatter = DateFormat.getDateInstance(3, locale);
		else {
			formatter = DateFormat.getDateInstance(3, Locale.getDefault());
		}

		formatter.setLenient(false);
		try {
			formatter.parse(value);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

	protected boolean onValidate(String value) {
		if (this.locale == null) {
			return isValid(value, this.locale);
		}
		return isValid(value, this.datePattern, this.strict);
	}
}