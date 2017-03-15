package com.primeton.installer.eos.action;

import com.primeton.install.IContext;
import com.primeton.install.InstallException;
import com.primeton.install.action.IAction;
import com.primeton.install.io.xml.XmlFile;
import com.primeton.install.util.ExpressionParser;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class UpdateDomainConfig
  implements IAction
{
  transient Logger logger = Logger.getLogger(getClass());

  public void execute(IContext context, Map parameters) throws InstallException { String ip = parameters.get("IP").toString();
    ip = ExpressionParser.parseString(ip);

    String port = parameters.get("PORT").toString();
    port = ExpressionParser.parseString(port);

    String appName = parameters.get("APP_NAME").toString();
    appName = ExpressionParser.parseString(appName);

    String domainConfig = parameters.get("DOMAIN_CONF_PATH").toString();
    domainConfig = ExpressionParser.parseString(domainConfig);

    String appSrvType = context.getStringValue("APP_SERVER_NAME").toLowerCase();
    try
    {
      addServer2Domain(domainConfig, ip, port, appName, appSrvType);
    } catch (Exception e) {
      this.logger.error("Modify Governor domain.xml failed!", e);
    } }

  protected void addServer2Domain(String configPath, String ip, String port, String appName, String appSrvType) throws IOException, XPathExpressionException, SAXException, TransformerException
  {
    XmlFile xmlFile = new XmlFile(new File(configPath));

    String xql = "//domain";
    xmlFile.setNodeAttribute(xql, "timestamp", System.currentTimeMillis() + "");
    xmlFile.setNodeAttribute(xql, "adminServerIP", ip);
    xmlFile.setNodeAttribute(xql, "adminServerPort", "6299");
    xmlFile.setNodeAttribute(xql, "adminAppName", "governor");

    xql = "//domain/servers";
    StringBuffer xmlStr = new StringBuffer();
    xmlStr.append("  <server ip=\"").append(ip).append("\" jndiPort=\"0\" adminPort=\"").append(port).append("\" appName=\"").append(appName).append("\" isSynchronized=\"true\" name=\"server_").append(ip).append("_").append(port).append("\" type=\"").append(appSrvType).append("\"/>");

    xmlFile.addNode(xql, xmlStr.toString());
    xmlFile.save();
  }

  public void rollback(IContext arg0, Map arg1) throws InstallException
  {
  }

  public static void main(String[] args)
  {
    UpdateDomainConfig tool = new UpdateDomainConfig();
    String path = "E:\\primeton\\eos2010\\apache-tomcat-5.5.20\\webapps\\governor\\WEB-INF\\_srv\\domain\\domain.xml";
    String port = "6200";
    String ip = "192.168.0.11";
    String appName = "default";
    try {
      tool.addServer2Domain(path, ip, port, appName, "tomcat");
    }
    catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (SAXException e) {
      e.printStackTrace();
    }
    catch (TransformerException e) {
      e.printStackTrace();
    }
  }
}