package com.bosssoft.platform.installer.jee.server.impl.jboss;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.IJEEServer;
import com.bosssoft.platform.installer.jee.server.config.AbstractJMSConfig;

public class JBossJMSConfig extends AbstractJMSConfig {
	public void config(IJEEServer jeeServer) throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) jeeServer.getEnv();
		String jbossHome = env.getJbossHome();
		String serverName = env.getServerName();

		File deployDir = getDeployDir(jbossHome, serverName);
		File serviceXml = new File(deployDir, "jms" + File.separator + "jbossmq-destinations-service.xml");

		AbstractJMSConfig.NameJndiModel[] queueModels = getQueueModels();
		try {
			XmlFile xmlFile = new XmlFile(serviceXml);
			for (int i = 0; i < queueModels.length; i++) {
				AbstractJMSConfig.NameJndiModel model = queueModels[i];
				String queueJndi = model.getJndi();
				addQueue(queueJndi, xmlFile);
			}
			xmlFile.save();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		} catch (XPathExpressionException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		} catch (SAXException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		} catch (TransformerException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		}
	}

	private void addQueue(String queueJndi, XmlFile xmlFile) throws XPathExpressionException, SAXException, IOException {
		String mqDestXql = "/server/mbean[@name=\"jboss.mq.destination:service=Queue,name=" + queueJndi + "\"]";
		Element mbean = (Element) xmlFile.findNode(mqDestXql);

		if (mbean == null) {
			String xmlStr = "<mbean code=\"org.jboss.mq.server.jmx.Queue\" name=\"jboss.mq.destination:service=Queue,name=" + queueJndi + "\">\n"
					+ "<depends optional-attribute-name=\"DestinationManager\">" + "jboss.mq:service=DestinationManager" + "</depends>\n" + "</mbean>\n";
			xmlFile.addNode("/server", xmlStr);
		} else {
			Element depends = (Element) xmlFile.findNode(mqDestXql + "/depends[@optional-attribute-name=\"DestinationManager\"]");
			String xmlStr = "<depends optional-attribute-name=\"DestinationManager\">jboss.mq:service=DestinationManager</depends>";
			if (depends == null) {
				xmlFile.addNode(mqDestXql, xmlStr);
			} else {
				mbean.removeChild(depends);
				xmlFile.addNode(mqDestXql, xmlStr);
			}
		}
	}

	public void unconfig(IJEEServer jeeServer) throws JEEServerOperationException {
		JBossEnv env = (JBossEnv) jeeServer.getEnv();
		String jbossHome = env.getJbossHome();
		String serverName = env.getServerName();

		File deployDir = getDeployDir(jbossHome, serverName);
		File serviceXml = new File(deployDir, "jms" + File.separator + "jbossmq-destinations-service.xml");

		AbstractJMSConfig.NameJndiModel[] queueModels = getQueueModels();
		try {
			XmlFile xmlFile = new XmlFile(serviceXml);
			for (int i = 0; i < queueModels.length; i++) {
				AbstractJMSConfig.NameJndiModel model = queueModels[i];
				removeQueue(model.getJndi(), xmlFile);
			}
			xmlFile.save();
		} catch (IOException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		} catch (XPathExpressionException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		} catch (TransformerException e) {
			throw new JEEServerOperationException(e.getMessage() + "[" + serviceXml.getAbsolutePath() + "]", e);
		}
	}

	private void removeQueue(String queueJndi, XmlFile xmlFile) throws XPathExpressionException {
		String xql = "/server/mbean[@name=\"jboss.mq.destination:service=Queue,name=" + queueJndi + "\"]";
		Element mbean = (Element) xmlFile.findNode(xql);
		if (mbean != null)
			mbean.getParentNode().removeChild(mbean);
	}

	private File getDeployDir(String jbossHome, String serverName) {
		String path = "server" + File.separator + serverName + File.separator + "deploy";
		File file = new File(jbossHome, path);
		return file;
	}
}