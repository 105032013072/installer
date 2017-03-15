package com.bosssoft.platform.installer.jee.server.impl.weblogic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.AbstractJEEServer;
import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.ITargetModel;
import com.bosssoft.platform.installer.jee.server.internal.ApplicationModelImpl;
import com.bosssoft.platform.installer.jee.server.internal.util.Util;

public class WeblogicServerImpl extends AbstractJEEServer {
	private WeblogicEnv weblogicEnv;
	public static final String STAGE_MODEL = "STAGE_MODEL";

	public WeblogicServerImpl(WeblogicEnv weblogicEnv) {
		super(weblogicEnv);
		this.weblogicEnv = weblogicEnv;
	}

	public void start(boolean cluster, String _jvmArgs) throws JEEServerOperationException {
		run((WeblogicEnv) getEnv(), "start", _jvmArgs);
	}

	public void stop(boolean cluster) throws JEEServerOperationException {
		run((WeblogicEnv) getEnv(), "stop", null);
	}

	public boolean isStarted() throws JEEServerOperationException {
		try {
			run((WeblogicEnv) getEnv(), "serverStatus", null);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void deploy(String appName, File earwar, ITargetModel target, Properties properties) throws JEEServerOperationException {
		run(appName, earwar, this.weblogicEnv, "deploy", target, properties);
	}

	public IApplicationModel[] getApplications() throws JEEServerOperationException {
		WeblogicEnv env = (WeblogicEnv) getEnv();
		String domainHome = env.getDomainHome();
		File config = new File(domainHome, "config/config.xml");
		List<IApplicationModel> list = new ArrayList<IApplicationModel>();
		try {
			XmlFile xmlFile = new XmlFile(config);
			NodeList nodeList = xmlFile.findNodes("/domain/app-deployment");
			int len = nodeList.getLength();

			for (int i = 0; i < len; i++) {
				Node element = nodeList.item(i);
				NodeList nodeList2 = element.getChildNodes();

				for (int j = 0; j < nodeList2.getLength(); j++) {
					Node tempNode = nodeList2.item(j);
					String nodeName = tempNode.getNodeName();
					if (nodeName.equals("name")) {
						String nodevalue = tempNode.getTextContent();
						ApplicationModelImpl app = new ApplicationModelImpl(nodevalue);
						list.add(app);
					}
				}
			}
		} catch (Exception localException) {
		}

		return (IApplicationModel[]) list.toArray(new IApplicationModel[list.size()]);
	}

	public void undeploy(String appName) throws JEEServerOperationException {
		run(appName, null, this.weblogicEnv, "undeploy", null, null);
	}

	private void run(String appName, File earwar, WeblogicEnv env, String operation, ITargetModel target, Properties args) throws JEEServerOperationException {
		WlstProperties.DeployProperty properties = getDeployProperties(appName, earwar, env, operation, target, args);

		File tempDir = Util.getTempDir();
		File propertieFile = new File(tempDir, "deployProperty.properties");
		String path = propertieFile.getAbsolutePath();
		try {
			properties.save(path);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}

		String weblogicHome = env.getWeblogicHome();
		File wlstCMD = WeblogicScrptTool.getWlstCmd(weblogicHome);
		File python = WlstProperties.getDeployPython();
		RunWlstCmdArgs arg = new RunWlstCmdArgs(wlstCMD, python, propertieFile);
		try {
			WeblogicScrptTool.runWLST(arg);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
	}

	private void run(WeblogicEnv env, String operation, String _jvmArgs) throws JEEServerOperationException {
		WlstProperties.StartupProperty property = getStartupProperty(env, operation, _jvmArgs);
		File tempDir = Util.getTempDir();
		File propertieFile = new File(tempDir, "startup.properties");
		String path = propertieFile.getAbsolutePath();
		try {
			property.save(path);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
		String weblogicHome = env.getWeblogicHome();
		File wlstCMD = WeblogicScrptTool.getWlstCmd(weblogicHome);
		File python = WlstProperties.getStartupPython();
		RunWlstCmdArgs arg = new RunWlstCmdArgs(wlstCMD, python, propertieFile);
		try {
			WeblogicScrptTool.runWLST(arg);
		} catch (IOException e) {
			throw new JEEServerOperationException(e);
		}
	}

	private WlstProperties.StartupProperty getStartupProperty(WeblogicEnv env, String operation, String _jvmArgs) {
		WlstProperties.StartupProperty property = new WlstProperties.StartupProperty();

		String userName = env.getLoginName();
		String password = env.getPassword();
		String domainHome = env.getDomainHome();
		domainHome = domainHome.trim();
		domainHome = domainHome.replace('\\', '/');

		if (domainHome.endsWith("/")) {
			domainHome = domainHome.substring(0, domainHome.length() - 1);
		}
		String domainName = domainHome.substring(domainHome.lastIndexOf('/') + 1);

		String resultFile = WeblogicScrptTool.getResultLogFile().getAbsolutePath();
		String host = env.getHost();
		String port = env.getPort();
		String url = "t3://" + host + ":" + port;

		property._operations = operation;

		property.adminServerURL = url;
		property.domainDir = domainHome;
		property.domainHome = domainHome;
		property.domainName = domainName;

		property.userName = userName;
		property.passWord = password;
		property.resultFile = resultFile;

		property._jvmArgs = _jvmArgs;
		return property;
	}

	private WlstProperties.DeployProperty getDeployProperties(String appName, File earwar, WeblogicEnv env, String operation, ITargetModel target, Properties args) {
		String userName = env.getLoginName();
		String password = env.getPassword();

		String host = env.getHost();
		String port = env.getPort();
		String url = "t3://" + host + ":" + port;

		String resultFile = WeblogicScrptTool.getResultLogFile().getAbsolutePath();

		WlstProperties.DeployProperty property = new WlstProperties.DeployProperty();

		property._operations = operation;
		property.adminServerURL = url;
		property.appName = appName;
		property.earPath = earwar.getAbsolutePath();
		property.resultFile = resultFile;

		StringBuilder servers = new StringBuilder();
		StringBuilder clusters = new StringBuilder();
		addTargets(servers, clusters, target);

		property.targetClusters = clusters.toString();
		property.targetServers = servers.toString();

		property.userName = userName;
		property.passWord = password;
		if (args != null) {
			String value = args.getProperty("STAGE_MODEL");
			property._stageModel = value;
		}
		return property;
	}

	private void addTargets(StringBuilder servers, StringBuilder clusters, ITargetModel target) {
		boolean isCluster = target.isCluster();
		String name = target.getName();
		if (isCluster) {
			if ((name != null) && (!name.trim().equals(""))) {
				if (clusters.length() > 0) {
					clusters.append(',');
				}
				clusters.append(name);
			}
		} else if ((name != null) && (!name.trim().equals(""))) {
			if (clusters.length() > 0) {
				clusters.append(',');
			}
			servers.append(name);
		}
		ITargetModel[] children = target.getChildren();
		for (int i = 0; i < children.length; i++) {
			ITargetModel model = children[i];
			addTargets(servers, clusters, model);
		}
	}
}