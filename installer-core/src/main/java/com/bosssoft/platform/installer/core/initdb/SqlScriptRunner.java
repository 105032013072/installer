package com.bosssoft.platform.installer.core.initdb;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.initdb.script.DB2Script;
import com.bosssoft.platform.installer.core.initdb.script.InformixScript;
import com.bosssoft.platform.installer.core.initdb.script.MySqlScript;
import com.bosssoft.platform.installer.core.initdb.script.OracleScript;
import com.bosssoft.platform.installer.core.initdb.script.SQLServerScript;
import com.bosssoft.platform.installer.core.initdb.script.SqlScript;
import com.bosssoft.platform.installer.core.initdb.script.SybaseScript;
import com.bosssoft.platform.installer.core.util.I18nUtil;

public class SqlScriptRunner {
	private Logger logger = Logger.getLogger(getClass());
	public String run(Connection connection, IContext context) {
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
			String[] sqlScripts=context.getStringValue("DB_INIT_SQLSCRIPT").split(",");
			for (String scriptFile : sqlScripts) {
		       String scriptPath="file:"+scriptFile;
				try {
					runScript(script, connection, new URL(scriptPath), "UTF-8", messageResultBuf);
				} catch (Throwable e) {
					int result=MainFrameController.showConfirmDialog("初始化数据库失败，是否继续", I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2);
					   if(result==1) System.exit(0);
					logger.debug("Run database script error!",e);
				}
			}
			return messageResultBuf.toString();
		} catch (Throwable t) {
			logger.debug("initialize database error!",t);
			throw new InstallException();
		}
	
	}
	private void runScript(SqlScript script, Connection connection, URL defineUrl, String encoding, StringBuffer messageResultBuf) {

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
   
}
