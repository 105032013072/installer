package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Map;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.io.xml.XmlFile;

public class CreateDataSource implements IAction {
	public void execute(IContext context, Map parameters) throws InstallException {
		String userConfigPath = parameters.get("USER_CONFIG_PATH").toString();

		String dbname = context.getStringValue("DB_TYPE");
		String driver = context.getStringValue("DB_DRIVER");
		String url = context.getStringValue("DB_URL");
		String user = context.getStringValue("DB_USERNAME");
		String password = context.getStringValue("DB_PASSWORD");

		boolean b = addC3p0DataSource(userConfigPath, dbname, driver, user, password, url);
		if (!b)
			throw new InstallException("Add C3P0 DataSource failed!");
	}

	protected boolean addC3p0DataSource(String userConfigPath, String dbname, String driver, String user, String password, String url) {
		try {
			XmlFile xmlFile = new XmlFile(new File(userConfigPath));
			String xql = "//module[@name='DataSource']/group[@name='default']";
			xmlFile.removeNode(xql);
			xql = "//module[@name='DataSource']";

			StringBuffer ds = new StringBuffer();
			ds.append("<group name=\"default\">").append("<configValue key=\"Database-Type\">").append(dbname).append("</configValue>")
					.append("<configValue key=\"Jdbc-Type\"></configValue>").append("<configValue key=\"C3p0-DriverClass\">").append(driver).append("</configValue>")
					.append("<configValue key=\"C3p0-Url\">").append(url).append("</configValue>").append("<configValue key=\"C3p0-UserName\">").append(user)
					.append("</configValue>").append("<configValue key=\"C3p0-Password\">").append(password).append("</configValue>")
					.append("<configValue key=\"C3p0-PoolSize\">5</configValue>").append("<configValue key=\"C3p0-MaxPoolSize\">5</configValue>")
					.append("<configValue key=\"C3p0-MinPoolSize\">5</configValue>").append("<configValue key=\"Transaction-Isolation\">ISOLATION_DEFAULT</configValue>")
					.append("<configValue key=\"Test-Connect-Sql\">SELECT count(*) from EOS_UNIQUE_TABLE</configValue>")
					.append("<configValue key=\"Retry-Connect-Count\">-1</configValue>").append("</group>");

			xmlFile.addNode(xql, ds.toString());
			xmlFile.save();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		XmlFile xmlFile;
		return true;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public static void main(String[] args) {
		String userConfigPath = "E:/installtest/user-config.xml";

		CreateDataSource c = new CreateDataSource();
		c.addC3p0DataSource(userConfigPath, "dbname", "driver", "user", "password", "url");
	}
}