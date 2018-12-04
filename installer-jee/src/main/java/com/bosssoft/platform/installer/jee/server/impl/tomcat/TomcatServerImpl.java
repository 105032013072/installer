package com.bosssoft.platform.installer.jee.server.impl.tomcat;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.AbstractJEEServer;
import com.bosssoft.platform.installer.jee.server.DeployType;
import com.bosssoft.platform.installer.jee.server.IApplicationModel;
import com.bosssoft.platform.installer.jee.server.ITargetModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TomcatServerImpl extends AbstractJEEServer {
	public static final String TOMCAT_HOME = "";
	private String tomcatHome;
	private TomcatHost[] hosts = null;
	private DeployType deployType=DeployType.DIRECTORY;

	public TomcatServerImpl(TomcatEnv env) throws IOException {
		super(env);
		this.tomcatHome = env.getTomcatHome();

		setEnv(env);

		init();
	}
	
	public TomcatServerImpl(TomcatEnv env,DeployType deployType) throws IOException{
		super(env);
		if(deployType!=null){
			this.deployType=deployType;
		}
		
		this.tomcatHome = env.getTomcatHome();

		setEnv(env);

		init();
	}
	

	public void deploy(String appName, File earwar, ITargetModel target, Properties properties) throws JEEServerOperationException {
		String targetName = null;
		if (target != null)
			targetName = target.getName();
		try {
			for (int i = 0; i < this.hosts.length; i++) {
				TomcatHost host = this.hosts[i];
				if ((targetName == null) || ((targetName != null) && (targetName.equals(host.getHostName())))) {
					host.deploy(appName, earwar);
					break;
				}
			}
		} catch (IOException e) {
			throw new JEEServerOperationException("deploy faild!", e);
		}
	}

	public void undeploy(String appName) throws JEEServerOperationException {
		try {
			for (int i = 0; i < this.hosts.length; i++) {
				TomcatHost host = this.hosts[i];
				host.undeploy(appName);
			}
		} catch (IOException e) {
			throw new JEEServerOperationException("unDeploy faild!", e);
		}
	}

	public IApplicationModel[] getApplications() {
		List list = new ArrayList();
		for (int i = 0; i < this.hosts.length; i++) {
			TomcatHost host = this.hosts[i];
			IApplicationModel[] apps = host.getApplications();
			Collections.addAll(list, apps);
		}
		return (IApplicationModel[]) list.toArray(new IApplicationModel[list.size()]);
	}

	public boolean existApp(String appName) {
		assert (appName == null);
		IApplicationModel[] apps = getApplications();
		for (int i = 0; i < apps.length; i++) {
			IApplicationModel model = apps[i];
			if (appName.equals(model.getAppName())) {
				return true;
			}
		}
		return false;
	}

	private void init() {
		String serverConf = getServerConf();
		try {
			XmlFile xmlFile = new XmlFile(new File(serverConf));
			NodeList nodeList = xmlFile.findNodes("//Host");
			int len = nodeList.getLength();
			List<TomcatHost> hostList = new ArrayList<TomcatHost>();
			for (int i = 0; i < len; i++) {
				Element element = (Element) nodeList.item(i);
				TomcatHost host = new TomcatHost(this.tomcatHome, element,deployType);
				hostList.add(host);
			}
			this.hosts =  hostList.toArray(new TomcatHost[hostList.size()]);
		} catch (IOException e) {
			throw new RuntimeException("Could not be parse the server config!" + serverConf, e);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("Could not be find hosts!" + serverConf, e);
		}
	}

	private String getServerConf() {
		return this.tomcatHome + File.separator + "conf" + File.separator + "server.xml";
	}
}