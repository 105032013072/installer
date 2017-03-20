package com.bosssoft.platform.installer.core.initdb;

import java.sql.Connection;

public class InstallDbInit {
	public static boolean initdb(Connection connection) {
		IDatabaseInitializer initializer = DatabaseInitializerFactory.getDatabaseInitializer();
		String result = initializer.initialize(connection, initializer.getComponentNames());
		if ((result == null) || (result.trim().length() == 0)) {
			return true;
		}
		return false;
	}
}