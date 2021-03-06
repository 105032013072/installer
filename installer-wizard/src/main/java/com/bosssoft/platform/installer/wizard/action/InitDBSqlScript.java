package com.bosssoft.platform.installer.wizard.action;

import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.initdb.SqlScriptRunner;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.util.DBConnectionUtil;

/**
 * 通过sql执行数据库脚本
 * @author huangxw
 *
 */
public class InitDBSqlScript implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		Connection connection=getConnection(context);
		SqlScriptRunner runner=new SqlScriptRunner();
		runner.run(connection,context);
	}

	private Connection getConnection(IContext context) {
		Connection conn = null;
		String user = context.getStringValue("DB_USERNAME");
		String password = context.getStringValue("DB_PASSWORD");
		String url = context.getStringValue("DB_URL");
		String driver = context.getStringValue("DB_DRIVER");
		String userJdbcJars = context.getStringValue("DB_JDBC_LIBS");
		try {
			conn = DBConnectionUtil.getConnection(userJdbcJars, driver, url, user, password);
		} catch (Exception e) {
			throw new InstallException("faild to get DB conection "+e);
		}
		return conn;
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
