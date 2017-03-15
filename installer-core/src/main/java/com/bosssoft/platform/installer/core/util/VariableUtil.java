package com.bosssoft.platform.installer.core.util;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;

public class VariableUtil {
	protected static Logger logger = Logger.getLogger(VariableUtil.class);

	public static boolean isVariable(String v) {
		boolean rtn = (v.trim().startsWith("${")) && (v.trim().endsWith("}"));
		return rtn;
	}

	public static String getVariableKey(Object v) {
		String value = v.toString().trim();
		if (isVariable(value))
			value = value.substring(2, value.length() - 1);
		return value;
	}

	public static String getVariableValue(IContext context, String v) {
		String value = "";
		int start = v.indexOf("${");
		int end = v.indexOf("}");
		if ((start < 0) || (end < 0) || (start > end)) {
			return v;
		}
		String key = v.substring(start + 2, end);

		Object objValue = context.getValue(key);
		if (objValue == null) {
			logger.error("Not found Variable: " + key);
			objValue = "{" + key + "}";
		}
		value = v.substring(0, start) + objValue.toString() + v.substring(end + 1);
		return getVariableValue(context, value);
	}
}