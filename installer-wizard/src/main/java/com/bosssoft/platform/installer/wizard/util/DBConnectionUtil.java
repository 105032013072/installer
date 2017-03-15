package com.bosssoft.platform.installer.wizard.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.util.ReflectUtil;

public class DBConnectionUtil {
	public static final int VALID = 100;
	public static final int NO_CREATETABLE_PERMISSION = 0;
	public static final int USERORPWD_EEROR = -1;
	public static final int DRIVER_ERROR = -2;
	public static final int ORTHER_ERROR = -3;
	public static final int JDBCJAR_ERROR = -4;
	private static Hashtable classLoaderCache = new Hashtable();

	public static Connection getConnection(String jdbcDriver, String url, String user, String password) throws ClassNotFoundException, SQLException {
		return getConnection(null, jdbcDriver, url, user, password);
	}

	public static Connection getConnection(String userJdbcJars, String jdbcDriver, String url, String userName, String password) throws ClassNotFoundException, SQLException {
		Connection connection = null;
		userJdbcJars = userJdbcJars == null ? "" : userJdbcJars;
		if ((userJdbcJars == null) || ("".equals(userJdbcJars))) {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(url, userName, password);
		} else {
			Driver driver = null;
			try {
				driver = getJDBCDriver(userJdbcJars, jdbcDriver);
				connection = getConnection(driver, url, userName, password);
			} catch (MalformedURLException localMalformedURLException) {
			}
		}
		return connection;
	}

	public static int validateDBConfig(String driverFiles, String jdbcDriver, String url, String userName, String password) {
		if ((jdbcDriver == null) || (jdbcDriver.trim().length() == 0))
			return DRIVER_ERROR;
		Driver driver = null;
		try {
			driver = getJDBCDriver(driverFiles, jdbcDriver);
		} catch (MalformedURLException e) {
			e.printStackTrace();

			return JDBCJAR_ERROR;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();

			return JDBCJAR_ERROR;
		}

		Connection conn = null;
		try {
			conn = getConnection(driver, url, userName, password);
			if (conn != null) {
				return 100;
			}
			return ORTHER_ERROR;
		} catch (SQLException ex) {
		}
		return USERORPWD_EEROR;
	}

	private static boolean isValid(Connection conn) {
		if (conn == null)
			return false;
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.close();
		} catch (SQLException ex) {
			return false;
		}
		return true;
	}

	private static URLClassLoader getURLClassLoader(String driverFiles) throws MalformedURLException {
		URLClassLoader loader = (URLClassLoader) classLoaderCache.get(driverFiles);
		if (loader == null) {
			StringTokenizer st = new StringTokenizer(driverFiles, ";");
			ArrayList<File> fileList = new ArrayList<File>();
			File file = null;
			while (st.hasMoreTokens()) {
				file = new File(st.nextToken());
				fileList.add(file);
			}
			URL[] urls = new URL[fileList.size()];
			int i = 0;
			for (int length = urls.length; i < length; i++) {
				urls[i] = ((File) fileList.get(i)).toURL();
			}
			loader = urls.length > 0 ? new URLClassLoader(urls) : null;
			if (loader != null) {
				classLoaderCache.put(driverFiles, loader);
			}
		}
		return loader;
	}

	public static List getAllDriverClassName(File[] files) {
		List list = new ArrayList();

		if (files == null || files.length == 0)
			return list;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, j = files.length; i < j; i++) {
			buffer.append(files[i].getAbsolutePath());
			if (i < j - 1)
				buffer.append(File.pathSeparator);
		}
		try {
			URLClassLoader loader = getURLClassLoader(buffer.toString());
			for (int i = 0, length = files == null ? 0 : files.length; i < length; i++) {
				JarFile jar = new JarFile(files[i]);
				addCandidateDriversToList(list, loader, jar);
			}
		} catch (IOException e) {

		}
		return list;
	}

	private static void addCandidateDriversToList(List list, URLClassLoader loader, JarFile jar) {
		for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();) {
			JarEntry entry =  e.nextElement();
			String className = getClassNameFromFileName(entry.getName());
			if (className != null)
				try {
					Class driverClass = loadDriverClass(className, loader);
					if (driverClass != null)
						list.add(driverClass.getName());
				} catch (NoClassDefFoundError localNoClassDefFoundError) {
				} catch (ClassNotFoundException localClassNotFoundException) {
				} catch (RuntimeException localRuntimeException) {
				}
		}
	}

	private static String getClassNameFromFileName(String name) {
		String result = null;
		if (name.endsWith(".class")) {
			result = name.substring(0, name.length() - 6).replace('/', '.').replace('\\', '.');
		}
		return result;
	}

	private static Class loadDriverClass(String className, ClassLoader loader) throws ClassNotFoundException {
		Class driverClass = loader.loadClass(className);
		return Driver.class.isAssignableFrom(driverClass) ? driverClass : null;
	}

	public static Connection getConnection(IContext context) {
		Connection conn = null;
		String user = context.getStringValue("DB_USERNAME");
		String pwd = context.getStringValue("DB_PASSWORD");
		String url = context.getStringValue("DB_URL");
		String driver = context.getStringValue("DB_DRIVER");
		String userJdbcJars = context.getStringValue("DB_JDBC_LIBS");
		try {
			conn = getConnection(userJdbcJars, driver, url, user, pwd);
		} catch (Exception e) {
			throw new InstallException("Cann't get a db Connenction!");
		}
		return conn;
	}

	public static Driver getJDBCDriver(String userJdbcJars, String jdbcDriver) throws MalformedURLException, ClassNotFoundException {
		userJdbcJars = userJdbcJars == null ? "" : userJdbcJars;

		Driver driver = null;
		if ((userJdbcJars == null) || (userJdbcJars.trim().length() == 0)) {
			driver = (Driver) ReflectUtil.newInstanceBy(jdbcDriver);
			return driver;
		}

		URLClassLoader loader = getURLClassLoader(userJdbcJars);
		Class driverClass = loader.loadClass(jdbcDriver);
		driver = (Driver) ReflectUtil.newInstanceBy(driverClass);
		return driver;
	}

	public static Connection getConnection(Driver driver, String url, String userName, String password) throws SQLException {
		Properties properties = new Properties();
		properties.setProperty("user", userName);
		properties.setProperty("password", password);

		return driver.connect(url, properties);
	}

	public static void main(String[] args) {
		String userDriverPath = "d:/bak/ojdbc14.jar";
		String jdbcDriver = "";
		String url = "jdbc:oracle:thin:@192.168.1.251:1521:eos";
		String userName = "lihang";
		String password = "lihang";
		try {
			Connection conc = getConnection(userDriverPath, jdbcDriver, url, userName, password);
			System.out.print(conc.isReadOnly());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}