package com.bosssoft.platform.installer.core.action;

import java.util.Map;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.ExpressionParser;

public class Echo extends AbstractAction {
	transient Logger logger = Logger.getLogger(getClass());

	private String message = null;
	private String logLevel = null;

	private static String LOG_LEVEL_INFO = "info";
	private static String LOG_LEVEL_DEBUG = "debug";
	private static String LOG_LEVEL_WARN = "warn";
	private static String LOG_LEVEL_ERROR = "error";
	private static String LOG_LEVEL_FATAL = "fatal";

	public void execute(IContext context, Map parameters) throws InstallException {
		String msg = ExpressionParser.parseString(this.message);
		if ((this.logLevel == null) || (this.logLevel.equals(LOG_LEVEL_INFO)))
			this.logger.info(msg);
		else if (this.logLevel.equals(LOG_LEVEL_DEBUG))
			this.logger.debug(msg);
		else if (this.logLevel.equals(LOG_LEVEL_WARN))
			this.logger.warn(msg);
		else if (this.logLevel.equals(LOG_LEVEL_ERROR))
			this.logger.error(msg);
		else if (this.logLevel.equals(LOG_LEVEL_FATAL))
			this.logger.fatal(msg);
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setLevel(String level) {
		if (level != null)
			this.logLevel = level.toLowerCase();
	}
}