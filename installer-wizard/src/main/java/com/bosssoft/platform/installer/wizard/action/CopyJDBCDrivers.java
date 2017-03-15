package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.io.FileUtils;

public class CopyJDBCDrivers implements IAction {
	transient Logger logger = Logger.getLogger(getClass());

	public void execute(IContext context, Map map) throws InstallException {
		String targetDirs = map.get("TARGET_DIRS").toString();
		String jdbcDir = map.get("JDBC_DIR").toString();

		StringTokenizer st = new StringTokenizer(targetDirs, ";");
		ArrayList tdList = new ArrayList();
		while (st.hasMoreTokens()) {
			tdList.add(st.nextToken());
		}

		String isDefaultJdbcDriver = context.getStringValue("DB_IS_DEFAULT_JAR").toLowerCase();
		if ("true".equals(isDefaultJdbcDriver)) {
			int m = 0;
			for (int n = tdList.size(); m < n; m++)
				try {
					FileUtils.copy(new File(jdbcDir), new File(tdList.get(m).toString()), null, FileOperationMessageListener.INSTANCE);
				} catch (Exception ioe) {
					this.logger.error("Copy file '" + jdbcDir + "' error", ioe);
				}
		} else {

		}
	}

	public void rollback(IContext context, Map map) throws InstallException {
	}
}