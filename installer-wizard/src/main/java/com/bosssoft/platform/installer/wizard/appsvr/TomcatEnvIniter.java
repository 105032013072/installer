package com.bosssoft.platform.installer.wizard.appsvr;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.bosssoft.platform.installer.io.xml.XmlFile;

public class TomcatEnvIniter implements IJeeServerEnvIniter {
	private String tomcatHome;
	public static final String ENCODING = "UTF-8";
	public static final String ENCODING_ATTR = "URIEncoding";

	public TomcatEnvIniter(String tomcatHome) {
		this.tomcatHome = tomcatHome;
	}

	public boolean excute() {
		try {
			return doConfig();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return false;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean doConfig() throws IOException, XPathExpressionException, TransformerException {
		String serverConf = getServerConf();
		File serverConfFile = new File(serverConf);
		XmlFile xmlFile = new XmlFile(serverConfFile);
		NodeList nodeList = xmlFile.findNodes("/Server/Service/Connector");

		int nodeLen = nodeList.getLength();

		boolean isChanged = false;
		for (int i = 0; i < nodeLen; i++) {
			Element element = (Element) nodeList.item(i);
			Attr attr = element.getAttributeNode("URIEncoding");
			if (attr == null) {
				attr = element.getOwnerDocument().createAttribute("URIEncoding");
			}
			String value = attr.getValue();
			if ((value == null) || (!value.toLowerCase().equals("UTF-8".toLowerCase()))) {
				attr.setValue("UTF-8");
				element.setAttributeNode(attr);
				isChanged = true;
			}
		}
		if (isChanged) {
			xmlFile.save();
		}

		return true;
	}

	private String getServerConf() {
		return this.tomcatHome + File.separator + "conf" + File.separator + "server.xml";
	}

	public String getTomcatHome() {
		return this.tomcatHome;
	}
}