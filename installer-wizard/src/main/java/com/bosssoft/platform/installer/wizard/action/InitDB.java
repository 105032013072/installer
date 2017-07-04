package com.bosssoft.platform.installer.wizard.action;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.wizard.util.DBConnectionUtil;

public class InitDB implements IAction {
	static transient Logger logger = Logger.getLogger(InitDB.class);

	public void execute(IContext context, Map parameters) throws InstallException {
		String className = parameters.get("CLASS_NAME").toString();
		String methodName = parameters.get("METHOD_NAME").toString();
		String isForceInit = context.getStringValue("DB_IS_FORCE_INIT");

		Connection connection = getConnection(context);
		logger.debug("Connection == null " + (connection == null));
		if (connection == null) {
			throw new InstallException("Could not connect to DB, init DB failed!");
		}

		if ((!"".equals(isForceInit)) && (!Boolean.valueOf(isForceInit).booleanValue()) && (isInitialized(connection))) {
			logger.debug("database has been init");
			return;
		}

		Object[] args = { connection };
		Class[] argsClass = { Connection.class };
		try {
			Object rtn = invokeStaticMethod(className, methodName, args, argsClass);
			boolean b = ((Boolean) rtn).booleanValue();
			logger.debug("initdb result is " + b);
			if (!b)
				throw new InstallException("Init DB failed!");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	public static boolean isInitialized(Connection connection) {
		Object[] args = { connection, "" };
		Class[] argsClass = { Connection.class, String.class };
		try {
			Class ownerClass = Class.forName("com.bosssoft.platform.installer.core.initdb.DatabaseInitializerImpl");
			Method method = ownerClass.getMethod("isInitialized", argsClass);
			Object rtn = method.invoke(ownerClass.newInstance(), args);
			return ((Boolean) rtn).booleanValue();
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
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

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	private Object invokeStaticMethod(String className, String methodName, Object[] args, Class[] argsClass) throws Exception {
		Class ownerClass = Class.forName(className);

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(null, args);
	}
}