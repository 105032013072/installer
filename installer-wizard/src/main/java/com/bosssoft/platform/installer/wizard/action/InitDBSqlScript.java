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

public class InitDBSqlScript implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		Connection connection=getConnection(context);
		SqlScriptRunner runner=new SqlScriptRunner();
		if(connection==null) 
			MainFrameController.showConfirmDialog("数据库连接对象为空，无法进行数据库初始化，是否继续", I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2);
		
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
			logger.error(e);
		}
		return conn;
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
