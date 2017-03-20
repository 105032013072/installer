package com.bosssoft.platform.installer.wizard.action;


import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.wizard.util.JDBCURLUtil;

public class SilentInit implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String dbURL = getJDBCURL(context);
		context.setValue("DB_URL", dbURL);
	}

	private String getJDBCURL(IContext context) {
		String dbType = context.getStringValue("DB_TYPE").toLowerCase();
		String ip = context.getStringValue("DB_IP");
		String port = context.getStringValue("DB_SERVER_PORT");
		String sid = context.getStringValue("DB_NAME");

		String isCluster = context.getStringValue("IS_CLUSTER");
		if (isCluster.equals("")) {
			context.setValue("IS_CLUSTER", "false");
		}
		String url = null;
		if (dbType.startsWith("oracle")) {
			url = JDBCURLUtil.getOracleURL(ip, port, sid);
		} else if (dbType.startsWith("db2")) {
			url = JDBCURLUtil.getDB2URL(ip, port, sid);
		} else if (dbType.startsWith("sybase")) {
			url = JDBCURLUtil.getSybaseURL(ip, port, sid);
		} else if ((dbType.startsWith("sql server")) || (dbType.startsWith("sqlserver"))) {
			url = JDBCURLUtil.getSQLServerURL(ip, port, sid);
		} else if (dbType.startsWith("informix")) {
			String serverName = context.getStringValue("DB_INFORMIX_SERVER");
			url = JDBCURLUtil.getInformixURL(ip, port, sid, serverName);
		} else if (dbType.startsWith("mysql")) {
			url = JDBCURLUtil.getMySqlURL(ip, port, sid);
		}
		return url;
	}

	public void rollback(IContext arg0, Map arg1) throws InstallException {
	}
}