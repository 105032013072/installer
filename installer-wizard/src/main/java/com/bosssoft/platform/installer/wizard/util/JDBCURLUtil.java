package com.bosssoft.platform.installer.wizard.util;

public class JDBCURLUtil {
	public static String getOracleURL(String ip, String port, String sid) {
		String url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + sid;

		return url;
	}

	public static String getDB2URL(String ip, String port, String sid) {
		String url = "jdbc:db2://" + ip + ":" + port + "/" + sid;

		return url;
	}

	public static String getSQLServerURL(String ip, String port, String sid) {
		String url = "jdbc:microsoft:sqlserver://" + ip + ":" + port + ";DatabaseName=" + sid + ";SelectMethod=Cursor";

		return url;
	}

	public static String getSybaseURL(String ip, String port, String sid) {
		String url = "jdbc:sybase:Tds:" + ip + ":" + port + "/" + sid + "?CHARSET=utf8";

		return url;
	}

	public static String getInformixURL(String ip, String port, String sid, String serverName) {
		String url = "jdbc:informix-sqli://" + ip + ":" + port + ":INFORMIXSERVER=" + serverName + ";Database=" + sid;

		return url;
	}

	public static String getMySqlURL(String ip, String port, String sid) {
		String url = "jdbc:mysql://" + ip + ":" + port + "/" + sid;

		return url;
	}
}