package com.bosssoft.platform.installer.jee.server.impl.tomcat;

import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.jee.server.DeployType;
import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

class TomcatHost {
	private File appBase;
	private File configDir;
	private String hostName;
	private DeployType deployType=DeployType.DIRECTORY;

	public TomcatHost(String tomcatHome, Element host) {
		init(tomcatHome, host);
	}
	
	public TomcatHost(String tomcatHome, Element host,DeployType deployType) {
		if(deployType!=null){
			this.deployType=deployType;
		}
		init(tomcatHome, host);
	}

	
	private void init(String tomcatHome, Element host) {
		String path = host.getAttribute("appBase");
		File file = new File(path);
		if (!file.isAbsolute())
			file = new File(tomcatHome, path);
		try {
			this.appBase = file.getCanonicalFile();
		} catch (IOException e) {
			this.appBase = file;
		}

		this.hostName = host.getAttribute("name");
		String parentName = ((Element) host.getParentNode()).getAttribute("name");
		this.configDir = new File(tomcatHome, "conf" + File.separator + parentName + File.separator + this.hostName);
	}

	public IApplicationModel[] getApplications() {
		List<IApplicationModel> list = new ArrayList<IApplicationModel>();

		Collections.addAll(list, computeBaseApps());

		Collections.addAll(list, computeConfApps());

		return list.toArray(new IApplicationModel[list.size()]);
	}

	public void deploy(String appName, File appFile) throws IOException {
		File dest = getAppBaseDir();

		File file = new File(dest, appFile.getName());
		if (file.exists()) {
			if (!appFile.isDirectory()) {
				boolean endWar = appName.toLowerCase().endsWith(".war");
				if (endWar) {
					appName = appName.substring(0, appName.length() - 4);
				}
			}
			throw new IOException("Could not be deployed the application(\"" + appName + "\"), " + "have a duplicate application!" + file.getAbsolutePath());
		}

		File destAppDir = new File(dest, appName);
		try {
			if (appFile.isDirectory()) {
				FileUtils.copy(appFile, destAppDir, null, null);
			} else{
				if(DeployType.WAR.toString().equals(deployType.toString())){
					FileUtils.copy(appFile, new File(dest,appFile.getName()), null, null);
				}else{
					FileUtils.unzip(appFile, destAppDir, null, null);
				}
			}
				
		} catch (OperationException e) {
			String message = e.getMessage();
			if ((StringUtils.isEmpty(message)) && (e.getCause() != null)) {
				message = e.getCause().getMessage();
			}
			throw new IOException(message);
		}
	}

	public void undeploy(String appName) throws IOException {
		IApplicationModel[] apps = getApplications();
		for (int i = 0; i < apps.length; i++) {
			TomcatAppModel model = (TomcatAppModel) apps[i];

			File file = model.getConfigFile();
			if ((file != null) && (file.exists())) {
				file.delete();
			}
			String localFile = model.getDeployPath();
			file = new File(localFile);
			if (file.exists()) {
				if (file.isDirectory())
					try {
						FileUtils.delete(file, null, null);
					} catch (OperationException e) {
						String message = e.getMessage();
						if ((StringUtils.isEmpty(message)) && (e.getCause() != null)) {
							message = e.getCause().getMessage();
						}
						throw new IOException(message);
					}
				else
					file.delete();
			}
		}
	}

	private IApplicationModel[] computeBaseApps() {
		File baseDir = getAppBaseDir();
		File[] appFiles = baseDir.listFiles();
		List<IApplicationModel> list = new ArrayList<IApplicationModel>();
		for (int i = 0; i < appFiles.length; i++) {
			File file = appFiles[i];
			if (file.exists()) {
				TomcatAppModel appModel = null;
				String fileName = file.getName();

				if (file.isDirectory()) {
					String appName = fixAppName(fileName);
					appModel = new TomcatAppModel(appName);
				} else if (fileName.toLowerCase().endsWith(".war")) {
					String appName = fileName.substring(0, fileName.length() - 4);
					appName = fixAppName(appName);
					appModel = new TomcatAppModel(appName);
				}

				if ((appModel != null) && (!list.contains(appModel))) {
					appModel.setDeployPath(file.getAbsolutePath());

					TargetModelImpl targetModel = new TargetModelImpl(getHostName());
					appModel.setTargetModel(targetModel);

					list.add(appModel);
				}
			}
		}
		return list.toArray(new IApplicationModel[list.size()]);
	}

	private IApplicationModel[] computeConfApps() {
		File baseDir = getConfBaseDir();
		File[] configs = baseDir.listFiles();

		List<IApplicationModel> list = new ArrayList<IApplicationModel>();

		for (int i = 0; i < configs.length; i++) {
			File file = configs[i];
			String name = file.getName();

			if (!name.equalsIgnoreCase("META-INF")) {
				if (!name.equalsIgnoreCase("WEB-INF")) {
					if (name.toLowerCase().endsWith(".xml")) {
						String nameTmp = name.substring(0, name.length() - 4);
						nameTmp = fixAppName(nameTmp);
						TomcatAppModel appModel = new TomcatAppModel(nameTmp);

						appModel.setDeployPath(file.getAbsolutePath());

						if (!list.contains(appModel)) {
							TargetModelImpl targetModel = new TargetModelImpl(getHostName());
							appModel.setTargetModel(targetModel);
							list.add(appModel);
						}
					}
				}
			}
		}
		return list.toArray(new IApplicationModel[list.size()]);
	}

	private String fixAppName(String name) {
		if (name.equals("ROOT")) {
			name = "";
		}
		return name;
	}

	public String getHostName() {
		return this.hostName;
	}

	public File getAppBaseDir() {
		return this.appBase;
	}

	public File getConfBaseDir() {
		return this.configDir;
	}

	public DeployType getDeployType() {
		return deployType;
	}
}