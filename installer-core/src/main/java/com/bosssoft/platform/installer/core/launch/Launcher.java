package com.bosssoft.platform.installer.core.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.bosssoft.platform.installer.core.Constants;
import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.cfg.InstallConfig;
import com.bosssoft.platform.installer.core.cfg.InstallConfigLoader;
import com.bosssoft.platform.installer.core.cfg.Variable;
import com.bosssoft.platform.installer.core.context.IContextFactory;
import com.bosssoft.platform.installer.core.event.IListener;
import com.bosssoft.platform.installer.core.gui.IRenderer;
import com.bosssoft.platform.installer.core.i18n.ResourceRegister;
import com.bosssoft.platform.installer.core.navigator.INavigator;
import com.bosssoft.platform.installer.core.runtime.IRunner;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.runtime.SilentRunner;
import com.bosssoft.platform.installer.core.runtime.SwingRunner;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class Launcher implements Constants {
	private static final String DEFAULT_CONTEXTFACTORY_CLASS = "com.bosssoft.platform.installer.core.context.DefaultContextFactory";
	private static final String DEFAULT_NAVIGATOR_CLASS = "com.bosssoft.platform.installer.core.navigate.DefaultNavigator";
	private static final String DEFAULT_RENDERER_CLASS = "com.bosssoft.platform.installer.core.gui.DefaultRenderer";
	private static final String DEFAULT_LOGFILE = "bosssoft_install.log";

	public static void main(String[] args) {
		try {
			Launcher launcher = new Launcher();
			launcher.run(args);
		} catch (LaunchException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

	public void run(String[] args) throws LaunchException {
		String logPath = configLog4j();

		String installRootDir = InstallerFileManager.getInstallerRoot();
		String configFilePath = getConfigFilePath(installRootDir);

		if (!isValidFilePath(configFilePath)) {
			throw new LaunchException("Config File is not File or not exist!" + configFilePath);
		}
		InstallConfig installConfig = null;
		try {
			installConfig = InstallConfigLoader.loadConfig(configFilePath);
		} catch (FileNotFoundException e) {
			throw new LaunchException(e.getMessage());
		}

		InstallRuntime runtime = InstallRuntime.INSTANCE;
		runtime.setConfig(installConfig);
		runtime.setInstallRoot(installRootDir);

		String contextFactoryClassname = installConfig.getContextFactory();
		if (contextFactoryClassname == null) {
			contextFactoryClassname = DEFAULT_CONTEXTFACTORY_CLASS;
		}
		try {
			IContextFactory contextFactory = (IContextFactory) Class.forName(contextFactoryClassname).newInstance();
			runtime.setContext(contextFactory.createContext());
			if (logPath != null) {
				runtime.getContext().setValue("INSTALL_LOGFILE_PATH", logPath);
				runtime.getContext().setValue("INSTALL_LOGFILE_NAME", logPath.substring(logPath.lastIndexOf("/") + 1));
			}

			putVariable2Context(installConfig, runtime.getContext());

			loadProperties2Context(installConfig, runtime.getContext());

			registerListener(installConfig, runtime);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LaunchException(e.getMessage());
		}

		String navigatorClassname = installConfig.getNavigator();
		if (navigatorClassname == null)
			navigatorClassname = DEFAULT_NAVIGATOR_CLASS;
		try {
			INavigator navigator = (INavigator) Class.forName(navigatorClassname).newInstance();
			runtime.setNavigator(navigator);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LaunchException(e.getMessage());
		}

		String rendererClassname = installConfig.getRenderer();
		if (rendererClassname == null)
			rendererClassname = DEFAULT_RENDERER_CLASS;
		try {
			IRenderer renderer = (IRenderer) Class.forName(rendererClassname).newInstance();
			runtime.setRenderer(renderer);
		} catch (Exception e) {
			throw new LaunchException(e.getMessage());
		}

		loadI18nProperties();

		String runmode = System.getProperty("install.runmode");
		if (runmode == null)
			runmode = "swing";
		else {
			runmode = runmode.toLowerCase();
		}
		runtime.setRunMode(runmode);

		IRunner runner = null;
		if (runtime.isSilentInstall())
			runner = new SilentRunner(installConfig);
		else {
			runner = new SwingRunner(installConfig);
		}

		runner.execute();
	}

	private void loadProperties2Context(InstallConfig installConfig, IContext context) throws IOException {
		List<String> list = installConfig.getLoadProperties();
		String installHome = InstallRuntime.INSTANCE.getInstallRoot();
		InputStream inStream = null;
		Properties propers = new Properties();
		Enumeration keys = null;
		String key = null;
		for (String path : list) {
			path = path.replace("\\", "/");
			if (path.indexOf("/") < 0)
				path = InstallerFileManager.getConfigDir() + File.separator + path;
			inStream = new FileInputStream(path);
			propers.load(inStream);
			keys = propers.keys();
			while (keys.hasMoreElements()) {
				key = keys.nextElement().toString();
				context.setValue(key, propers.get(key));
			}
			propers.clear();
		}
	}

	private void registerListener(InstallConfig installConfig, InstallRuntime runtime) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List<String> listeners = installConfig.getListeners();
		IListener listener = null;
		for (String className : listeners) {
			listener = (IListener) Class.forName(className).newInstance();
			runtime.addListener(listener);
		}
	}

	private void putVariable2Context(InstallConfig installConfig, IContext context) {
		List<Variable> variables = installConfig.getVariables();
		for (Variable v : variables)
			context.setValue(v.getKey(), v.getValue());
	}

	private void loadI18nProperties() {
		File i18nDir = new File(InstallRuntime.INSTANCE.getInstallRoot() + "/i18n");
		if (!i18nDir.exists())
			return;
		File[] i18nFiles = i18nDir.listFiles();
		for (int i = 0; i < i18nFiles.length; i++)
			ResourceRegister.registerResource("I18N", i18nFiles[i].getAbsolutePath());
	}

	private String getConfigFilePath(String installHome) {
		String configFileName = System.getProperty("install.config.name");
		if (configFileName == null) {
			configFileName = "install.xml";
		}
		String path = InstallerFileManager.getConfigDir() + File.separator + configFileName;

		return path;
	}

	private boolean isValidFilePath(String path) {
		if (path == null) {
			return false;
		}
		File f = new File(path);

		if ((!f.exists()) || (f.isDirectory())) {
			return false;
		}
		return true;
	}

	private String configLog4j() {
		String logPath = null;
		String userHome = System.getProperty("user.home");
		String logFileName = System.getProperty("install.logfile");
		if ((logFileName == null) || (logFileName.trim().length() == 0))
			logFileName = DEFAULT_LOGFILE;
		userHome = userHome.replace('\\', '/');
		File logfile = new File(userHome + "/" + logFileName);
		if (logfile.exists())
			logfile.delete();

		Properties properties = new Properties();
		String configPath = InstallerFileManager.getConfigDir() + "/log4j.properties";
		try {
			properties.load(new FileInputStream(configPath));
		} catch (IOException e) {
			System.out.println("Cann't find file log4j.properties in path:" + configPath);
			e.printStackTrace();
			return null;
		}

		logPath = userHome + "/" + logFileName;
		properties.setProperty("log4j.appender.InstallFile.File", logPath);

		if (System.getProperty("install.debug", "false").equals("true")) {
			properties.setProperty("log4j.appender.InstallFile.Threshold", "DEBUG");
		}

		PropertyConfigurator.configure(properties);
		return logPath;
	}
}