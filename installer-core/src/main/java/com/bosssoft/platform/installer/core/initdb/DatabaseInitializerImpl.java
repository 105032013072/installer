package com.bosssoft.platform.installer.core.initdb;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosssoft.platform.installer.core.initdb.script.DB2Script;
import com.bosssoft.platform.installer.core.initdb.script.InformixScript;
import com.bosssoft.platform.installer.core.initdb.script.MySqlScript;
import com.bosssoft.platform.installer.core.initdb.script.OracleScript;
import com.bosssoft.platform.installer.core.initdb.script.SQLServerScript;
import com.bosssoft.platform.installer.core.initdb.script.SqlScript;
import com.bosssoft.platform.installer.core.initdb.script.SybaseScript;

public class DatabaseInitializerImpl implements IDatabaseInitializer {
	static Logger logger = LoggerFactory.getLogger(DatabaseInitializerImpl.class);

	public String initialize(Connection connection, String[] componentNames) {
		if (connection == null) {
			return "Connection is null!";
		}
		if ((componentNames == null) || (componentNames.length == 0))
			return null;
		try {
			DatabaseMetaData metaData = connection.getMetaData();
			String databaseProductName = metaData.getDatabaseProductName();
			SqlScript script = null;
			String dbType = null;
			if (databaseProductName.toLowerCase().indexOf("db2") != -1) {
				script = new DB2Script();
				dbType ="db2";
			} else if (databaseProductName.toLowerCase().indexOf("oracle") != -1) {
				script = new OracleScript();
				dbType = "oracle";
			} else if (databaseProductName.toLowerCase().indexOf("sql server") != -1) {
				script = new SQLServerScript();
				dbType = "sqlserver";
			} else if (databaseProductName.toLowerCase().indexOf("informix") != -1) {
				script = new InformixScript();
				dbType = "informix";
			} else if ((databaseProductName.toLowerCase().indexOf("adaptive server enterprise") != -1)
					|| (databaseProductName.toLowerCase().indexOf("sybase adaptive server iq") != -1)) {
				script = new SybaseScript();
				dbType = "sybase";
			} else if (databaseProductName.toLowerCase().indexOf("mysql") != -1) {
				script = new MySqlScript();
				dbType = "mysql";
			} else {
				return "Not support this database " + databaseProductName;
			}

			StringBuffer messageResultBuf = new StringBuffer();
			for (String moduleName : componentNames) {
				if (moduleName != null) {
					for (String[] scriptDefine : ScriptDefineLoader.getDefineScripts(moduleName, dbType))
						if ((scriptDefine != null) && (scriptDefine.length == 2)) {
							try {
								runScript(script, connection, new URL(scriptDefine[0]), scriptDefine[1], messageResultBuf);
							} catch (Throwable e) {
								logger.debug("Run database script error!",e);
							}
						}
				}
			}
			return messageResultBuf.toString();
		} catch (Throwable t) {
			logger.debug("initialize database error!",t);
			return t.getMessage();
		}
	}

	private static void runScript(SqlScript script, Connection connection, URL defineUrl, String encoding, StringBuffer messageResultBuf) {
		InputStream in = null;
		try {
			in = defineUrl.openStream();
			script.run(in, encoding == null ? "UTF-8" : encoding, connection, messageResultBuf);
		} catch (Throwable t) {
			logger.debug("run database script error!",t);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Throwable ignore) {
			}
		}
	}

	public String[] getComponentNames() {
		return ScriptDefineLoader.getComponentNames();
	}

	public boolean isInitialized(Connection connection, String componentName) {
		if (connection == null) {
			return false;
		}

		String tableName = ScriptDefineLoader.getTestTableName(componentName);

		ResultSet rs = null;
		Statement stmt = null;
		String testSql = null;
		if ((tableName != null) && (tableName.trim().length() > 0))
			testSql = "select * from " + tableName;
		else {
			return false;
		}
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(testSql);
			return true;
		} catch (Exception ex) {
			logger.debug("",ex);
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception ex) {
			}
		}
	}
}