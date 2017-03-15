package com.bosssoft.platform.installer.jee.server.impl.jboss;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.AbstractJEEServer;
import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.internal.ApplicationModelImpl;

public class JBossServerImpl extends AbstractJEEServer {
	public JBossServerImpl(JBossEnv env) {
		super(env);
	}

	public void deploy(String appName, File earwar, ITargetModel target, Properties properties) throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) getEnv();
		String jbossHome = env.getJbossHome();
		String serverName = env.getServerName();
		File dest = new File(jbossHome + File.separator + "server" + File.separator + serverName + File.separator + "deploy");

		if (earwar.isDirectory())
			dest = new File(dest, earwar.getName());
		try {
			FileUtils.copy(earwar, dest, null, null);
		} catch (OperationException e) {
			throw new JEEServerOperationException(e);
		}
	}

	public IApplicationModel[] getApplications() throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) getEnv();
		String jbossHome = env.getJbossHome();
		String serverName = env.getServerName();
		File dest = new File(jbossHome + File.separator + "server" + File.separator + serverName + File.separator + "deploy");
		File[] appFiles = dest.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String name = pathname.getName().toLowerCase();
				if ((name.endsWith(".ear")) || (name.endsWith(".war"))) {
					return true;
				}
				return false;
			}
		});
		if (appFiles == null) {
			return new IApplicationModel[0];
		}

		List<IApplicationModel> list = new ArrayList<IApplicationModel>(appFiles.length);
		for (int i = 0; i < appFiles.length; i++) {
			File file = appFiles[i];
			String appName = file.getName();
			appName = appName.substring(0, appName.length() - 4);
			ApplicationModelImpl modelImpl = new ApplicationModelImpl(appName);
			list.add(modelImpl);
		}
		return (IApplicationModel[]) list.toArray(new IApplicationModel[list.size()]);
	}

	public void undeploy(String appName) throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) getEnv();
		String jbossHome = env.getJbossHome();
		String serverName = env.getServerName();
		File dest = new File(jbossHome + File.separator + "server" + File.separator + serverName + File.separator + "deploy");
		File app = new File(dest, appName + ".ear");
		if (!app.exists()) {
			app = new File(dest, appName + ".war");
		}
		if (app.exists())
			try {
				FileUtils.delete(app, null, null);
			} catch (OperationException e) {
				throw new JEEServerOperationException(e);
			}
	}
}