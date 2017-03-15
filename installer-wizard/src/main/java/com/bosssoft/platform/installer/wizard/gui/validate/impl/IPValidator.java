package com.bosssoft.platform.installer.wizard.gui.validate.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class IPValidator extends AbstractStringValidator {
	public IPValidator() {
	}

	public IPValidator(boolean ignoreCase) {
		super(ignoreCase);
	}

	protected boolean onValidate(String value) {
		if (value == null)
			return false;
		if (value.equalsIgnoreCase("localhost")) {
			return true;
		}
		String[] parts = StringUtils.splitPreserveAllTokens(value, ".");
		if ((parts != null) && (parts.length == 4)) {
			for (int i = 0; i < parts.length; i++) {
				if (parts[i].length() > 3) {
					return false;
				}

				if (NumberUtils.isDigits(parts[i])) {
					int val = 0;
					try {
						val = NumberUtils.createInteger(parts[i]).intValue();
					} catch (Exception ee) {
						return false;
					}
					if ((val <= -1) || (val >= 256)) {
						return false;
					}
				} else {
					return false;
				}

			}

			return true;
		}

		return false;
	}
}