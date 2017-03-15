package com.bosssoft.platform.installer.core.util;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;

public class ExpressionParser {
	static Logger logger = Logger.getLogger(ExpressionParser.class);
	private static IContext context = InstallRuntime.INSTANCE.getContext();

	public static String parseString(String s) {
		return getVariableValue(s);
	}

	private static String getVariableValue(String v) {
		String value = "";
		int start = v.indexOf("${");
		int end = v.indexOf("}");
		if ((start < 0) || (end < 0) || (start > end)) {
			return v;
		}
		String key = v.substring(start + 2, end);

		Object objValue = context.getValue(key);
		if (objValue == null) {
			logger.debug("not found variable as '" + key + "'");
			return v;
		}
		value = v.substring(0, start) + objValue.toString() + v.substring(end + 1);
		return getVariableValue(value);
	}
}