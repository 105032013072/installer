package com.bosssoft.platform.installer.wizard.action;

import java.sql.Connection;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.initdb.SqlScriptRunner;
import com.bosssoft.platform.installer.wizard.util.DBConnectionUtil;
import com.googlecode.flyway.core.Flyway;

/**
 * 通过flyway执行数据库脚本
 * @author huangxw
 *
 */
public class InitDBSqlScriptFlyWay implements IAction{
transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String user = context.getStringValue("DB_USERNAME");
		String password = context.getStringValue("DB_PASSWORD");
		String url = context.getStringValue("DB_URL");
		
		Flyway flyway=new Flyway();
		flyway.setDataSource(url, user, password);
		flyway.setInitOnMigrate(true);
		flyway.migrate(); 
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
